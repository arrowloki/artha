package com.artha.domain

import com.artha.model.AccountType
import com.artha.model.Money
import com.artha.model.TransactionType

data class AccountBalanceState(
    val accountId: String,
    val name: String,
    val type: AccountType,
    val balanceMinor: Long,
    val currency: String = "INR",
    val creditLimitMinor: Long = 0L
) {
    /**
     * For credit cards, balanceMinor represents what you owe (liability).
     * For asset accounts, balanceMinor represents what you have.
     */
    val availableCreditMinor: Long
        get() = if (type == AccountType.CREDIT_CARD) {
            (creditLimitMinor - balanceMinor).coerceAtLeast(0L)
        } else {
            0L
        }

    val utilizationPercentage: Float
        get() = if (type == AccountType.CREDIT_CARD && creditLimitMinor > 0L) {
            ((balanceMinor.toFloat() / creditLimitMinor.toFloat()) * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }
}

/**
 * Domain Invariant: Money is spent when the purchase happens, never again when the card bill is paid.
 * Paying a card bill is a transfer between accounts, never an expense.
 */
class SingleCountCreditCardRule {

    /**
     * Determines if a transaction is counted as spending in budgets, spending reports, and aggregates.
     * Guaranteed: TRANSFERS are strictly excluded!
     */
    fun isCountedAsSpending(type: TransactionType): Boolean {
        return when (type) {
            TransactionType.EXPENSE -> true
            TransactionType.INCOME -> false
            TransactionType.TRANSFER -> false
        }
    }

    /**
     * Calculates the balance delta for an account involved in a transaction.
     *
     * Rules:
     * - EXPENSE on Asset: asset decreases (-amount)
     * - EXPENSE on Liability (Credit Card): liability debt increases (+amount)
     * - INCOME on Asset: asset increases (+amount)
     * - TRANSFER: Source decreases, Destination increases (or destination liability debt decreases)
     */
    fun computeBalanceDelta(
        accountType: AccountType,
        transactionType: TransactionType,
        amountMinor: Long,
        isSourceAccount: Boolean
    ): Long {
        require(amountMinor >= 0L) { "Amount minor must be non-negative, was $amountMinor" }

        return when (transactionType) {
            TransactionType.EXPENSE -> {
                if (accountType.isLiability) {
                    amountMinor // Card outstanding debt goes UP
                } else {
                    -amountMinor // Bank/Cash balance goes DOWN
                }
            }

            TransactionType.INCOME -> {
                amountMinor // Bank/Cash balance goes UP
            }

            TransactionType.TRANSFER -> {
                if (isSourceAccount) {
                    if (accountType.isLiability) amountMinor else -amountMinor
                } else {
                    // Destination account:
                    // If paying a credit card, card liability goes DOWN (-amount)
                    // If moving to savings/cash, asset goes UP (+amount)
                    if (accountType.isLiability) -amountMinor else amountMinor
                }
            }
        }
    }

    /**
     * Net Worth Calculation: Total Assets - Total Liabilities.
     * Proves:
     * 1. A card purchase immediately drops Net Worth (liability increases).
     * 2. Paying the card bill leaves Net Worth UNCHANGED (Bank asset drops by X, Card debt drops by X).
     */
    fun calculateNetWorth(accounts: List<AccountBalanceState>, targetCurrency: String = "INR"): Money {
        var totalAssets = 0L
        var totalLiabilities = 0L

        for (account in accounts) {
            if (account.type.isLiability) {
                totalLiabilities += account.balanceMinor
            } else {
                totalAssets += account.balanceMinor
            }
        }

        val netWorthMinor = totalAssets - totalLiabilities
        return Money(netWorthMinor, targetCurrency)
    }

    /**
     * Mistake Guard: Detects if a user inadvertently logs a credit card bill payment
     * as an ordinary expense, so the app can prompt or auto-convert to a Transfer.
     */
    fun detectBillPaymentMistake(
        note: String,
        amountMinor: Long,
        cardAccounts: List<AccountBalanceState>
    ): AccountBalanceState? {
        val lowerNote = note.lowercase()
        val paymentKeywords = listOf("bill", "payment", "card payment", "cc payment", "pay card", "statement", "credit card")

        val hasKeyword = paymentKeywords.any { lowerNote.contains(it) }

        for (card in cardAccounts.filter { it.type == AccountType.CREDIT_CARD }) {
            val cardNameLower = card.name.lowercase()
            val matchesCardName = lowerNote.contains(cardNameLower)
            val matchesOutstanding = card.balanceMinor > 0L && abs(card.balanceMinor - amountMinor) <= 500L // within ₹5 / 500 minor units

            if ((hasKeyword && matchesCardName) || (hasKeyword && matchesOutstanding) || (matchesCardName && matchesOutstanding)) {
                return card
            }
        }

        return null
    }

    private fun abs(value: Long): Long = if (value < 0) -value else value
}
