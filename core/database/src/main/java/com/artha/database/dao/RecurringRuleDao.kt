package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artha.database.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {

    @Query("SELECT * FROM recurring_rules ORDER BY next_due_date ASC")
    fun getAllRecurringRulesFlow(): Flow<List<RecurringRuleEntity>>

    @Query("SELECT * FROM recurring_rules WHERE next_due_date <= :nowTimestamp")
    suspend fun getDueRecurringRules(nowTimestamp: Long): List<RecurringRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringRule(rule: RecurringRuleEntity)

    @Update
    suspend fun updateRecurringRule(rule: RecurringRuleEntity)

    @Query("UPDATE recurring_rules SET next_due_date = :nextDueDate WHERE id = :id")
    suspend fun updateNextDueDate(id: String, nextDueDate: Long)

    @Query("DELETE FROM recurring_rules WHERE id = :id")
    suspend fun deleteRecurringRule(id: String)
}
