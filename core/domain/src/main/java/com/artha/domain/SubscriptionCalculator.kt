package com.artha.domain

import com.artha.database.entity.BillSubscriptionEntity
import com.artha.model.RecurrenceFrequency
import java.util.concurrent.TimeUnit

data class SubscriptionSummary(
    val totalYearlyCostMinor: Long,
    val totalMonthlyCostMinor: Long,
    val activeCount: Int,
    val upcomingDueCount: Int,
    val trialsEndingSoonCount: Int,
    val currency: String
)

data class SubscriptionRenewalStatus(
    val subscription: BillSubscriptionEntity,
    val daysUntilDue: Long,
    val isDueSoon: Boolean, // Within 7 days
    val isTrialEndingSoon: Boolean, // Trial ends within 3 days
    val yearlyEquivalentMinor: Long
)

class SubscriptionCalculator {

    /**
     * Normalize any subscription frequency into its exact annualized cost in minor units.
     */
    fun calculateAnnualCostMinor(amountMinor: Long, frequency: RecurrenceFrequency): Long {
        return when (frequency) {
            RecurrenceFrequency.DAILY -> amountMinor * 365L
            RecurrenceFrequency.WEEKLY -> amountMinor * 52L
            RecurrenceFrequency.BIWEEKLY -> amountMinor * 26L
            RecurrenceFrequency.MONTHLY -> amountMinor * 12L
            RecurrenceFrequency.QUARTERLY -> amountMinor * 4L
            RecurrenceFrequency.YEARLY -> amountMinor
            RecurrenceFrequency.CUSTOM_CRON -> amountMinor * 12L
        }
    }

    /**
     * Compute full summary rollup across all active subscriptions.
     */
    fun calculateSummary(
        subscriptions: List<BillSubscriptionEntity>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SubscriptionSummary {
        val active = subscriptions.filter { it.isActive }
        val currency = active.firstOrNull()?.currency ?: "INR"

        var totalAnnual = 0L
        var upcomingCount = 0
        var trialEndingCount = 0

        val sevenDaysMs = TimeUnit.DAYS.toMillis(7)
        val threeDaysMs = TimeUnit.DAYS.toMillis(3)

        for (sub in active) {
            totalAnnual += calculateAnnualCostMinor(sub.amountMinor, sub.frequency)

            val dueDiff = sub.nextDueDate - currentTimeMillis
            if (dueDiff in 0..sevenDaysMs) {
                upcomingCount++
            }

            val trialEnd = sub.trialEndDate
            if (trialEnd != null) {
                val trialDiff = trialEnd - currentTimeMillis
                if (trialDiff in 0..threeDaysMs) {
                    trialEndingCount++
                }
            }
        }

        val totalMonthly = totalAnnual / 12L

        return SubscriptionSummary(
            totalYearlyCostMinor = totalAnnual,
            totalMonthlyCostMinor = totalMonthly,
            activeCount = active.size,
            upcomingDueCount = upcomingCount,
            trialsEndingSoonCount = trialEndingCount,
            currency = currency
        )
    }

    /**
     * Evaluate renewal status for an individual subscription.
     */
    fun evaluateRenewalStatus(
        sub: BillSubscriptionEntity,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SubscriptionRenewalStatus {
        val dueDiff = sub.nextDueDate - currentTimeMillis
        val daysUntilDue = TimeUnit.MILLISECONDS.toDays(dueDiff).coerceAtLeast(0L)
        val isDueSoon = dueDiff in 0..TimeUnit.DAYS.toMillis(7)

        val trialEnd = sub.trialEndDate
        val isTrialEndingSoon = trialEnd != null && (trialEnd - currentTimeMillis) in 0..TimeUnit.DAYS.toMillis(3)

        val yearlyCost = calculateAnnualCostMinor(sub.amountMinor, sub.frequency)

        return SubscriptionRenewalStatus(
            subscription = sub,
            daysUntilDue = daysUntilDue,
            isDueSoon = isDueSoon,
            isTrialEndingSoon = isTrialEndingSoon,
            yearlyEquivalentMinor = yearlyCost
        )
    }
}
