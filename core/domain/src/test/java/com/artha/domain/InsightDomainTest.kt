package com.artha.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightDomainTest {

    private val emiCalculator = EmiCalculator()
    private val reportsEngine = ReportsEngine()

    @Test
    fun testEmiCalculationPrecision() {
        // Loan of ₹10,00,000 (100,000,000 minor units) at 8.5% APR for 20 years (240 months)
        val principal = 100000000L
        val rate = 8.5
        val tenure = 240

        val emi = emiCalculator.calculateMonthlyEmi(principal, rate, tenure)
        // Standard formula gives ₹8,678.23 -> 867823 minor units
        assertEquals(867823L, emi)
    }

    @Test
    fun testAmortizationScheduleSettlesToZero() {
        // ₹1,00,000 loan at 10% APR for 12 months
        val principal = 10000000L
        val rate = 10.0
        val tenure = 12

        val schedule = emiCalculator.generateAmortizationSchedule(principal, rate, tenure)
        assertEquals(12, schedule.size)

        // Month 1 starts with full principal
        assertEquals(principal, schedule.first().openingBalanceMinor)

        // Final month ends with exactly 0 balance
        assertEquals(0L, schedule.last().closingBalanceMinor)

        // Total principal paid across all 12 months must exactly equal original principal
        val totalPrincipalPaid = schedule.sumOf { it.principalComponentMinor }
        assertEquals(principal, totalPrincipalPaid)
    }

    @Test
    fun testPrepaymentSavesInterestAndShortensTenure() {
        val principal = 10000000L // ₹1,00,000
        val rate = 10.0
        val tenure = 24 // 2 years

        // Extra ₹2,000 paid each month
        val simulation = emiCalculator.simulatePrepayment(
            principalMinor = principal,
            annualInterestRatePercent = rate,
            tenureMonths = tenure,
            extraMonthlyPaymentMinor = 200000L
        )

        // Must reduce months
        assertTrue(simulation.monthsSaved > 0)
        assertTrue(simulation.revisedTenureMonths < tenure)
        // Must save interest
        assertTrue(simulation.interestSavedMinor > 0L)
    }

    @Test
    fun testCashFlowAndSavingsRate() {
        // Inflow ₹1,00,000, Outflow ₹65,000 -> Savings ₹35,000 (35% savings rate)
        val cashFlow = reportsEngine.calculateCashFlow(
            inflowMinor = 10000000L,
            outflowMinor = 6500000L,
            currency = "INR"
        )

        assertEquals(3500000L, cashFlow.netSavingsMinor)
        assertEquals(35, cashFlow.savingsRatePercentage)
    }

    @Test
    fun testPeriodComparison() {
        // Current ₹45,000 vs Previous ₹40,000 (+₹5,000 / +12.5%)
        val comparison = reportsEngine.comparePeriods(
            currentPeriodExpenseMinor = 4500000L,
            previousPeriodExpenseMinor = 4000000L
        )

        assertEquals(500000L, comparison.deltaMinor)
        assertEquals(12.5f, comparison.percentageChange, 0.01f)
        assertTrue(comparison.isIncreased)
    }
}
