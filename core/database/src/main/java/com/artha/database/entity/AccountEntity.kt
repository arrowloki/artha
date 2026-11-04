package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.AccountType

@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["type", "is_archived"]),
        Index(value = ["sort_order"])
    ]
)
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: AccountType,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "opening_balance_minor")
    val openingBalanceMinor: Long = 0L,

    @ColumnInfo(name = "current_balance_minor")
    val currentBalanceMinor: Long = 0L,

    // Credit Card specific fields
    @ColumnInfo(name = "credit_limit_minor")
    val creditLimitMinor: Long = 0L,

    @ColumnInfo(name = "billing_day")
    val billingDay: Int = 1,

    @ColumnInfo(name = "due_day")
    val dueDay: Int = 20,

    @ColumnInfo(name = "grace_period_days")
    val gracePeriodDays: Int = 20,

    @ColumnInfo(name = "apr_percentage")
    val aprPercentage: Double = 0.0,

    // UI & metadata
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#2196F3",

    @ColumnInfo(name = "icon_name")
    val iconName: String = "account_balance",

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
