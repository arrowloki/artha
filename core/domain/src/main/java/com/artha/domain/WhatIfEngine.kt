package com.artha.domain

import com.artha.model.Money
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class OneTimeScenarioExpense(
    val title: String,
    val totalAmountMinor: Long,
    val monthOffset: Int = 1, // 1-based month index when purchase happens
    val downPaymentMinor: Long = totalAmountMinor,
    val loanTenureMonths: Int = 0,
    val loanInterestRateAnnual: Double = 9.0
)

data class RecurringExpenseDelta(
    val title: String,
    val monthlyDeltaMinor: Long, // Positive = extra expense (e.g. rent hike), Negative = savings (e.g. cut subscriptions)
    val startMonth: Int = 1
)

data class IncomeDelta(
    val title: String,
    val monthlyDeltaMinor: Long, // Positive = hike/extra income
    val startMonth: Int = 1
)

data class SipAdjustment(
    val title: String,
    val monthlySipMinor: Long,
    val expectedReturnRateAnnual: Double = 12.0,
    val startMonth: Int = 1
)

data class SimulationParams(
    val initialNetWorthMinor: Long,
    val initialLiquidSavingsMinor: Long,
    val monthlyIncomeMinor: Long,
    val monthlyExpenseMinor: Long,
    val annualInvestmentReturnRatePercent: Double = 10.0,
    val oneTimeExpenses: List<OneTimeScenarioExpense> = emptyList(),
    val recurringExpenseDeltas: List<RecurringExpenseDelta> = emptyList(),
    val incomeDeltas: List<IncomeDelta> = emptyList(),
    val sipAdjustments: List<SipAdjustment> = emptyList()
)

data class SimulationMonthPoint(
    val monthIndex: Int,
    val baselineNetWorthMinor: Long,
    val simulatedNetWorthMinor: Long,
    val netWorthDeltaMinor: Long,
    val baselineLiquidSavingsMinor: Long,
    val simulatedLiquidSavingsMinor: Long,
    val baselineMonthlyFreeCashFlowMinor: Long,
    val simulatedMonthlyFreeCashFlowMinor: Long
)

data class MilestoneComparison(
    val milestoneName: String,
    val monthIndex: Int,
    val baselineNetWorth: Money,
    val simulatedNetWorth: Money,
    val netWorthDelta: Money,
    val baselineCashFlow: Money,
    val simulatedCashFlow: Money
)

data class SimulationResult(
    val monthlyTimeline: List<SimulationMonthPoint>,
    val milestone1Year: MilestoneComparison,
    val milestone3Years: MilestoneComparison,
    val milestone5Years: MilestoneComparison,
    val breakevenMonth: Int?,
    val emergencyRunwayMonthsSimulated: Int
)

/**
 * What-If Financial Scenario Simulator Engine.
 * Projects financial trajectory over 1, 3, and 5 years, comparing baseline status-quo
 * vs simulated events (car purchase, rent increase, SIP hikes, subscription cutbacks).
 */
object WhatIfEngine {

    fun simulate(params: SimulationParams, totalMonths: Int = 60): SimulationResult {
        val monthlyInvRate = if (params.annualInvestmentReturnRatePercent > 0) {
            (1.0 + params.annualInvestmentReturnRatePercent / 100.0).pow(1.0 / 12.0) - 1.0
        } else {
            0.0
        }

        // Active loans from one-time purchases
        class ActiveLoan(
            val monthlyEmiMinor: Long,
            var remainingMonths: Int,
            var remainingPrincipalMinor: Long,
            val monthlyRate: Double
        )

        val activeLoans = mutableListOf<ActiveLoan>()

        // Timeline state tracking
        var baseLiquid = params.initialLiquidSavingsMinor
        var baseInvestments = max(0L, params.initialNetWorthMinor - params.initialLiquidSavingsMinor)

        var simLiquid = params.initialLiquidSavingsMinor
        var simInvestments = baseInvestments
        var simTotalLoanLiability = 0L

        val timeline = mutableListOf<SimulationMonthPoint>()
        var breakevenMonth: Int? = null

        for (m in 1..totalMonths) {
            // --- 1. Baseline Calculations ---
            val baseIncome = params.monthlyIncomeMinor
            val baseExpense = params.monthlyExpenseMinor
            val baseCashFlow = baseIncome - baseExpense

            // Compounding investment growth
            baseInvestments += (baseInvestments * monthlyInvRate).toLong()
            if (baseCashFlow > 0) {
                // Half into liquid, half into compounding investments
                baseLiquid += baseCashFlow / 2
                baseInvestments += baseCashFlow / 2
            } else {
                baseLiquid += baseCashFlow
            }
            val baseNetWorth = baseLiquid + baseInvestments

            // --- 2. Simulated Scenario Calculations ---
            // Income adjustments
            var simIncome = params.monthlyIncomeMinor
            for (id in params.incomeDeltas) {
                if (m >= id.startMonth) {
                    simIncome += id.monthlyDeltaMinor
                }
            }

            // Recurring expense adjustments
            var simExpense = params.monthlyExpenseMinor
            for (red in params.recurringExpenseDeltas) {
                if (m >= red.startMonth) {
                    simExpense += red.monthlyDeltaMinor
                }
            }

            // One-time expenses occurring in month m
            for (ote in params.oneTimeExpenses) {
                if (ote.monthOffset == m) {
                    simLiquid -= ote.downPaymentMinor
                    val loanPrincipal = max(0L, ote.totalAmountMinor - ote.downPaymentMinor)
                    if (loanPrincipal > 0 && ote.loanTenureMonths > 0) {
                        val monthlyEmiMinor = EmiCalculator.calculateMonthlyEmi(
                            principalMinor = loanPrincipal,
                            annualInterestRatePercent = ote.loanInterestRateAnnual,
                            tenureMonths = ote.loanTenureMonths
                        )
                        val monthlyRate = (ote.loanInterestRateAnnual / 12.0) / 100.0
                        activeLoans.add(
                            ActiveLoan(
                                monthlyEmiMinor = monthlyEmiMinor,
                                remainingMonths = ote.loanTenureMonths,
                                remainingPrincipalMinor = loanPrincipal,
                                monthlyRate = monthlyRate
                            )
                        )
                        simTotalLoanLiability += loanPrincipal
                    }
                }
            }

            // Process active loan EMIs
            var currentMonthLoanEmiTotal = 0L
            val loanIterator = activeLoans.iterator()
            while (loanIterator.hasNext()) {
                val loan = loanIterator.next()
                if (loan.remainingMonths > 0) {
                    val interestComponent = (loan.remainingPrincipalMinor * loan.monthlyRate).toLong()
                    val principalComponent = min(loan.remainingPrincipalMinor, loan.monthlyEmiMinor - interestComponent)
                    currentMonthLoanEmiTotal += loan.monthlyEmiMinor
                    loan.remainingPrincipalMinor -= principalComponent
                    simTotalLoanLiability -= principalComponent
                    loan.remainingMonths -= 1
                    if (loan.remainingMonths <= 0 || loan.remainingPrincipalMinor <= 0) {
                        loanIterator.remove()
                    }
                }
            }

            // Process SIP investments
            var simSipTotal = 0L
            for (sip in params.sipAdjustments) {
                if (m >= sip.startMonth) {
                    simSipTotal += sip.monthlySipMinor
                }
            }

            val simCashFlow = simIncome - simExpense - currentMonthLoanEmiTotal

            // Growth of simulated investments
            simInvestments += (simInvestments * monthlyInvRate).toLong()

            // Allocate cash flow and SIP
            if (simSipTotal > 0) {
                simLiquid -= simSipTotal
                simInvestments += simSipTotal
            }

            if (simCashFlow > 0) {
                simLiquid += simCashFlow / 2
                simInvestments += simCashFlow / 2
            } else {
                simLiquid += simCashFlow
            }

            val simNetWorth = (simLiquid + simInvestments) - simTotalLoanLiability
            val deltaNetWorth = simNetWorth - baseNetWorth

            if (breakevenMonth == null && m > 1 && deltaNetWorth >= 0 && (timeline.lastOrNull()?.netWorthDeltaMinor ?: 0) < 0) {
                breakevenMonth = m
            }

            timeline.add(
                SimulationMonthPoint(
                    monthIndex = m,
                    baselineNetWorthMinor = baseNetWorth,
                    simulatedNetWorthMinor = simNetWorth,
                    netWorthDeltaMinor = deltaNetWorth,
                    baselineLiquidSavingsMinor = baseLiquid,
                    simulatedLiquidSavingsMinor = simLiquid,
                    baselineMonthlyFreeCashFlowMinor = baseCashFlow,
                    simulatedMonthlyFreeCashFlowMinor = simCashFlow
                )
            )
        }

        fun makeMilestone(name: String, month: Int): MilestoneComparison {
            val point = timeline.getOrNull(month - 1) ?: timeline.last()
            return MilestoneComparison(
                milestoneName = name,
                monthIndex = point.monthIndex,
                baselineNetWorth = Money(point.baselineNetWorthMinor),
                simulatedNetWorth = Money(point.simulatedNetWorthMinor),
                netWorthDelta = Money(point.netWorthDeltaMinor),
                baselineCashFlow = Money(point.baselineMonthlyFreeCashFlowMinor),
                simulatedCashFlow = Money(point.simulatedMonthlyFreeCashFlowMinor)
            )
        }

        val m1 = makeMilestone("1 Year", 12)
        val m3 = makeMilestone("3 Years", 36)
        val m5 = makeMilestone("5 Years", 60)

        val latestExpenses = max(100L, params.monthlyExpenseMinor)
        val runwayMonths = ((timeline.getOrNull(11)?.simulatedLiquidSavingsMinor ?: simLiquid) / latestExpenses).toInt()

        return SimulationResult(
            monthlyTimeline = timeline,
            milestone1Year = m1,
            milestone3Years = m3,
            milestone5Years = m5,
            breakevenMonth = breakevenMonth,
            emergencyRunwayMonthsSimulated = max(0, runwayMonths)
        )
    }
}
