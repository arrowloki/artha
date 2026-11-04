package com.artha.domain

import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetEntity
import com.artha.model.BudgetPeriod
import com.artha.model.RecurrenceFrequency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class PlanningDomainTest {

    private val budgetEngine = BudgetEngine()
    private val subCalculator = SubscriptionCalculator()
    private val scheduleCalculator = RecurringScheduleCalculator()

    @Test
    fun testBudgetConsumptionAndSafeToSpend() {
        val budget = BudgetEntity(
            id = "b1",
            name = "Dining Out",
            amountMinor = 1000000L, // ₹10,000.00
            currency = "INR",
            periodType = BudgetPeriod.MONTHLY,
            startDate = 0L,
            endDate = 0L,
            alertThresholdPercentage = 80
        )

        // 1. Within budget, under 80%
        val consumption1 = budgetEngine.calculateConsumption(budget, spentMinor = 500000L, daysRemainingInPeriod = 10)
        assertEquals(500000L, consumption1.remainingMinor)
        assertEquals(50000L, consumption1.safeToSpendDailyMinor) // ₹500/day
        assertFalse(consumption1.isOverBudget)
        assertFalse(consumption1.isNearThreshold)

        // 2. Near threshold (85%)
        val consumption2 = budgetEngine.calculateConsumption(budget, spentMinor = 850000L, daysRemainingInPeriod = 5)
        assertEquals(150000L, consumption2.remainingMinor)
        assertEquals(30000L, consumption2.safeToSpendDailyMinor) // ₹300/day
        assertFalse(consumption2.isOverBudget)
        assertTrue(consumption2.isNearThreshold)

        // 3. Over budget (110%)
        val consumption3 = budgetEngine.calculateConsumption(budget, spentMinor = 1100000L, daysRemainingInPeriod = 3)
        assertEquals(-100000L, consumption3.remainingMinor)
        assertEquals(0L, consumption3.safeToSpendDailyMinor)
        assertTrue(consumption3.isOverBudget)
        assertFalse(consumption3.isNearThreshold)
    }

    @Test
    fun testSubscriptionYearlyCostRollup() {
        val subs = listOf(
            BillSubscriptionEntity(
                id = "s1",
                name = "Netflix",
                amountMinor = 64900L, // ₹649/mo
                frequency = RecurrenceFrequency.MONTHLY,
                accountId = "a1",
                nextDueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)
            ),
            BillSubscriptionEntity(
                id = "s2",
                name = "Gym Membership",
                amountMinor = 200000L, // ₹2,000/mo
                frequency = RecurrenceFrequency.MONTHLY,
                accountId = "a1",
                nextDueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15)
            ),
            BillSubscriptionEntity(
                id = "s3",
                name = "Amazon Prime",
                amountMinor = 149900L, // ₹1,499/yr
                frequency = RecurrenceFrequency.YEARLY,
                accountId = "a1",
                nextDueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(40)
            )
        )

        val summary = subCalculator.calculateSummary(subs)

        // Netflix: 649 * 12 = 7,788
        // Gym: 2,000 * 12 = 24,000
        // Prime: 1,499
        // Total = 7,788 + 24,000 + 1,499 = 33,287
        assertEquals(3328700L, summary.totalYearlyCostMinor)
        assertEquals(3, summary.activeCount)
        assertEquals(1, summary.upcomingDueCount) // Netflix is due in 2 days
    }

    @Test
    fun testRecurringScheduleAdvance() {
        val baseTime = 1704067200000L // 2024-01-01
        val nextMonth = scheduleCalculator.calculateNextOccurrence(baseTime, RecurrenceFrequency.MONTHLY, 1)
        assertTrue(nextMonth > baseTime)
    }
}
