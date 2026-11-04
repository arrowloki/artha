package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.BudgetCategoryCrossRef
import com.artha.database.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val db: ArthaDatabase) {

    private val budgetDao = db.budgetDao()
    private val aggregateDao = db.aggregateDao()

    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>> = budgetDao.getAllBudgetsFlow()

    suspend fun getBudgetById(id: String): BudgetEntity? = budgetDao.getBudgetById(id)

    suspend fun createOrUpdateBudget(budget: BudgetEntity, categoryIds: List<String> = emptyList()) {
        budgetDao.insertBudget(budget)
        if (categoryIds.isNotEmpty()) {
            budgetDao.clearBudgetCategories(budget.id)
            val refs = categoryIds.map { BudgetCategoryCrossRef(budgetId = budget.id, categoryId = it) }
            budgetDao.insertBudgetCategories(refs)
        }
    }

    suspend fun deleteBudget(id: String) {
        budgetDao.clearBudgetCategories(id)
        budgetDao.deleteBudget(id)
    }

    suspend fun getCategorySpentInMonth(yearMonth: String, categoryId: String, currency: String): Long {
        return aggregateDao.getCategoryMonthlyAggregate(yearMonth, categoryId, currency)?.totalAmountMinor ?: 0L
    }

    suspend fun getTotalMonthlyExpense(yearMonth: String, currency: String): Long {
        return aggregateDao.getMonthlyAggregate(yearMonth, currency)?.totalExpenseMinor ?: 0L
    }
}
