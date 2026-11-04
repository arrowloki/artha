package com.artha.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatIfEngineTest {

    @Test
    fun testCarPurchaseScenarioReducesNetWorthAndRecovers() {
        val params = SimulationParams(
            initialNetWorthMinor = 2_000_000_00L, // ₹20 Lakhs
            initialLiquidSavingsMinor = 500_000_00L, // ₹5 Lakhs
            monthlyIncomeMinor = 150_000_00L, // ₹1.5 Lakhs / month
            monthlyExpenseMinor = 60_000_00L, // ₹60k / month
            annualInvestmentReturnRatePercent = 12.0,
            oneTimeExpenses = listOf(
                OneTimeScenarioExpense(
                    title = "Buy 15L Car",
                    totalAmountMinor = 1_500_000_00L, // ₹15 Lakhs
                    monthOffset = 1,
                    downPaymentMinor = 300_000_00L, // ₹3 Lakhs down
                    loanTenureMonths = 48, // 4 years
                    loanInterestRateAnnual = 9.0
                )
            )
        )

        val result = WhatIfEngine.simulate(params, totalMonths = 60)

        assertEquals(60, result.monthlyTimeline.size)

        // At month 1, simulated net worth should be lower than baseline due to down payment & initial EMI
        val month1 = result.monthlyTimeline[0]
        assertTrue(month1.netWorthDeltaMinor < 0)

        // Free cash flow in month 1 should be lower by roughly the EMI
        assertTrue(month1.simulatedMonthlyFreeCashFlowMinor < month1.baselineMonthlyFreeCashFlowMinor)

        // Milestones should be populated
        assertNotNull(result.milestone1Year)
        assertNotNull(result.milestone3Years)
        assertNotNull(result.milestone5Years)
        assertTrue(result.emergencyRunwayMonthsSimulated > 0)
    }

    @Test
    fun testSubscriptionCutAndSipHikeIncreasesNetWorth() {
        val params = SimulationParams(
            initialNetWorthMinor = 1_000_000_00L, // ₹10 Lakhs
            initialLiquidSavingsMinor = 300_000_00L,
            monthlyIncomeMinor = 100_000_00L,
            monthlyExpenseMinor = 50_000_00L,
            annualInvestmentReturnRatePercent = 10.0,
            recurringExpenseDeltas = listOf(
                RecurringExpenseDelta(
                    title = "Cut 3 Subscriptions",
                    monthlyDeltaMinor = -2_000_00L // -₹2,000 / month
                )
            ),
            sipAdjustments = listOf(
                SipAdjustment(
                    title = "Additional SIP",
                    monthlySipMinor = 10_000_00L // +₹10,000 / month
                )
            )
        )

        val result = WhatIfEngine.simulate(params, totalMonths = 60)

        // At 5 years, simulated net worth should be higher than baseline
        val m5 = result.milestone5Years
        assertTrue(m5.simulatedNetWorth.amountMinor >= m5.baselineNetWorth.amountMinor)
    }
}
