package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.MonthlyAggregateEntity
import com.artha.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val db: ArthaDatabase) {

    private val transactionDao = db.transactionDao()
    private val aggregateDao = db.aggregateDao()

    fun getRecentTransactionsFlow(limit: Int = 100, offset: Int = 0): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactionsFlow(limit, offset)
    }

    fun getTransactionsByAccountFlow(accountId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByAccountFlow(accountId)
    }

    fun getTransactionsBetweenFlow(startTime: Long, endTime: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsBetweenFlow(startTime, endTime)
    }

    fun getTransactionsByCategoryFlow(categoryId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategoryFlow(categoryId)
    }

    fun getMonthlyAggregatesFlow(currency: String): Flow<List<MonthlyAggregateEntity>> {
        return aggregateDao.getMonthlyAggregatesFlow(currency)
    }

    fun getCategoryMonthlyAggregatesFlow(yearMonth: String, currency: String): Flow<List<CategoryMonthlyAggregateEntity>> {
        return aggregateDao.getCategoryMonthlyAggregatesFlow(yearMonth, currency)
    }

    suspend fun recordTransaction(transaction: TransactionEntity, searchableText: String = "") {
        transactionDao.insertTransactionWithAggregates(transaction, searchableText)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransactionWithAggregates(transaction)
    }

    suspend fun updateReconciledStatus(id: String, isReconciled: Boolean) {
        transactionDao.updateReconciledStatus(id, isReconciled)
    }

    suspend fun searchTransactions(query: String, limit: Int = 100): List<TransactionEntity> {
        return transactionDao.searchTransactions(query, limit)
    }
}
