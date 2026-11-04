package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rules",
    indices = [
        Index(value = ["is_active", "priority"])
    ]
)
data class RuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "condition_field")
    val conditionField: String, // "MERCHANT", "AMOUNT", "NOTE"

    @ColumnInfo(name = "condition_operator")
    val conditionOperator: String, // "CONTAINS", "EQUALS", "GREATER_THAN", "REGEX"

    @ColumnInfo(name = "condition_value")
    val conditionValue: String,

    @ColumnInfo(name = "action_field")
    val actionField: String, // "SET_CATEGORY", "SET_TAG", "SET_PAYMENT_METHOD", "FLAG"

    @ColumnInfo(name = "action_value")
    val actionValue: String,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
