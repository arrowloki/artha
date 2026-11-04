package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.artha.model.DebtType

@Entity(
    tableName = "debts_loans",
    indices = [
        Index(value = ["type"])
    ]
)
data class DebtLoanEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "person_or_institution")
    val personOrInstitution: String,

    @ColumnInfo(name = "type")
    val type: DebtType,

    @ColumnInfo(name = "principal_minor")
    val principalMinor: Long,

    @ColumnInfo(name = "remaining_minor")
    val remainingMinor: Long,

    @ColumnInfo(name = "interest_rate_apr")
    val interestRateApr: Double = 0.0,

    @ColumnInfo(name = "tenure_months")
    val tenureMonths: Int = 0,

    @ColumnInfo(name = "emi_amount_minor")
    val emiAmountMinor: Long = 0L,

    @ColumnInfo(name = "emi_due_day")
    val emiDueDay: Int = 1,

    @ColumnInfo(name = "start_date")
    val startDate: Long,

    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,

    @ColumnInfo(name = "notes")
    val notes: String = ""
)
