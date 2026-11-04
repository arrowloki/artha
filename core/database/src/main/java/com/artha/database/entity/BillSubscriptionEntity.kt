package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.RecurrenceFrequency

@Entity(
    tableName = "bills_subscriptions",
    indices = [
        Index(value = ["next_due_date"]),
        Index(value = ["is_active"])
    ]
)
data class BillSubscriptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "frequency")
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,

    @ColumnInfo(name = "due_day")
    val dueDay: Int = 1,

    @ColumnInfo(name = "trial_end_date")
    val trialEndDate: Long? = null,

    @ColumnInfo(name = "cancellation_url")
    val cancellationUrl: String? = null,

    @ColumnInfo(name = "is_auto_paid")
    val isAutoPaid: Boolean = false,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "last_paid_date")
    val lastPaidDate: Long? = null,

    @ColumnInfo(name = "next_due_date")
    val nextDueDate: Long
)
