package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.AccountMonthlyAggregateEntity
import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.MonthlyAggregateEntity
import com.artha.database.entity.TransactionEntity
import com.artha.database.entity.TransactionFtsEntity
import com.artha.model.AccountType
import com.artha.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Dao
abstract class TransactionDao {

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    abstract suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    abstract fun getRecentTransactionsFlow(limit: Int = 100, offset: Int = 0): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE account_id = :accountId OR to_account_id = :accountId ORDER BY timestamp DESC")
    abstract fun getTransactionsByAccountFlow(accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    abstract fun getTransactionsBetweenFlow(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY timestamp DESC")
    abstract fun getTransactionsByCategoryFlow(categoryId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTransactionRaw(transaction: TransactionEntity)

    @Update
    abstract suspend fun updateTransactionRaw(transaction: TransactionEntity)

    @Delete
    abstract suspend fun deleteTransactionRaw(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFts(fts: TransactionFtsEntity)

    @Query("DELETE FROM transactions_fts WHERE transaction_id = :transactionId")
    abstract suspend fun deleteFts(transactionId: String)

    @Query("""
        SELECT t.* FROM transactions t
        JOIN transactions_fts fts ON t.id = fts.transaction_id
        WHERE transactions_fts MATCH :query
        ORDER BY t.timestamp DESC
        LIMIT :limit
    """)
    abstract suspend fun searchTransactions(query: String, limit: Int = 100): List<TransactionEntity>

    // --- Helper Queries for Aggregate Updates ---

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    abstract suspend fun getAccount(id: String): AccountEntity?

    @Query("UPDATE accounts SET current_balance_minor = current_balance_minor + :deltaMinor, updated_at = :updatedAt WHERE id = :id")
    abstract suspend fun updateAccountBalance(id: String, deltaMinor: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM monthly_aggregates WHERE year_month = :yearMonth AND currency = :currency LIMIT 1")
    abstract suspend fun getMonthlyAggregate(yearMonth: String, currency: String): MonthlyAggregateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertMonthlyAggregate(aggregate: MonthlyAggregateEntity)

    @Query("SELECT * FROM category_monthly_aggregates WHERE year_month = :yearMonth AND category_id = :categoryId AND currency = :currency LIMIT 1")
    abstract suspend fun getCategoryMonthlyAggregate(yearMonth: String, categoryId: String, currency: String): CategoryMonthlyAggregateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertCategoryMonthlyAggregate(aggregate: CategoryMonthlyAggregateEntity)

    @Query("SELECT * FROM account_monthly_aggregates WHERE year_month = :yearMonth AND account_id = :accountId LIMIT 1")
    abstract suspend fun getAccountMonthlyAggregate(yearMonth: String, accountId: String): AccountMonthlyAggregateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAccountMonthlyAggregate(aggregate: AccountMonthlyAggregateEntity)

    /**
     * Atomic transaction insert enforcing the single-count credit card rule
     * and incrementally maintaining monthly, category, and account aggregates in the same DB transaction.
     */
    @Transaction
    open suspend fun insertTransactionWithAggregates(
        tx: TransactionEntity,
        searchableText: String = ""
    ) {
        insertTransactionRaw(tx)

        // Maintain FTS
        insertFts(
            TransactionFtsEntity(
                transactionId = tx.id,
                note = tx.note,
                searchableText = "$searchableText ${tx.note}"
            )
        )

        val yearMonth = formatYearMonth(tx.timestamp)
        val sourceAccount = getAccount(tx.accountId) ?: return

        when (tx.type) {
            TransactionType.EXPENSE -> {
                // Balance impact:
                // If asset account: balance decreases (-amount)
                // If credit card liability: outstanding debt increases (+amount)
                val balanceDelta = if (sourceAccount.type.isLiability) tx.amountMinor else -tx.amountMinor
                updateAccountBalance(tx.accountId, balanceDelta, tx.timestamp)

                // Incremental Monthly Aggregate (transfers strictly excluded!)
                val currentMonth = getMonthlyAggregate(yearMonth, tx.currency)
                    ?: MonthlyAggregateEntity(yearMonth = yearMonth, currency = tx.currency)
                val updatedMonth = currentMonth.copy(
                    totalExpenseMinor = currentMonth.totalExpenseMinor + tx.amountMinor,
                    netSavingsMinor = currentMonth.totalIncomeMinor - (currentMonth.totalExpenseMinor + tx.amountMinor),
                    transactionCount = currentMonth.transactionCount + 1,
                    updatedAt = System.currentTimeMillis()
                )
                upsertMonthlyAggregate(updatedMonth)

                // Incremental Category Aggregate
                if (tx.categoryId != null) {
                    val currentCat = getCategoryMonthlyAggregate(yearMonth, tx.categoryId, tx.currency)
                        ?: CategoryMonthlyAggregateEntity(yearMonth = yearMonth, categoryId = tx.categoryId, currency = tx.currency)
                    val updatedCat = currentCat.copy(
                        totalAmountMinor = currentCat.totalAmountMinor + tx.amountMinor,
                        transactionCount = currentCat.transactionCount + 1
                    )
                    upsertCategoryMonthlyAggregate(updatedCat)
                }

                // Account Monthly Inflow/Outflow
                val currentAcc = getAccountMonthlyAggregate(yearMonth, tx.accountId)
                    ?: AccountMonthlyAggregateEntity(yearMonth = yearMonth, accountId = tx.accountId)
                upsertAccountMonthlyAggregate(
                    currentAcc.copy(totalOutflowMinor = currentAcc.totalOutflowMinor + tx.amountMinor)
                )
            }

            TransactionType.INCOME -> {
                // Balance impact: asset increases
                updateAccountBalance(tx.accountId, tx.amountMinor, tx.timestamp)

                // Incremental Monthly Aggregate
                val currentMonth = getMonthlyAggregate(yearMonth, tx.currency)
                    ?: MonthlyAggregateEntity(yearMonth = yearMonth, currency = tx.currency)
                val updatedMonth = currentMonth.copy(
                    totalIncomeMinor = currentMonth.totalIncomeMinor + tx.amountMinor,
                    netSavingsMinor = (currentMonth.totalIncomeMinor + tx.amountMinor) - currentMonth.totalExpenseMinor,
                    transactionCount = currentMonth.transactionCount + 1,
                    updatedAt = System.currentTimeMillis()
                )
                upsertMonthlyAggregate(updatedMonth)

                // Incremental Category Aggregate
                if (tx.categoryId != null) {
                    val currentCat = getCategoryMonthlyAggregate(yearMonth, tx.categoryId, tx.currency)
                        ?: CategoryMonthlyAggregateEntity(yearMonth = yearMonth, categoryId = tx.categoryId, currency = tx.currency)
                    upsertCategoryMonthlyAggregate(
                        currentCat.copy(
                            totalAmountMinor = currentCat.totalAmountMinor + tx.amountMinor,
                            transactionCount = currentCat.transactionCount + 1
                        )
                    )
                }

                // Account Monthly Inflow
                val currentAcc = getAccountMonthlyAggregate(yearMonth, tx.accountId)
                    ?: AccountMonthlyAggregateEntity(yearMonth = yearMonth, accountId = tx.accountId)
                upsertAccountMonthlyAggregate(
                    currentAcc.copy(totalInflowMinor = currentAcc.totalInflowMinor + tx.amountMinor)
                )
            }

            TransactionType.TRANSFER -> {
                // Transfer: bank down, destination up (or credit card outstanding down)
                // NEVER alters monthly expense/income aggregates!
                val toAccountId = tx.toAccountId ?: return
                val targetAccount = getAccount(toAccountId) ?: return

                // Source account deduction
                val srcDelta = if (sourceAccount.type.isLiability) tx.amountMinor else -tx.amountMinor
                updateAccountBalance(tx.accountId, srcDelta, tx.timestamp)

                // Destination account addition (paying a card reduces liability debt)
                val destDelta = if (targetAccount.type.isLiability) -tx.amountMinor else tx.amountMinor
                updateAccountBalance(toAccountId, destDelta, tx.timestamp)
            }
        }
    }

    @Query("UPDATE transactions SET is_reconciled = :isReconciled, updated_at = :updatedAt WHERE id = :id")
    abstract suspend fun updateReconciledStatus(id: String, isReconciled: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Atomic transaction deletion reverting balance changes and aggregates.
     */
    @Transaction
    open suspend fun deleteTransactionWithAggregates(tx: TransactionEntity) {
        val yearMonth = formatYearMonth(tx.timestamp)
        val sourceAccount = getAccount(tx.accountId)

        when (tx.type) {
            TransactionType.EXPENSE -> {
                if (sourceAccount != null) {
                    val balanceDelta = if (sourceAccount.type.isLiability) -tx.amountMinor else tx.amountMinor
                    updateAccountBalance(tx.accountId, balanceDelta, System.currentTimeMillis())
                }

                val currentMonth = getMonthlyAggregate(yearMonth, tx.currency)
                if (currentMonth != null) {
                    val newExpense = (currentMonth.totalExpenseMinor - tx.amountMinor).coerceAtLeast(0L)
                    val newCount = (currentMonth.transactionCount - 1).coerceAtLeast(0)
                    upsertMonthlyAggregate(
                        currentMonth.copy(
                            totalExpenseMinor = newExpense,
                            netSavingsMinor = currentMonth.totalIncomeMinor - newExpense,
                            transactionCount = newCount,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                if (tx.categoryId != null) {
                    val currentCat = getCategoryMonthlyAggregate(yearMonth, tx.categoryId, tx.currency)
                    if (currentCat != null) {
                        val newAmount = (currentCat.totalAmountMinor - tx.amountMinor).coerceAtLeast(0L)
                        val newCount = (currentCat.transactionCount - 1).coerceAtLeast(0)
                        upsertCategoryMonthlyAggregate(
                            currentCat.copy(
                                totalAmountMinor = newAmount,
                                transactionCount = newCount
                            )
                        )
                    }
                }

                val currentAcc = getAccountMonthlyAggregate(yearMonth, tx.accountId)
                if (currentAcc != null) {
                    upsertAccountMonthlyAggregate(
                        currentAcc.copy(
                            totalOutflowMinor = (currentAcc.totalOutflowMinor - tx.amountMinor).coerceAtLeast(0L)
                        )
                    )
                }
            }

            TransactionType.INCOME -> {
                if (sourceAccount != null) {
                    updateAccountBalance(tx.accountId, -tx.amountMinor, System.currentTimeMillis())
                }

                val currentMonth = getMonthlyAggregate(yearMonth, tx.currency)
                if (currentMonth != null) {
                    val newIncome = (currentMonth.totalIncomeMinor - tx.amountMinor).coerceAtLeast(0L)
                    val newCount = (currentMonth.transactionCount - 1).coerceAtLeast(0)
                    upsertMonthlyAggregate(
                        currentMonth.copy(
                            totalIncomeMinor = newIncome,
                            netSavingsMinor = newIncome - currentMonth.totalExpenseMinor,
                            transactionCount = newCount,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                if (tx.categoryId != null) {
                    val currentCat = getCategoryMonthlyAggregate(yearMonth, tx.categoryId, tx.currency)
                    if (currentCat != null) {
                        val newAmount = (currentCat.totalAmountMinor - tx.amountMinor).coerceAtLeast(0L)
                        val newCount = (currentCat.transactionCount - 1).coerceAtLeast(0)
                        upsertCategoryMonthlyAggregate(
                            currentCat.copy(
                                totalAmountMinor = newAmount,
                                transactionCount = newCount
                            )
                        )
                    }
                }

                val currentAcc = getAccountMonthlyAggregate(yearMonth, tx.accountId)
                if (currentAcc != null) {
                    upsertAccountMonthlyAggregate(
                        currentAcc.copy(
                            totalInflowMinor = (currentAcc.totalInflowMinor - tx.amountMinor).coerceAtLeast(0L)
                        )
                    )
                }
            }

            TransactionType.TRANSFER -> {
                val toAccountId = tx.toAccountId
                val targetAccount = if (toAccountId != null) getAccount(toAccountId) else null

                if (sourceAccount != null) {
                    val srcDelta = if (sourceAccount.type.isLiability) -tx.amountMinor else tx.amountMinor
                    updateAccountBalance(tx.accountId, srcDelta, System.currentTimeMillis())
                }

                if (toAccountId != null && targetAccount != null) {
                    val destDelta = if (targetAccount.type.isLiability) tx.amountMinor else -tx.amountMinor
                    updateAccountBalance(toAccountId, destDelta, System.currentTimeMillis())
                }
            }
        }

        deleteFts(tx.id)
        deleteTransactionRaw(tx)
    }

    private fun formatYearMonth(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
        return sdf.format(Date(timestamp))
    }
}
