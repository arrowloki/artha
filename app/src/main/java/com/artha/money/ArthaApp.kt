package com.artha.money

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.artha.data.preferences.UserPreferencesRepository
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.BillSubscriptionRepository
import com.artha.data.repository.BudgetRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.GoalRepository
import com.artha.data.repository.RecurringRuleRepository
import com.artha.data.repository.RuleRepository
import com.artha.data.repository.TransactionRepository
import com.artha.data.worker.RecurringTransactionWorker
import com.artha.database.ArthaDatabase
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetEntity
import com.artha.database.entity.GoalEntity
import com.artha.database.entity.RuleEntity
import com.artha.data.repository.DebtLoanRepository
import com.artha.data.repository.DataTransferRepository
import com.artha.database.entity.DebtLoanEntity
import com.artha.model.DebtType
import com.artha.model.AccountType
import com.artha.model.BudgetPeriod
import com.artha.model.RecurrenceFrequency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

class ArthaApp : Application() {

    lateinit var database: ArthaDatabase
        private set

    lateinit var accountRepository: AccountRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var budgetRepository: BudgetRepository
        private set

    lateinit var billSubscriptionRepository: BillSubscriptionRepository
        private set

    lateinit var goalRepository: GoalRepository
        private set

    lateinit var recurringRuleRepository: RecurringRuleRepository
        private set

    lateinit var debtLoanRepository: DebtLoanRepository
        private set

    lateinit var ruleRepository: RuleRepository
        private set

    lateinit var dataTransferRepository: DataTransferRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Install uncaught exception handler to log fatal issues
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("ArthaCrash", "Fatal exception in thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Initialize Room database with fallback
        try {
            database = ArthaDatabase.getInstance(this, useEncryption = true)
        } catch (e: Throwable) {
            android.util.Log.e("ArthaApp", "Fatal error initializing encrypted database, falling back to standard Room", e)
            database = ArthaDatabase.getInstance(this, useEncryption = false)
        }

        // Initialize repositories
        preferencesRepository = UserPreferencesRepository(this)
        accountRepository = AccountRepository(database)
        categoryRepository = CategoryRepository(database)
        transactionRepository = TransactionRepository(database)
        budgetRepository = BudgetRepository(database)
        billSubscriptionRepository = BillSubscriptionRepository(database)
        goalRepository = GoalRepository(database)
        recurringRuleRepository = RecurringRuleRepository(database)
        debtLoanRepository = DebtLoanRepository(database)
        ruleRepository = RuleRepository(database)
        dataTransferRepository = DataTransferRepository(database, transactionRepository)

        // Safely schedule WorkManager for recurring entries
        try {
            val recurringWork = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(1, TimeUnit.DAYS).build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "artha_recurring_transactions",
                ExistingPeriodicWorkPolicy.KEEP,
                recurringWork
            )
        } catch (e: Throwable) {
            android.util.Log.w("ArthaApp", "WorkManager recurring transaction scheduling failed", e)
        }

        // Asynchronously seed default categories, starter accounts & planning if empty
        CoroutineScope(Dispatchers.IO).launch {
            try {
                categoryRepository.seedDefaultCategoriesIfEmpty()
                seedStarterAccountsIfEmpty()
                seedStarterPlanningIfEmpty()
                seedStarterDebtsIfEmpty()
                seedStarterRulesIfEmpty()
            } catch (e: Throwable) {
                android.util.Log.e("ArthaApp", "Async data seeding failed", e)
            }
        }
    }

    private suspend fun seedStarterAccountsIfEmpty() {
        val accounts = accountRepository.getActiveAccountsFlow().firstOrNull()
        if (accounts.isNullOrEmpty()) {
            val starterAccounts = listOf(
                AccountEntity(
                    id = "acc_primary_bank",
                    name = "Main Savings Bank",
                    type = AccountType.BANK,
                    currency = "INR",
                    openingBalanceMinor = 4500000L, // ₹45,000.00
                    currentBalanceMinor = 4500000L,
                    colorHex = "#2196F3"
                ),
                AccountEntity(
                    id = "acc_cash_wallet",
                    name = "Physical Cash",
                    type = AccountType.CASH,
                    currency = "INR",
                    openingBalanceMinor = 350000L, // ₹3,500.00
                    currentBalanceMinor = 350000L,
                    colorHex = "#4CAF50"
                ),
                AccountEntity(
                    id = "acc_primary_card",
                    name = "Rewards Credit Card",
                    type = AccountType.CREDIT_CARD,
                    currency = "INR",
                    openingBalanceMinor = 1450000L, // ₹14,500.00 outstanding
                    currentBalanceMinor = 1450000L,
                    creditLimitMinor = 15000000L, // ₹1,50,000.00 limit
                    billingDay = 15,
                    dueDay = 5,
                    colorHex = "#E91E63"
                )
            )
            starterAccounts.forEach { accountRepository.createAccount(it) }
        }
    }

    private suspend fun seedStarterPlanningIfEmpty() {
        val budgets = budgetRepository.getAllBudgetsFlow().firstOrNull()
        if (budgets.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val sampleBudget = BudgetEntity(
                id = "b_living_monthly",
                name = "Monthly Living Expenses",
                amountMinor = 3500000L, // ₹35,000.00
                currency = "INR",
                periodType = BudgetPeriod.MONTHLY,
                startDate = now,
                endDate = now + TimeUnit.DAYS.toMillis(30),
                rolloverEnabled = true,
                alertThresholdPercentage = 80
            )
            budgetRepository.createOrUpdateBudget(sampleBudget)
        }

        val bills = billSubscriptionRepository.getAllBillsFlow().firstOrNull()
        if (bills.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val sampleBills = listOf(
                BillSubscriptionEntity(
                    id = "sub_netflix",
                    name = "Netflix Premium",
                    amountMinor = 64900L, // ₹649.00
                    currency = "INR",
                    accountId = "acc_primary_card",
                    frequency = RecurrenceFrequency.MONTHLY,
                    dueDay = 18,
                    nextDueDate = now + TimeUnit.DAYS.toMillis(4)
                ),
                BillSubscriptionEntity(
                    id = "sub_broadband",
                    name = "Fiber Internet",
                    amountMinor = 99900L, // ₹999.00
                    currency = "INR",
                    accountId = "acc_primary_bank",
                    frequency = RecurrenceFrequency.MONTHLY,
                    dueDay = 25,
                    nextDueDate = now + TimeUnit.DAYS.toMillis(11)
                )
            )
            sampleBills.forEach { billSubscriptionRepository.saveBill(it) }
        }

        val goals = goalRepository.getAllGoalsFlow().firstOrNull()
        if (goals.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val sampleGoal = GoalEntity(
                id = "g_emergency_fund",
                name = "Emergency Buffer (3 Months)",
                targetAmountMinor = 15000000L, // ₹1,50,000.00
                savedAmountMinor = 4500000L, // ₹45,000.00
                currency = "INR",
                targetDate = now + TimeUnit.DAYS.toMillis(180)
            )
            goalRepository.saveGoal(sampleGoal)
        }
    }

    private suspend fun seedStarterDebtsIfEmpty() {
        val debts = debtLoanRepository.getAllDebtsFlow().firstOrNull()
        if (debts.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val sampleDebts = listOf(
                DebtLoanEntity(
                    id = "debt_lent_1",
                    personOrInstitution = "Rahul (Colleague)",
                    type = DebtType.LENT,
                    principalMinor = 500000L, // ₹5,000.00
                    remainingMinor = 500000L,
                    startDate = now,
                    notes = "Lunch & event tickets"
                ),
                DebtLoanEntity(
                    id = "debt_loan_1",
                    personOrInstitution = "SBI Auto Loan",
                    type = DebtType.EMI_LOAN,
                    principalMinor = 80000000L, // ₹8,00,000.00
                    remainingMinor = 65000000L, // ₹6,50,000.00
                    interestRateApr = 8.75,
                    tenureMonths = 60,
                    emiAmountMinor = 1650800L, // ₹16,508.00
                    emiDueDay = 10,
                    startDate = now,
                    notes = "Car purchase"
                )
            )
            sampleDebts.forEach { debtLoanRepository.saveDebt(it) }
        }
    }

    private suspend fun seedStarterRulesIfEmpty() {
        val rules = ruleRepository.getActiveRules()
        if (rules.isEmpty()) {
            val starterRules = listOf(
                RuleEntity(
                    id = "rule_starbucks",
                    name = "Starbucks to Coffee & Dining",
                    conditionField = "MERCHANT",
                    conditionOperator = "CONTAINS",
                    conditionValue = "Starbucks",
                    actionField = "SET_CATEGORY",
                    actionValue = "cat_food_dining",
                    priority = 10
                ),
                RuleEntity(
                    id = "rule_uber",
                    name = "Uber to Transport & Fuel",
                    conditionField = "MERCHANT",
                    conditionOperator = "CONTAINS",
                    conditionValue = "Uber",
                    actionField = "SET_CATEGORY",
                    actionValue = "cat_transport",
                    priority = 20
                ),
                RuleEntity(
                    id = "rule_large_spend",
                    name = "Flag Outliers Above ₹50,000",
                    conditionField = "AMOUNT",
                    conditionOperator = "GREATER_THAN",
                    conditionValue = "5000000",
                    actionField = "EXCLUDE_FROM_BUDGET",
                    actionValue = "true",
                    priority = 30
                )
            )
            starterRules.forEach { ruleRepository.insertRule(it) }
        }
    }

    companion object {
        lateinit var instance: ArthaApp
            private set
    }
}
