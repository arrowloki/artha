package com.artha.domain

import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.MonthlyAggregateEntity
import com.artha.database.entity.TransactionEntity
import com.artha.model.TransactionType
import java.util.Calendar

data class CashFlowSummary(
    val totalInflowMinor: Long,
    val totalOutflowMinor: Long,
    val netSavingsMinor: Long,
    val savingsRatePercentage: Int, // e.g. 35%
    val currency: String
)

data class CategoryShare(
    val categoryId: String,
    val categoryName: String,
    val colorHex: String,
    val totalAmountMinor: Long,
    val sharePercentage: Float, // 0.0f to 100.0f
    val transactionCount: Int
)

data class PeriodComparison(
    val currentPeriodExpenseMinor: Long,
    val previousPeriodExpenseMinor: Long,
    val deltaMinor: Long, // current - previous
    val percentageChange: Float, // e.g. +12.5% or -8.0%
    val isIncreased: Boolean
)

data class DailySpendHeatmapDay(
    val dayOfMonth: Int,
    val totalSpendMinor: Long,
    val intensityLevel: Int // 0: None, 1: Low, 2: Medium, 3: High
)

class ReportsEngine {

    /**
     * Compute cash flow metrics and savings rate percentage.
     */
    fun calculateCashFlow(
        inflowMinor: Long,
        outflowMinor: Long,
        currency: String = "INR"
    ): CashFlowSummary {
        val netSavings = inflowMinor - outflowMinor
        val savingsRate = if (inflowMinor > 0L) {
            val rate = (netSavings.toDouble() / inflowMinor.toDouble()) * 100.0
            rate.toInt().coerceIn(-100, 100)
        } else {
            0
        }

        return CashFlowSummary(
            totalInflowMinor = inflowMinor,
            totalOutflowMinor = outflowMinor,
            netSavingsMinor = netSavings,
            savingsRatePercentage = savingsRate,
            currency = currency
        )
    }

    /**
     * Compute category share distribution.
     */
    fun calculateCategoryShares(
        aggregates: List<CategoryMonthlyAggregateEntity>,
        categoryNames: Map<String, Pair<String, String>> // id -> (name, colorHex)
    ): List<CategoryShare> {
        val totalSpent = aggregates.sumOf { it.totalAmountMinor }
        if (totalSpent <= 0L) return emptyList()

        return aggregates
            .sortedByDescending { it.totalAmountMinor }
            .map { agg ->
                val meta = categoryNames[agg.categoryId]
                val name = meta?.first ?: "Other"
                val color = meta?.second ?: "#9E9E9E"
                val share = (agg.totalAmountMinor.toFloat() / totalSpent.toFloat()) * 100f

                CategoryShare(
                    categoryId = agg.categoryId,
                    categoryName = name,
                    colorHex = color,
                    totalAmountMinor = agg.totalAmountMinor,
                    sharePercentage = share,
                    transactionCount = agg.transactionCount
                )
            }
    }

    /**
     * Compare spending between two periods.
     */
    fun comparePeriods(
        currentPeriodExpenseMinor: Long,
        previousPeriodExpenseMinor: Long
    ): PeriodComparison {
        val delta = currentPeriodExpenseMinor - previousPeriodExpenseMinor
        val pctChange = if (previousPeriodExpenseMinor > 0L) {
            ((delta.toFloat()) / previousPeriodExpenseMinor.toFloat()) * 100f
        } else {
            0f
        }

        return PeriodComparison(
            currentPeriodExpenseMinor = currentPeriodExpenseMinor,
            previousPeriodExpenseMinor = previousPeriodExpenseMinor,
            deltaMinor = delta,
            percentageChange = pctChange,
            isIncreased = delta > 0L
        )
    }

    /**
     * Generate calendar heatmap for a given month.
     */
    fun generateHeatmap(
        transactions: List<TransactionEntity>,
        year: Int,
        monthZeroIndexed: Int
    ): List<DailySpendHeatmapDay> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthZeroIndexed)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Sum expenses by day
        val daySums = LongArray(maxDays + 1)
        transactions.filter { it.type == TransactionType.EXPENSE }.forEach { tx ->
            cal.timeInMillis = tx.timestamp
            val d = cal.get(Calendar.DAY_OF_MONTH)
            if (d in 1..maxDays) {
                daySums[d] += tx.amountMinor
            }
        }

        val maxSpend = daySums.maxOrNull() ?: 1L
        val list = ArrayList<DailySpendHeatmapDay>(maxDays)

        for (d in 1..maxDays) {
            val spend = daySums[d]
            val intensity = when {
                spend == 0L -> 0
                maxSpend > 0 && spend < (maxSpend * 0.33) -> 1
                maxSpend > 0 && spend < (maxSpend * 0.66) -> 2
                else -> 3
            }
            list.add(DailySpendHeatmapDay(d, spend, intensity))
        }

        return list
    }
}
