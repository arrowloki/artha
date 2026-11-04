package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.RuleEntity
import com.artha.domain.RulesEngine
import com.artha.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RuleRepository(private val db: ArthaDatabase) {

    private val ruleDao = db.ruleDao()
    private val transactionDao = db.transactionDao()

    fun getAllRulesFlow(): Flow<List<RuleEntity>> = ruleDao.getAllRulesFlow()

    suspend fun getActiveRules(): List<RuleEntity> = ruleDao.getActiveRules()

    suspend fun insertRule(rule: RuleEntity) {
        ruleDao.insertRule(rule)
    }

    suspend fun updateRule(rule: RuleEntity) {
        ruleDao.updateRule(rule)
    }

    suspend fun deleteRule(id: String) {
        ruleDao.deleteRule(id)
    }

    /**
     * Re-runs all active rules across historical transactions.
     * Updates modified transactions and synchronizes category monthly aggregates.
     * Returns the count of transactions updated.
     */
    suspend fun rerunAllRulesOnHistory(): Int {
        val activeRules = ruleDao.getActiveRules()
        if (activeRules.isEmpty()) return 0

        val allTransactions = transactionDao.getAllTransactions()
        var updatedCount = 0

        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.US)

        for (tx in allTransactions) {
            val evaluated = RulesEngine.evaluateAll(tx, activeRules)
            if (evaluated != tx) {
                transactionDao.updateTransactionRaw(evaluated)

                val oldCat = tx.categoryId
                val newCat = evaluated.categoryId

                // If category changed on an expense, sync category aggregates
                if (tx.type == TransactionType.EXPENSE && oldCat != newCat) {
                    val yearMonth = dateFormat.format(Date(tx.timestamp))

                    // Deduct from old category
                    if (oldCat != null) {
                        val oldCatAgg = transactionDao.getCategoryMonthlyAggregate(yearMonth, oldCat, tx.currency)
                        if (oldCatAgg != null) {
                            val newTotal = (oldCatAgg.totalAmountMinor - tx.amountMinor).coerceAtLeast(0L)
                            val newCount = (oldCatAgg.transactionCount - 1).coerceAtLeast(0)
                            transactionDao.upsertCategoryMonthlyAggregate(
                                oldCatAgg.copy(totalAmountMinor = newTotal, transactionCount = newCount)
                            )
                        }
                    }

                    // Add to new category
                    if (newCat != null) {
                        val newCatAgg = transactionDao.getCategoryMonthlyAggregate(yearMonth, newCat, evaluated.currency)
                            ?: CategoryMonthlyAggregateEntity(yearMonth = yearMonth, categoryId = newCat, currency = evaluated.currency)
                        transactionDao.upsertCategoryMonthlyAggregate(
                            newCatAgg.copy(
                                totalAmountMinor = newCatAgg.totalAmountMinor + evaluated.amountMinor,
                                transactionCount = newCatAgg.transactionCount + 1
                            )
                        )
                    }
                }
                updatedCount++
            }
        }
        return updatedCount
    }
}
