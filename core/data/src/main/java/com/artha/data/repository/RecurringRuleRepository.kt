package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

class RecurringRuleRepository(private val db: ArthaDatabase) {

    private val recurringDao = db.recurringRuleDao()

    fun getAllRecurringRulesFlow(): Flow<List<RecurringRuleEntity>> = recurringDao.getAllRecurringRulesFlow()

    suspend fun getDueRules(nowTimestamp: Long): List<RecurringRuleEntity> = recurringDao.getDueRecurringRules(nowTimestamp)

    suspend fun saveRule(rule: RecurringRuleEntity) {
        recurringDao.insertRecurringRule(rule)
    }

    suspend fun updateRule(rule: RecurringRuleEntity) {
        recurringDao.updateRecurringRule(rule)
    }

    suspend fun updateNextDueDate(id: String, nextDueDate: Long) {
        recurringDao.updateNextDueDate(id, nextDueDate)
    }

    suspend fun deleteRule(id: String) {
        recurringDao.deleteRecurringRule(id)
    }
}
