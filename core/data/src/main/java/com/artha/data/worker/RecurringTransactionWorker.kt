package com.artha.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artha.data.repository.RecurringRuleRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.ArthaDatabase
import com.artha.database.entity.TransactionEntity
import com.artha.domain.RecurringScheduleCalculator
import java.util.UUID

class RecurringTransactionWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val scheduleCalculator = RecurringScheduleCalculator()

    override suspend fun doWork(): Result {
        val db = ArthaDatabase.getInstance(applicationContext, useEncryption = true)
        val recurringRepo = RecurringRuleRepository(db)
        val transactionRepo = TransactionRepository(db)

        val now = System.currentTimeMillis()
        val dueRules = recurringRepo.getDueRules(now)

        for (rule in dueRules) {
            if (rule.autoApply) {
                val tx = TransactionEntity(
                    id = UUID.randomUUID().toString(),
                    accountId = rule.accountId,
                    toAccountId = rule.toAccountId,
                    categoryId = rule.categoryId,
                    amountMinor = rule.amountMinor,
                    currency = rule.currency,
                    type = rule.type,
                    paymentMethod = rule.paymentMethod,
                    timestamp = now,
                    note = "[Auto] ${rule.title}",
                    recurringRuleId = rule.id
                )
                transactionRepo.recordTransaction(tx, rule.title)
            }

            // Advance to next recurrence cycle
            val nextDate = scheduleCalculator.calculateNextOccurrence(
                currentTimestamp = rule.nextDueDate,
                frequency = rule.frequency,
                interval = rule.interval
            )
            recurringRepo.updateNextDueDate(rule.id, nextDate)
        }

        return Result.success()
    }
}
