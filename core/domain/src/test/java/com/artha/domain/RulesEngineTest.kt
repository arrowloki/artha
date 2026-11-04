package com.artha.domain

import com.artha.database.entity.RuleEntity
import com.artha.database.entity.TransactionEntity
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RulesEngineTest {

    private val baseTx = TransactionEntity(
        id = "tx-1",
        accountId = "acc-bank",
        amountMinor = 35000L, // ₹350
        type = TransactionType.EXPENSE,
        paymentMethod = PaymentMethod.UPI,
        note = "Starbucks coffee morning meeting"
    )

    @Test
    fun testMerchantContainsRuleCategorizesTransaction() {
        val rule = RuleEntity(
            id = "rule-starbucks",
            name = "Starbucks to Coffee",
            conditionField = "MERCHANT",
            conditionOperator = "CONTAINS",
            conditionValue = "Starbucks",
            actionField = "SET_CATEGORY",
            actionValue = "cat-coffee",
            priority = 1,
            isActive = true
        )

        assertTrue(RulesEngine.matches(baseTx, rule))
        val updated = RulesEngine.applyRule(baseTx, rule)
        assertEquals("cat-coffee", updated.categoryId)
    }

    @Test
    fun testAmountGreaterThanRuleExcludesFromBudget() {
        val largeTx = baseTx.copy(amountMinor = 10000000L) // ₹1,00,000

        val rule = RuleEntity(
            id = "rule-large-spend",
            name = "Exclude Large Outliers",
            conditionField = "AMOUNT",
            conditionOperator = "GREATER_THAN",
            conditionValue = "5000000", // ₹50,000
            actionField = "EXCLUDE_FROM_BUDGET",
            actionValue = "true",
            priority = 2,
            isActive = true
        )

        assertTrue(RulesEngine.matches(largeTx, rule))
        val updated = RulesEngine.applyRule(largeTx, rule)
        assertTrue(updated.isExcludedFromBudget)
    }

    @Test
    fun testInactiveRuleIsIgnored() {
        val rule = RuleEntity(
            id = "rule-inactive",
            name = "Inactive Rule",
            conditionField = "MERCHANT",
            conditionOperator = "CONTAINS",
            conditionValue = "Starbucks",
            actionField = "SET_CATEGORY",
            actionValue = "cat-coffee",
            priority = 1,
            isActive = false
        )

        assertFalse(RulesEngine.matches(baseTx, rule))
    }

    @Test
    fun testMultipleRulesEvaluatedInPriorityOrder() {
        val rule1 = RuleEntity(
            id = "rule-1",
            name = "Set Category",
            conditionField = "NOTE",
            conditionOperator = "CONTAINS",
            conditionValue = "meeting",
            actionField = "SET_CATEGORY",
            actionValue = "cat-business",
            priority = 10
        )

        val rule2 = RuleEntity(
            id = "rule-2",
            name = "Set Payment Method",
            conditionField = "NOTE",
            conditionOperator = "CONTAINS",
            conditionValue = "coffee",
            actionField = "SET_PAYMENT_METHOD",
            actionValue = "CREDIT_CARD",
            priority = 20
        )

        val updated = RulesEngine.evaluateAll(baseTx, listOf(rule1, rule2))
        assertEquals("cat-business", updated.categoryId)
        assertEquals(PaymentMethod.CREDIT_CARD, updated.paymentMethod)
    }
}
