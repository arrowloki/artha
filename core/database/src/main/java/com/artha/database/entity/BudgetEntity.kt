package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.BudgetPeriod

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["period_type", "start_date", "end_date"])
    ]
)
data class BudgetEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "period_type")
    val periodType: BudgetPeriod = BudgetPeriod.MONTHLY,

    @ColumnInfo(name = "start_date")
    val startDate: Long,

    @ColumnInfo(name = "end_date")
    val endDate: Long,

    @ColumnInfo(name = "rollover_enabled")
    val rolloverEnabled: Boolean = false,

    @ColumnInfo(name = "envelope_mode")
    val envelopeMode: Boolean = false,

    @ColumnInfo(name = "alert_threshold_percentage")
    val alertThresholdPercentage: Int = 80
)
