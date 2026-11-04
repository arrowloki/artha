package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val db: ArthaDatabase) {

    private val goalDao = db.goalDao()

    fun getAllGoalsFlow(): Flow<List<GoalEntity>> = goalDao.getAllGoalsFlow()

    suspend fun saveGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    suspend fun contributeToGoal(id: String, deltaMinor: Long) {
        goalDao.contributeToGoal(id, deltaMinor)
    }

    suspend fun deleteGoal(id: String) {
        goalDao.deleteGoal(id)
    }
}
