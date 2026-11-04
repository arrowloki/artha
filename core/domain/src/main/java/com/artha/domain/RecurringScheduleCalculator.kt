package com.artha.domain

import com.artha.model.RecurrenceFrequency
import java.util.Calendar

class RecurringScheduleCalculator {

    /**
     * Compute the next due timestamp given a base timestamp, frequency, and interval.
     */
    fun calculateNextOccurrence(
        currentTimestamp: Long,
        frequency: RecurrenceFrequency,
        interval: Int = 1
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentTimestamp
        }

        val step = interval.coerceAtLeast(1)

        when (frequency) {
            RecurrenceFrequency.DAILY -> cal.add(Calendar.DAY_OF_YEAR, step)
            RecurrenceFrequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, step)
            RecurrenceFrequency.BIWEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, step * 2)
            RecurrenceFrequency.MONTHLY -> cal.add(Calendar.MONTH, step)
            RecurrenceFrequency.QUARTERLY -> cal.add(Calendar.MONTH, step * 3)
            RecurrenceFrequency.YEARLY -> cal.add(Calendar.YEAR, step)
            RecurrenceFrequency.CUSTOM_CRON -> cal.add(Calendar.MONTH, step)
        }

        return cal.timeInMillis
    }

    /**
     * Determine if a recurring entry is due to be recorded.
     */
    fun isDue(nextDueDate: Long, currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        return nextDueDate <= currentTimeMillis
    }
}
