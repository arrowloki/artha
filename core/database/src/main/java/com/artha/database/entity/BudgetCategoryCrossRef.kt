package com.artha.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "budget_categories",
    primaryKeys = ["budget_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budget_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["budget_id"]),
        Index(value = ["category_id"])
    ]
)
data class BudgetCategoryCrossRef(
    @ColumnInfo(name = "budget_id")
    val budgetId: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String
)
