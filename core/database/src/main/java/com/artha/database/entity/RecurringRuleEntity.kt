package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.PaymentMethod
import com.artha.model.RecurrenceFrequency
import com.artha.model.TransactionType

@Entity(
    tableName = "recurring_rules",
    indices = [
        Index(value = ["next_due_date"])
    ]
)
data class RecurringRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: TransactionType,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "to_account_id")
    val toAccountId: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,

    @ColumnInfo(name = "frequency")
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,

    @ColumnInfo(name = "interval")
    val interval: Int = 1,

    @ColumnInfo(name = "next_due_date")
    val nextDueDate: Long,

    @ColumnInfo(name = "auto_apply")
    val autoApply: Boolean = false,

    @ColumnInfo(name = "reminder_days_before")
    val reminderDaysBefore: Int = 1
)
