package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "account_monthly_aggregates",
    primaryKeys = ["year_month", "account_id"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["account_id"])
    ]
)
data class AccountMonthlyAggregateEntity(
    @ColumnInfo(name = "year_month")
    val yearMonth: String,

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "starting_balance_minor")
    val startingBalanceMinor: Long = 0L,

    @ColumnInfo(name = "ending_balance_minor")
    val endingBalanceMinor: Long = 0L,

    @ColumnInfo(name = "total_inflow_minor")
    val totalInflowMinor: Long = 0L,

    @ColumnInfo(name = "total_outflow_minor")
    val totalOutflowMinor: Long = 0L
)
