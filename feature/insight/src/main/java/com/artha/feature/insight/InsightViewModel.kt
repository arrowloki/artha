package com.artha.feature.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.DebtLoanRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.entity.DebtLoanEntity
import com.artha.database.entity.DebtPaymentEntity
import com.artha.domain.CashFlowSummary
import com.artha.domain.CategoryShare
import com.artha.domain.EmiCalculator
import com.artha.domain.IncomeDelta
import com.artha.domain.OneTimeScenarioExpense
import com.artha.domain.PeriodComparison
import com.artha.domain.PrepaymentSimulationResult
import com.artha.domain.RecurringExpenseDelta
import com.artha.domain.ReportsEngine
import com.artha.domain.SimulationParams
import com.artha.domain.SimulationResult
import com.artha.domain.SipAdjustment
import com.artha.domain.WhatIfEngine
import com.artha.model.DebtType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DebtsSummary(
    val totalLentMinor: Long,
    val totalBorrowedMinor: Long,
    val totalEmiDebtMinor: Long,
    val netBalanceMinor: Long // Lent - (Borrowed + EMI)
)

data class EmiSimulationState(
    val monthlyEmiMinor: Long = 0L,
    val totalInterestMinor: Long = 0L,
    val totalPayableMinor: Long = 0L,
    val prepaymentResult: PrepaymentSimulationResult? = null
)

class InsightViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val debtLoanRepository: DebtLoanRepository
) : ViewModel() {

    private val reportsEngine = ReportsEngine()
    private val emiCalculator = EmiCalculator()

    private val currentYearMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    // 1. Monthly Aggregates & Cash Flow
    val monthlyAggregates = transactionRepository.getMonthlyAggregatesFlow("INR")
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val cashFlowSummary: StateFlow<CashFlowSummary> = monthlyAggregates.map { list ->
        val current = list.firstOrNull { it.yearMonth == currentYearMonth }
        val inflow = current?.totalIncomeMinor ?: 0L
        val outflow = current?.totalExpenseMinor ?: 0L
        reportsEngine.calculateCashFlow(inflow, outflow, "INR")
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CashFlowSummary(0L, 0L, 0L, 0, "INR"))

    // 2. Month-over-Month Comparison
    val monthOverMonthComparison: StateFlow<PeriodComparison> = monthlyAggregates.map { list ->
        val current = list.firstOrNull { it.yearMonth == currentYearMonth }?.totalExpenseMinor ?: 0L
        val prevYearMonth = getPreviousYearMonth()
        val prev = list.firstOrNull { it.yearMonth == prevYearMonth }?.totalExpenseMinor ?: 0L
        reportsEngine.comparePeriods(current, prev)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PeriodComparison(0L, 0L, 0L, 0f, false))

    // 3. Category Shares
    val categoryShares: StateFlow<List<CategoryShare>> = combine(
        transactionRepository.getCategoryMonthlyAggregatesFlow(currentYearMonth, "INR"),
        categoryRepository.getActiveCategoriesFlow()
    ) { aggs, cats ->
        val metaMap = cats.associate { it.id to Pair(it.name, it.colorHex) }
        reportsEngine.calculateCategoryShares(aggs, metaMap)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 4. Debts, Loans & IOUs
    val allDebts: StateFlow<List<DebtLoanEntity>> = debtLoanRepository.getAllDebtsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val debtsSummary: StateFlow<DebtsSummary> = allDebts.map { debts ->
        val lent = debts.filter { it.type == DebtType.LENT }.sumOf { it.remainingMinor }
        val borrowed = debts.filter { it.type == DebtType.BORROWED }.sumOf { it.remainingMinor }
        val emi = debts.filter { it.type == DebtType.EMI_LOAN }.sumOf { it.remainingMinor }
        DebtsSummary(
            totalLentMinor = lent,
            totalBorrowedMinor = borrowed,
            totalEmiDebtMinor = emi,
            netBalanceMinor = lent - (borrowed + emi)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DebtsSummary(0L, 0L, 0L, 0L))

    // 5. Interactive EMI Calculator State
    private val _principalInput = MutableStateFlow("1000000") // ₹10,00,000
    val principalInput: StateFlow<String> = _principalInput.asStateFlow()

    private val _interestRateInput = MutableStateFlow("8.5")
    val interestRateInput: StateFlow<String> = _interestRateInput.asStateFlow()

    private val _tenureMonthsInput = MutableStateFlow("120") // 10 years
    val tenureMonthsInput: StateFlow<String> = _tenureMonthsInput.asStateFlow()

    private val _extraMonthlyPaymentInput = MutableStateFlow("2000")
    val extraMonthlyPaymentInput: StateFlow<String> = _extraMonthlyPaymentInput.asStateFlow()

    private val _emiSimulationState = MutableStateFlow(EmiSimulationState())
    val emiSimulationState: StateFlow<EmiSimulationState> = _emiSimulationState.asStateFlow()

    init {
        recomputeEmi()
        recomputeWhatIf()
    }

    fun updateEmiInputs(
        principal: String = _principalInput.value,
        rate: String = _interestRateInput.value,
        tenure: String = _tenureMonthsInput.value,
        extraPayment: String = _extraMonthlyPaymentInput.value
    ) {
        _principalInput.value = principal
        _interestRateInput.value = rate
        _tenureMonthsInput.value = tenure
        _extraMonthlyPaymentInput.value = extraPayment
        recomputeEmi()
    }

    private fun recomputeEmi() {
        val p = (_principalInput.value.toDoubleOrNull() ?: 0.0) * 100.0
        val principalMinor = p.toLong()
        val rate = _interestRateInput.value.toDoubleOrNull() ?: 0.0
        val tenure = _tenureMonthsInput.value.toIntOrNull() ?: 0
        val extra = (_extraMonthlyPaymentInput.value.toDoubleOrNull() ?: 0.0) * 100.0
        val extraMinor = extra.toLong()

        if (principalMinor > 0L && tenure > 0) {
            val emi = emiCalculator.calculateMonthlyEmi(principalMinor, rate, tenure)
            val schedule = emiCalculator.generateAmortizationSchedule(principalMinor, rate, tenure)
            val totalInterest = schedule.sumOf { it.interestComponentMinor }
            val totalPayable = principalMinor + totalInterest

            val prepaymentResult = if (extraMinor > 0L) {
                emiCalculator.simulatePrepayment(
                    principalMinor = principalMinor,
                    annualInterestRatePercent = rate,
                    tenureMonths = tenure,
                    extraMonthlyPaymentMinor = extraMinor
                )
            } else null

            _emiSimulationState.value = EmiSimulationState(
                monthlyEmiMinor = emi,
                totalInterestMinor = totalInterest,
                totalPayableMinor = totalPayable,
                prepaymentResult = prepaymentResult
            )
        }
    }

    // Actions: Debts & Loans
    fun createDebt(
        personOrInstitution: String,
        type: DebtType,
        principalMinor: Long,
        interestRateApr: Double = 0.0,
        tenureMonths: Int = 0,
        emiDueDay: Int = 1,
        notes: String = ""
    ) {
        val now = System.currentTimeMillis()
        val emi = if (type == DebtType.EMI_LOAN && tenureMonths > 0) {
            emiCalculator.calculateMonthlyEmi(principalMinor, interestRateApr, tenureMonths)
        } else 0L

        val debt = DebtLoanEntity(
            id = UUID.randomUUID().toString(),
            personOrInstitution = personOrInstitution.trim(),
            type = type,
            principalMinor = principalMinor,
            remainingMinor = principalMinor,
            interestRateApr = interestRateApr,
            tenureMonths = tenureMonths,
            emiAmountMinor = emi,
            emiDueDay = emiDueDay,
            startDate = now,
            notes = notes.trim()
        )
        viewModelScope.launch {
            debtLoanRepository.saveDebt(debt)
        }
    }

    fun recordDebtPayment(debtId: String, amountMinor: Long) {
        val payment = DebtPaymentEntity(
            id = UUID.randomUUID().toString(),
            debtId = debtId,
            paymentDate = System.currentTimeMillis(),
            amountMinor = amountMinor,
            principalComponentMinor = amountMinor,
            interestComponentMinor = 0L
        )
        viewModelScope.launch {
            debtLoanRepository.recordPayment(payment)
        }
    }

    // 6. What-If Financial Scenario Simulator
    private val _selectedScenario = MutableStateFlow(0) // 0: Car, 1: Subscriptions+SIP, 2: Salary Hike
    val selectedScenario: StateFlow<Int> = _selectedScenario.asStateFlow()

    private val _whatIfResult = MutableStateFlow<SimulationResult?>(null)
    val whatIfResult: StateFlow<SimulationResult?> = _whatIfResult.asStateFlow()

    fun selectScenario(index: Int) {
        _selectedScenario.value = index
        recomputeWhatIf()
    }

    private fun recomputeWhatIf() {
        val params = when (_selectedScenario.value) {
            0 -> {
                // Buy 15L Car
                SimulationParams(
                    initialNetWorthMinor = 2_000_000_00L, // ₹20 Lakhs
                    initialLiquidSavingsMinor = 500_000_00L, // ₹5 Lakhs
                    monthlyIncomeMinor = 120_000_00L, // ₹1.2 Lakhs
                    monthlyExpenseMinor = 50_000_00L, // ₹50k
                    annualInvestmentReturnRatePercent = 11.0,
                    oneTimeExpenses = listOf(
                        OneTimeScenarioExpense(
                            title = "Buy ₹15L Car",
                            totalAmountMinor = 1_500_000_00L,
                            monthOffset = 1,
                            downPaymentMinor = 300_000_00L,
                            loanTenureMonths = 48,
                            loanInterestRateAnnual = 9.0
                        )
                    )
                )
            }
            1 -> {
                // Cut Subscriptions & Boost SIP
                SimulationParams(
                    initialNetWorthMinor = 1_500_000_00L, // ₹15 Lakhs
                    initialLiquidSavingsMinor = 300_000_00L, // ₹3 Lakhs
                    monthlyIncomeMinor = 100_000_00L, // ₹1 Lakh
                    monthlyExpenseMinor = 55_000_00L, // ₹55k
                    annualInvestmentReturnRatePercent = 12.0,
                    recurringExpenseDeltas = listOf(
                        RecurringExpenseDelta(
                            title = "Cut 3 Subscriptions",
                            monthlyDeltaMinor = -1_500_00L
                        )
                    ),
                    sipAdjustments = listOf(
                        SipAdjustment(
                            title = "Additional Monthly SIP",
                            monthlySipMinor = 10_000_00L,
                            expectedReturnRateAnnual = 12.0
                        )
                    )
                )
            }
            else -> {
                // 15% Salary Hike
                SimulationParams(
                    initialNetWorthMinor = 1_800_000_00L, // ₹18 Lakhs
                    initialLiquidSavingsMinor = 400_000_00L,
                    monthlyIncomeMinor = 120_000_00L,
                    monthlyExpenseMinor = 55_000_00L,
                    annualInvestmentReturnRatePercent = 11.0,
                    incomeDeltas = listOf(
                        IncomeDelta(
                            title = "15% Salary Hike",
                            monthlyDeltaMinor = 18_000_00L
                        )
                    ),
                    sipAdjustments = listOf(
                        SipAdjustment(
                            title = "Divert 50% Hike to SIP",
                            monthlySipMinor = 9_000_00L,
                            expectedReturnRateAnnual = 12.0
                        )
                    )
                )
            }
        }
        _whatIfResult.value = WhatIfEngine.simulate(params, totalMonths = 60)
    }

    private fun getPreviousYearMonth(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        return SimpleDateFormat("yyyy-MM", Locale.US).format(cal.time)
    }
}
