package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.PaymentMethod
import com.artha.model.TransactionStatus
import com.artha.model.TransactionType
import com.artha.model.WorthItStatus

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["to_account_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["account_id", "timestamp"]),
        Index(value = ["to_account_id", "timestamp"]),
        Index(value = ["category_id", "timestamp"]),
        Index(value = ["type", "timestamp"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "to_account_id")
    val toAccountId: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "exchange_rate_to_base")
    val exchangeRateToBase: Double = 1.0,

    @ColumnInfo(name = "amount_in_base_minor")
    val amountInBaseMinor: Long = amountMinor,

    @ColumnInfo(name = "type")
    val type: TransactionType,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "is_excluded_from_budget")
    val isExcludedFromBudget: Boolean = false,

    @ColumnInfo(name = "is_excluded_from_reports")
    val isExcludedFromReports: Boolean = false,

    @ColumnInfo(name = "is_reconciled")
    val isReconciled: Boolean = false,

    @ColumnInfo(name = "status")
    val status: TransactionStatus = TransactionStatus.COMPLETED,

    @ColumnInfo(name = "receipt_image_path")
    val receiptImagePath: String? = null,

    @ColumnInfo(name = "recurring_rule_id")
    val recurringRuleId: String? = null,

    @ColumnInfo(name = "parent_transaction_id")
    val parentTransactionId: String? = null,

    @ColumnInfo(name = "transfer_counterpart_id")
    val transferCounterpartId: String? = null,

    @ColumnInfo(name = "latitude")
    val latitude: Double? = null,

    @ColumnInfo(name = "longitude")
    val longitude: Double? = null,

    @ColumnInfo(name = "location_name")
    val locationName: String? = null,

    @ColumnInfo(name = "worth_it_status")
    val worthItStatus: WorthItStatus = WorthItStatus.UNRATED,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
