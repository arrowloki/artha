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
import com.artha.data.repository.TransactionRepository
import com.artha.data.worker.RecurringTransactionWorker
import com.artha.database.ArthaDatabase
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetEntity
import com.artha.database.entity.GoalEntity
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

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize encrypted Room database
        database = ArthaDatabase.getInstance(this, useEncryption = true)

        // Initialize repositories
        accountRepository = AccountRepository(database)
        categoryRepository = CategoryRepository(database)
        transactionRepository = TransactionRepository(database)
        preferencesRepository = UserPreferencesRepository(this)
        budgetRepository = BudgetRepository(database)
        billSubscriptionRepository = BillSubscriptionRepository(database)
        goalRepository = GoalRepository(database)
        recurringRuleRepository = RecurringRuleRepository(database)

        // Schedule WorkManager for recurring entries
        val recurringWork = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "artha_recurring_transactions",
            ExistingPeriodicWorkPolicy.KEEP,
            recurringWork
        )

        // Asynchronously seed default categories, starter accounts & planning if empty
        CoroutineScope(Dispatchers.IO).launch {
            categoryRepository.seedDefaultCategoriesIfEmpty()
            seedStarterAccountsIfEmpty()
            seedStarterPlanningIfEmpty()
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

    companion object {
        lateinit var instance: ArthaApp
            private set
    }
}
