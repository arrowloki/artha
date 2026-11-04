package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_payments",
    foreignKeys = [
        ForeignKey(
            entity = DebtLoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["debt_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["debt_id"]),
        Index(value = ["transaction_id"])
    ]
)
data class DebtPaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "debt_id")
    val debtId: String,

    @ColumnInfo(name = "transaction_id")
    val transactionId: String? = null,

    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,

    @ColumnInfo(name = "principal_component_minor")
    val principalComponentMinor: Long,

    @ColumnInfo(name = "interest_component_minor")
    val interestComponentMinor: Long,

    @ColumnInfo(name = "payment_date")
    val paymentDate: Long = System.currentTimeMillis()
)
