package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "category_monthly_aggregates",
    primaryKeys = ["year_month", "category_id", "currency"],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id"])
    ]
)
data class CategoryMonthlyAggregateEntity(
    @ColumnInfo(name = "year_month")
    val yearMonth: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "total_amount_minor")
    val totalAmountMinor: Long = 0L,

    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int = 0
)
