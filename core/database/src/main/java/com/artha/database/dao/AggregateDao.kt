package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artha.database.entity.AccountMonthlyAggregateEntity
import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.MonthlyAggregateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AggregateDao {

    @Query("SELECT * FROM monthly_aggregates WHERE year_month = :yearMonth AND currency = :currency LIMIT 1")
    suspend fun getMonthlyAggregate(yearMonth: String, currency: String): MonthlyAggregateEntity?

    @Query("SELECT * FROM monthly_aggregates WHERE currency = :currency ORDER BY year_month DESC")
    fun getMonthlyAggregatesFlow(currency: String): Flow<List<MonthlyAggregateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMonthlyAggregate(aggregate: MonthlyAggregateEntity)

    @Query("SELECT * FROM category_monthly_aggregates WHERE year_month = :yearMonth AND currency = :currency ORDER BY total_amount_minor DESC")
    fun getCategoryMonthlyAggregatesFlow(yearMonth: String, currency: String): Flow<List<CategoryMonthlyAggregateEntity>>

    @Query("SELECT * FROM category_monthly_aggregates WHERE year_month = :yearMonth AND category_id = :categoryId AND currency = :currency LIMIT 1")
    suspend fun getCategoryMonthlyAggregate(yearMonth: String, categoryId: String, currency: String): CategoryMonthlyAggregateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategoryMonthlyAggregate(aggregate: CategoryMonthlyAggregateEntity)

    @Query("SELECT * FROM account_monthly_aggregates WHERE year_month = :yearMonth")
    fun getAccountMonthlyAggregatesFlow(yearMonth: String): Flow<List<AccountMonthlyAggregateEntity>>

    @Query("SELECT * FROM account_monthly_aggregates WHERE year_month = :yearMonth AND account_id = :accountId LIMIT 1")
    suspend fun getAccountMonthlyAggregate(yearMonth: String, accountId: String): AccountMonthlyAggregateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccountMonthlyAggregate(aggregate: AccountMonthlyAggregateEntity)
}
