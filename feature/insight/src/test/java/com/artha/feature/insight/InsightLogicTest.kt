package com.artha.feature.insight

import com.artha.database.entity.DebtLoanEntity
import com.artha.domain.EmiCalculator
import com.artha.domain.ReportsEngine
import com.artha.model.DebtType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightLogicTest {

    private val emiCalculator = EmiCalculator()
    private val reportsEngine = ReportsEngine()

    @Test
    fun testDebtsSummaryAggregation() {
        val debts = listOf(
            DebtLoanEntity(
                id = "d1",
                personOrInstitution = "Friend",
                type = DebtType.LENT,
                principalMinor = 500000L,
                remainingMinor = 500000L,
                startDate = 0L
            ),
            DebtLoanEntity(
                id = "d2",
                personOrInstitution = "Neighbor",
                type = DebtType.BORROWED,
                principalMinor = 200000L,
                remainingMinor = 200000L,
                startDate = 0L
            ),
            DebtLoanEntity(
                id = "d3",
                personOrInstitution = "Bank Loan",
                type = DebtType.EMI_LOAN,
                principalMinor = 5000000L,
                remainingMinor = 4000000L,
                startDate = 0L
            )
        )

        val lent = debts.filter { it.type == DebtType.LENT }.sumOf { it.remainingMinor }
        val borrowed = debts.filter { it.type == DebtType.BORROWED }.sumOf { it.remainingMinor }
        val emi = debts.filter { it.type == DebtType.EMI_LOAN }.sumOf { it.remainingMinor }
        val net = lent - (borrowed + emi)

        assertEquals(500000L, lent) // ₹5,000
        assertEquals(200000L, borrowed) // ₹2,000
        assertEquals(4000000L, emi) // ₹40,000
        assertEquals(-3700000L, net) // -₹37,000 net liability
    }

    @Test
    fun testPrepaymentSimulationTenureDrop() {
        // ₹5,00,000 loan at 9.0% for 60 months
        val principal = 50000000L
        val rate = 9.0
        val tenure = 60

        val sim = emiCalculator.simulatePrepayment(
            principalMinor = principal,
            annualInterestRatePercent = rate,
            tenureMonths = tenure,
            extraMonthlyPaymentMinor = 500000L // ₹5,000 extra per month
        )

        assertTrue(sim.monthsSaved > 0)
        assertTrue(sim.interestSavedMinor > 0L)
    }
}
