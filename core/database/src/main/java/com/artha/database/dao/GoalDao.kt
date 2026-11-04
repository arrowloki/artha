package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artha.database.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY is_reached ASC, target_date ASC")
    fun getAllGoalsFlow(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("UPDATE goals SET saved_amount_minor = saved_amount_minor + :deltaMinor, is_reached = (saved_amount_minor + :deltaMinor >= target_amount_minor) WHERE id = :id")
    suspend fun contributeToGoal(id: String, deltaMinor: Long)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: String)
}
