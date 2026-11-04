package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artha.database.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Query("SELECT * FROM rules WHERE is_active = 1 ORDER BY priority ASC")
    suspend fun getActiveRules(): List<RuleEntity>

    @Query("SELECT * FROM rules ORDER BY priority ASC")
    fun getAllRulesFlow(): Flow<List<RuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RuleEntity)

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Query("DELETE FROM rules WHERE id = :id")
    suspend fun deleteRule(id: String)
}
