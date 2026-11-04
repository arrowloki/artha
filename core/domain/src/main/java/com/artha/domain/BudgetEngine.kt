package com.artha.domain

import com.artha.database.entity.BudgetEntity
import com.artha.model.Money
import java.util.Calendar
import kotlin.math.max

data class BudgetConsumption(
    val budgetId: String,
    val name: String,
    val budgetAmountMinor: Long,
    val spentMinor: Long,
    val remainingMinor: Long,
    val consumptionRatio: Float, // 0.0f to 1.0f+
    val isOverBudget: Boolean,
    val isNearThreshold: Boolean, // Crossed alertThresholdPercentage
    val safeToSpendDailyMinor: Long,
    val currency: String
)

class BudgetEngine {

    /**
     * Compute budget consumption against spent amount.
     */
    fun calculateConsumption(
        budget: BudgetEntity,
        spentMinor: Long,
        daysRemainingInPeriod: Int
    ): BudgetConsumption {
        val remaining = budget.amountMinor - spentMinor
        val ratio = if (budget.amountMinor > 0) {
            spentMinor.toFloat() / budget.amountMinor.toFloat()
        } else {
            0f
        }

        val percentage = (ratio * 100).toInt()
        val isOver = spentMinor > budget.amountMinor
        val isNear = percentage >= budget.alertThresholdPercentage && !isOver

        val safeDaily = calculateSafeToSpendDaily(remaining, daysRemainingInPeriod)

        return BudgetConsumption(
            budgetId = budget.id,
            name = budget.name,
            budgetAmountMinor = budget.amountMinor,
            spentMinor = spentMinor,
            remainingMinor = remaining,
            consumptionRatio = ratio,
            isOverBudget = isOver,
            isNearThreshold = isNear,
            safeToSpendDailyMinor = safeDaily,
            currency = budget.currency
        )
    }

    /**
     * Calculate Safe-To-Spend Daily allowance.
     * Divides remaining funds across days left in period. Returns 0 if remaining <= 0 or days <= 0.
     */
    fun calculateSafeToSpendDaily(remainingMinor: Long, daysRemainingInPeriod: Int): Long {
        if (remainingMinor <= 0L || daysRemainingInPeriod <= 0) return 0L
        return remainingMinor / daysRemainingInPeriod
    }

    /**
     * Zero-based budgeting: every unit of income must be assigned a purpose.
     * Returns 0 if fully allocated, positive if unallocated money remains, negative if over-allocated.
     */
    fun calculateZeroBasedBalance(totalIncomeMinor: Long, totalAllocatedMinor: Long): Long {
        return totalIncomeMinor - totalAllocatedMinor
    }

    /**
     * Rollover calculation for envelope budgeting.
     * Surplus rolls over into next month's envelope.
     */
    fun calculateRolloverBudget(
        baseBudgetMinor: Long,
        previousPeriodBalanceMinor: Long,
        allowNegativeRollover: Boolean = false
    ): Long {
        return if (previousPeriodBalanceMinor > 0) {
            baseBudgetMinor + previousPeriodBalanceMinor
        } else if (allowNegativeRollover) {
            max(0L, baseBudgetMinor + previousPeriodBalanceMinor)
        } else {
            baseBudgetMinor
        }
    }

    /**
     * Helper to compute days remaining in current calendar month.
     */
    fun getDaysRemainingInMonth(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        return max(1, (maxDays - currentDay) + 1)
    }
}
