package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["account_id"]),
        Index(value = ["is_reached"])
    ]
)
data class GoalEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "target_amount_minor")
    val targetAmountMinor: Long,

    @ColumnInfo(name = "saved_amount_minor")
    val savedAmountMinor: Long = 0L,

    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @ColumnInfo(name = "target_date")
    val targetDate: Long,

    @ColumnInfo(name = "account_id")
    val accountId: String? = null,

    @ColumnInfo(name = "icon_name")
    val iconName: String = "flag",

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#4CAF50",

    @ColumnInfo(name = "is_reached")
    val isReached: Boolean = false
)
