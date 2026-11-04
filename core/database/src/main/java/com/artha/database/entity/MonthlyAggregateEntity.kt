package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Pre-computed monthly summary for instant dashboards.
 * Excludes transfers by domain invariant.
 */
@Entity(
    tableName = "monthly_aggregates",
    primaryKeys = ["year_month", "currency"]
)
data class MonthlyAggregateEntity(
    @ColumnInfo(name = "year_month")
    val yearMonth: String, // "YYYY-MM"

    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "total_income_minor")
    val totalIncomeMinor: Long = 0L,

    @ColumnInfo(name = "total_expense_minor")
    val totalExpenseMinor: Long = 0L,

    @ColumnInfo(name = "net_savings_minor")
    val netSavingsMinor: Long = 0L,

    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
