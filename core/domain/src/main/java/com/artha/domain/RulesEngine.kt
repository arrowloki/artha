package com.artha.domain

import com.artha.database.entity.RuleEntity
import com.artha.database.entity.TransactionEntity
import com.artha.model.PaymentMethod
import java.util.Locale
import java.util.regex.Pattern

/**
 * Smart Rules Engine.
 * Evaluates user-defined rules and executes actions on transactions (e.g. auto-categorization,
 * payment method mapping, budget/report exclusions, tag assignments).
 */
object RulesEngine {

    /**
     * Checks if a transaction matches the condition specified in a rule.
     */
    fun matches(transaction: TransactionEntity, rule: RuleEntity): Boolean {
        if (!rule.isActive) return false

        val operator = rule.conditionOperator.uppercase(Locale.ROOT)
        val targetValue = rule.conditionValue.trim()

        return when (rule.conditionField.uppercase(Locale.ROOT)) {
            "NOTE", "MERCHANT", "PAYEE" -> {
                val fieldVal = transaction.note.lowercase(Locale.ROOT)
                val compVal = targetValue.lowercase(Locale.ROOT)
                when (operator) {
                    "CONTAINS" -> fieldVal.contains(compVal)
                    "EQUALS" -> fieldVal == compVal
                    "STARTS_WITH" -> fieldVal.startsWith(compVal)
                    "ENDS_WITH" -> fieldVal.endsWith(compVal)
                    "REGEX" -> runCatching { Pattern.compile(targetValue, Pattern.CASE_INSENSITIVE).matcher(transaction.note).find() }.getOrDefault(false)
                    else -> false
                }
            }
            "AMOUNT" -> {
                val compAmountMinor = targetValue.toLongOrNull() ?: return false
                when (operator) {
                    "EQUALS" -> transaction.amountMinor == compAmountMinor
                    "GREATER_THAN", ">" -> transaction.amountMinor > compAmountMinor
                    "LESS_THAN", "<" -> transaction.amountMinor < compAmountMinor
                    "GREATER_THAN_OR_EQUAL", ">=" -> transaction.amountMinor >= compAmountMinor
                    "LESS_THAN_OR_EQUAL", "<=" -> transaction.amountMinor <= compAmountMinor
                    else -> false
                }
            }
            "PAYMENT_METHOD" -> {
                val currentMethod = transaction.paymentMethod.name.lowercase(Locale.ROOT)
                val targetMethod = targetValue.lowercase(Locale.ROOT)
                when (operator) {
                    "EQUALS" -> currentMethod == targetMethod
                    "CONTAINS" -> currentMethod.contains(targetMethod)
                    else -> false
                }
            }
            else -> false
        }
    }

    /**
     * Applies the rule's action to the transaction, returning the transformed transaction.
     */
    fun applyRule(transaction: TransactionEntity, rule: RuleEntity): TransactionEntity {
        if (!matches(transaction, rule)) return transaction

        val actionVal = rule.actionValue.trim()
        return when (rule.actionField.uppercase(Locale.ROOT)) {
            "SET_CATEGORY" -> {
                transaction.copy(categoryId = actionVal)
            }
            "SET_PAYMENT_METHOD" -> {
                val method = runCatching { PaymentMethod.valueOf(actionVal.uppercase(Locale.ROOT)) }.getOrNull()
                if (method != null) transaction.copy(paymentMethod = method) else transaction
            }
            "EXCLUDE_FROM_BUDGET" -> {
                transaction.copy(isExcludedFromBudget = true)
            }
            "EXCLUDE_FROM_REPORTS" -> {
                transaction.copy(isExcludedFromReports = true)
            }
            "APPEND_NOTE" -> {
                val updatedNote = if (transaction.note.isBlank()) actionVal else "${transaction.note} $actionVal"
                transaction.copy(note = updatedNote)
            }
            else -> transaction
        }
    }

    /**
     * Evaluates a transaction sequentially against a list of active rules sorted by priority.
     */
    fun evaluateAll(transaction: TransactionEntity, rules: List<RuleEntity>): TransactionEntity {
        var current = transaction
        for (rule in rules) {
            if (rule.isActive && matches(current, rule)) {
                current = applyRule(current, rule)
            }
        }
        return current
    }
}
