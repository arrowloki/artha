package com.artha.feature.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.BillSubscriptionRepository
import com.artha.data.repository.BudgetRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.GoalRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetEntity
import com.artha.database.entity.CategoryEntity
import com.artha.database.entity.GoalEntity
import com.artha.database.entity.TransactionEntity
import com.artha.domain.BudgetConsumption
import com.artha.domain.BudgetEngine
import com.artha.domain.RecurringScheduleCalculator
import com.artha.domain.SubscriptionCalculator
import com.artha.domain.SubscriptionRenewalStatus
import com.artha.domain.SubscriptionSummary
import com.artha.model.BudgetPeriod
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.RecurrenceFrequency
import com.artha.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PlanningViewModel(
    private val budgetRepository: BudgetRepository,
    private val billSubscriptionRepository: BillSubscriptionRepository,
    private val goalRepository: GoalRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val budgetEngine = BudgetEngine()
    private val subscriptionCalculator = SubscriptionCalculator()
    private val scheduleCalculator = RecurringScheduleCalculator()

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getActiveAccountsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.getActiveCategoriesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = budgetRepository.getAllBudgetsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val daysRemainingInMonth = budgetEngine.getDaysRemainingInMonth()

    // Monthly aggregates & budget consumptions
    private val currentYearMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    val budgetsWithConsumption: StateFlow<List<BudgetConsumption>> = allBudgets.map { budgets ->
        val list = mutableListOf<BudgetConsumption>()
        for (b in budgets) {
            // Aggregate spent in this month
            val spentMinor = budgetRepository.getTotalMonthlyExpense(currentYearMonth, b.currency)
            val consumption = budgetEngine.calculateConsumption(
                budget = b,
                spentMinor = spentMinor,
                daysRemainingInPeriod = daysRemainingInMonth
            )
            list.add(consumption)
        }
        list
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Subscriptions & Bills
    val allBills: StateFlow<List<BillSubscriptionEntity>> = billSubscriptionRepository.getAllBillsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val subscriptionSummary: StateFlow<SubscriptionSummary> = allBills.map { subs ->
        subscriptionCalculator.calculateSummary(subs)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SubscriptionSummary(0L, 0L, 0, 0, 0, "INR")
    )

    val subscriptionRenewalStatuses: StateFlow<List<SubscriptionRenewalStatus>> = allBills.map { subs ->
        subs.filter { it.isActive }.map { sub ->
            subscriptionCalculator.evaluateRenewalStatus(sub)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Goals & Sinking Funds
    val goals: StateFlow<List<GoalEntity>> = goalRepository.getAllGoalsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Actions: Budgets
    fun createBudget(
        name: String,
        amountMinor: Long,
        currency: String = "INR",
        periodType: BudgetPeriod = BudgetPeriod.MONTHLY,
        alertThresholdPercentage: Int = 80,
        rolloverEnabled: Boolean = false,
        categoryIds: List<String> = emptyList()
    ) {
        val now = System.currentTimeMillis()
        val budget = BudgetEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            amountMinor = amountMinor,
            currency = currency,
            periodType = periodType,
            startDate = now,
            endDate = now + (30L * 24 * 60 * 60 * 1000),
            rolloverEnabled = rolloverEnabled,
            alertThresholdPercentage = alertThresholdPercentage
        )
        viewModelScope.launch {
            budgetRepository.createOrUpdateBudget(budget, categoryIds)
        }
    }

    fun deleteBudget(id: String) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(id)
        }
    }

    // Actions: Subscriptions & Bills
    fun createSubscription(
        name: String,
        amountMinor: Long,
        currency: String = "INR",
        frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
        accountId: String,
        dueDay: Int = 1,
        trialEndDate: Long? = null,
        cancellationUrl: String? = null,
        isAutoPaid: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        val nextDue = scheduleCalculator.calculateNextOccurrence(now, frequency)
        val bill = BillSubscriptionEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            amountMinor = amountMinor,
            currency = currency,
            accountId = accountId,
            frequency = frequency,
            dueDay = dueDay,
            trialEndDate = trialEndDate,
            cancellationUrl = cancellationUrl?.trim(),
            isAutoPaid = isAutoPaid,
            nextDueDate = nextDue
        )
        viewModelScope.launch {
            billSubscriptionRepository.saveBill(bill)
        }
    }

    fun markSubscriptionPaid(sub: BillSubscriptionEntity) {
        val now = System.currentTimeMillis()
        val nextDue = scheduleCalculator.calculateNextOccurrence(sub.nextDueDate, sub.frequency)

        viewModelScope.launch {
            // 1. Record expense transaction
            val tx = TransactionEntity(
                id = UUID.randomUUID().toString(),
                accountId = sub.accountId,
                amountMinor = sub.amountMinor,
                currency = sub.currency,
                type = TransactionType.EXPENSE,
                paymentMethod = PaymentMethod.NET_BANKING,
                timestamp = now,
                note = "Subscription: ${sub.name}"
            )
            transactionRepository.recordTransaction(tx, "Subscription ${sub.name}")

            // 2. Advance due date
            billSubscriptionRepository.markBillPaid(sub.id, now, nextDue)
        }
    }

    fun deleteSubscription(id: String) {
        viewModelScope.launch {
            billSubscriptionRepository.deleteBill(id)
        }
    }

    // Actions: Goals & Sinking Funds
    fun createGoal(
        name: String,
        targetAmountMinor: Long,
        currency: String = "INR",
        targetDate: Long,
        accountId: String? = null
    ) {
        val goal = GoalEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            targetAmountMinor = targetAmountMinor,
            savedAmountMinor = 0L,
            currency = currency,
            targetDate = targetDate,
            accountId = accountId
        )
        viewModelScope.launch {
            goalRepository.saveGoal(goal)
        }
    }

    fun contributeToGoal(id: String, deltaMinor: Long) {
        viewModelScope.launch {
            goalRepository.contributeToGoal(id, deltaMinor)
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            goalRepository.deleteGoal(id)
        }
    }
}
