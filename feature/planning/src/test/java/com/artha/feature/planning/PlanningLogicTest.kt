package com.artha.feature.planning

import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetEntity
import com.artha.database.entity.GoalEntity
import com.artha.domain.BudgetEngine
import com.artha.domain.SubscriptionCalculator
import com.artha.model.BudgetPeriod
import com.artha.model.Money
import com.artha.model.RecurrenceFrequency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanningLogicTest {

    private val budgetEngine = BudgetEngine()
    private val subCalculator = SubscriptionCalculator()

    @Test
    fun testEnvelopeBudgetThresholdAlerts() {
        val budget = BudgetEntity(
            id = "b_groceries",
            name = "Groceries Envelope",
            amountMinor = 2000000L, // ₹20,000.00
            currency = "INR",
            periodType = BudgetPeriod.MONTHLY,
            startDate = 0L,
            endDate = 0L,
            alertThresholdPercentage = 80
        )

        // Case A: Spent ₹10,000 (50%) -> safe
        val consumptionA = budgetEngine.calculateConsumption(budget, spentMinor = 1000000L, daysRemainingInPeriod = 15)
        assertEquals(1000000L, consumptionA.remainingMinor)
        assertEquals(66666L, consumptionA.safeToSpendDailyMinor) // ₹666.66/day
        assertFalse(consumptionA.isNearThreshold)
        assertFalse(consumptionA.isOverBudget)

        // Case B: Spent ₹17,000 (85%) -> near limit warning triggered
        val consumptionB = budgetEngine.calculateConsumption(budget, spentMinor = 1700000L, daysRemainingInPeriod = 5)
        assertEquals(300000L, consumptionB.remainingMinor)
        assertEquals(60000L, consumptionB.safeToSpendDailyMinor) // ₹600/day
        assertTrue(consumptionB.isNearThreshold)
        assertFalse(consumptionB.isOverBudget)

        // Case C: Spent ₹21,000 (105%) -> exceeded
        val consumptionC = budgetEngine.calculateConsumption(budget, spentMinor = 2100000L, daysRemainingInPeriod = 2)
        assertEquals(-100000L, consumptionC.remainingMinor)
        assertEquals(0L, consumptionC.safeToSpendDailyMinor)
        assertFalse(consumptionC.isNearThreshold)
        assertTrue(consumptionC.isOverBudget)
    }

    @Test
    fun testSubscriptionCostRollupAcrossFrequencies() {
        val subs = listOf(
            BillSubscriptionEntity(
                id = "s1",
                name = "Cloud Backup",
                amountMinor = 20000L, // ₹200/mo
                frequency = RecurrenceFrequency.MONTHLY,
                accountId = "a1",
                nextDueDate = System.currentTimeMillis() + 86400000L
            ),
            BillSubscriptionEntity(
                id = "s2",
                name = "Annual Domain",
                amountMinor = 120000L, // ₹1,200/yr
                frequency = RecurrenceFrequency.YEARLY,
                accountId = "a1",
                nextDueDate = System.currentTimeMillis() + 86400000L * 30
            )
        )

        val summary = subCalculator.calculateSummary(subs)
        // Cloud: 200 * 12 = 2,400
        // Domain: 1,200
        // Total = 3,600
        assertEquals(360000L, summary.totalYearlyCostMinor)
        assertEquals(30000L, summary.totalMonthlyCostMinor) // ₹300/mo
        assertEquals(2, summary.activeCount)
    }

    @Test
    fun testGoalProgressCalculation() {
        val goal = GoalEntity(
            id = "g1",
            name = "MacBook Pro",
            targetAmountMinor = 20000000L, // ₹2,00,000.00
            savedAmountMinor = 15000000L, // ₹1,50,000.00
            targetDate = System.currentTimeMillis() + 100000000L
        )

        val progress = goal.savedAmountMinor.toFloat() / goal.targetAmountMinor.toFloat()
        val percent = (progress * 100).toInt()
        assertEquals(75, percent)
    }
}
