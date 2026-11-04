package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.CategoryEntity
import com.artha.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class CategoryRepository(private val db: ArthaDatabase) {

    private val categoryDao = db.categoryDao()

    fun getActiveCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getActiveCategoriesFlow()

    fun getCategoriesByTypeFlow(type: String): Flow<List<CategoryEntity>> = categoryDao.getCategoriesByTypeFlow(type)

    suspend fun getCategoryById(id: String): CategoryEntity? = categoryDao.getCategoryById(id)

    suspend fun createCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun seedDefaultCategoriesIfEmpty() {
        val existing = categoryDao.getActiveCategoriesFlow().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                // Expense Categories
                CategoryEntity(
                    id = "cat_food_dining",
                    name = "Food & Dining",
                    type = "EXPENSE",
                    iconName = "restaurant",
                    colorHex = "#F59E0B",
                    sortOrder = 1,
                    defaultPaymentMethod = PaymentMethod.UPI
                ),
                CategoryEntity(
                    id = "cat_groceries",
                    name = "Groceries",
                    type = "EXPENSE",
                    iconName = "shopping_cart",
                    colorHex = "#10B981",
                    sortOrder = 2,
                    defaultPaymentMethod = PaymentMethod.UPI
                ),
                CategoryEntity(
                    id = "cat_shopping",
                    name = "Shopping",
                    type = "EXPENSE",
                    iconName = "shopping_bag",
                    colorHex = "#EC4899",
                    sortOrder = 3,
                    defaultPaymentMethod = PaymentMethod.CREDIT_CARD
                ),
                CategoryEntity(
                    id = "cat_transport",
                    name = "Transportation",
                    type = "EXPENSE",
                    iconName = "directions_car",
                    colorHex = "#3B82F6",
                    sortOrder = 4,
                    defaultPaymentMethod = PaymentMethod.UPI
                ),
                CategoryEntity(
                    id = "cat_bills",
                    name = "Bills & Utilities",
                    type = "EXPENSE",
                    iconName = "receipt_long",
                    colorHex = "#6366F1",
                    sortOrder = 5,
                    defaultPaymentMethod = PaymentMethod.NET_BANKING
                ),
                CategoryEntity(
                    id = "cat_housing",
                    name = "Housing & Rent",
                    type = "EXPENSE",
                    iconName = "home",
                    colorHex = "#8B5CF6",
                    sortOrder = 6,
                    defaultPaymentMethod = PaymentMethod.NET_BANKING
                ),
                CategoryEntity(
                    id = "cat_entertainment",
                    name = "Entertainment",
                    type = "EXPENSE",
                    iconName = "movie",
                    colorHex = "#F43F5E",
                    sortOrder = 7,
                    defaultPaymentMethod = PaymentMethod.CREDIT_CARD
                ),
                CategoryEntity(
                    id = "cat_health",
                    name = "Health & Medical",
                    type = "EXPENSE",
                    iconName = "medical_services",
                    colorHex = "#14B8A6",
                    sortOrder = 8,
                    defaultPaymentMethod = PaymentMethod.UPI
                ),
                CategoryEntity(
                    id = "cat_travel",
                    name = "Travel",
                    type = "EXPENSE",
                    iconName = "flight",
                    colorHex = "#06B6D4",
                    sortOrder = 9,
                    defaultPaymentMethod = PaymentMethod.CREDIT_CARD
                ),

                // Income Categories
                CategoryEntity(
                    id = "cat_salary",
                    name = "Salary",
                    type = "INCOME",
                    iconName = "work",
                    colorHex = "#10B981",
                    sortOrder = 10,
                    defaultPaymentMethod = PaymentMethod.NET_BANKING
                ),
                CategoryEntity(
                    id = "cat_freelance",
                    name = "Freelance & Consulting",
                    type = "INCOME",
                    iconName = "laptop",
                    colorHex = "#3B82F6",
                    sortOrder = 11,
                    defaultPaymentMethod = PaymentMethod.NET_BANKING
                ),
                CategoryEntity(
                    id = "cat_investments_income",
                    name = "Dividends & Returns",
                    type = "INCOME",
                    iconName = "trending_up",
                    colorHex = "#F59E0B",
                    sortOrder = 12,
                    defaultPaymentMethod = PaymentMethod.NET_BANKING
                )
            )
            categoryDao.insertCategories(defaults)
        }
    }
}
