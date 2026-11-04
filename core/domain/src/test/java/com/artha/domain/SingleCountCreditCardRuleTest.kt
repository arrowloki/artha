package com.artha.domain

import com.artha.model.AccountType
import com.artha.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SingleCountCreditCardRuleTest {

    private val rule = SingleCountCreditCardRule()

    @Test
    fun testSpendingCountingRule() {
        // Purchases count as spending
        assertTrue(rule.isCountedAsSpending(TransactionType.EXPENSE))

        // Incomes do not count as spending
        assertFalse(rule.isCountedAsSpending(TransactionType.INCOME))

        // Card bill payments and transfers MUST NEVER count as spending!
        assertFalse(rule.isCountedAsSpending(TransactionType.TRANSFER))
    }

    @Test
    fun testSingleCountCreditCardLifecycle() {
        // Initial state:
        // Bank = ₹1,00,000 (10,000,000 minor)
        // Card = ₹0 debt
        var bank = AccountBalanceState("b1", "Bank", AccountType.BANK, 10000000L)
        var card = AccountBalanceState("c1", "HDFC Card", AccountType.CREDIT_CARD, 0L, creditLimitMinor = 20000000L)

        // Step 0: Initial Net Worth = 1,00,000
        val initialNetWorth = rule.calculateNetWorth(listOf(bank, card))
        assertEquals(10000000L, initialNetWorth.amountMinor)

        // Step 1: User buys a flight for ₹15,000 using Credit Card (EXPENSE)
        val purchaseAmount = 1500000L // ₹15,000
        assertTrue("Purchase must count as spending", rule.isCountedAsSpending(TransactionType.EXPENSE))

        // Card balance increases (liability goes up)
        val cardDelta = rule.computeBalanceDelta(card.type, TransactionType.EXPENSE, purchaseAmount, isSourceAccount = true)
        card = card.copy(balanceMinor = card.balanceMinor + cardDelta)
        assertEquals(1500000L, card.balanceMinor)

        // Net worth immediately drops from ₹1,00,000 to ₹85,000 on the purchase date!
        val netWorthAfterPurchase = rule.calculateNetWorth(listOf(bank, card))
        assertEquals(8500000L, netWorthAfterPurchase.amountMinor)

        // Step 2: On due date, user pays credit card bill (TRANSFER from Bank to Card)
        assertFalse("Bill payment must NEVER count as spending", rule.isCountedAsSpending(TransactionType.TRANSFER))

        // Bank balance decreases
        val bankDelta = rule.computeBalanceDelta(bank.type, TransactionType.TRANSFER, purchaseAmount, isSourceAccount = true)
        bank = bank.copy(balanceMinor = bank.balanceMinor + bankDelta)
        assertEquals(8500000L, bank.balanceMinor) // ₹85,000 left in bank

        // Card debt decreases back to 0
        val destDelta = rule.computeBalanceDelta(card.type, TransactionType.TRANSFER, purchaseAmount, isSourceAccount = false)
        card = card.copy(balanceMinor = card.balanceMinor + destDelta)
        assertEquals(0L, card.balanceMinor) // Debt is 0

        // CRITICAL INVARIANT: Net Worth MUST REMAIN UNCHANGED by the bill payment!
        val netWorthAfterPayment = rule.calculateNetWorth(listOf(bank, card))
        assertEquals(8500000L, netWorthAfterPayment.amountMinor)
        assertEquals(netWorthAfterPurchase.amountMinor, netWorthAfterPayment.amountMinor)
    }

    @Test
    fun testAtmWithdrawalIsTransferAndPreservesNetWorth() {
        var bank = AccountBalanceState("b1", "Bank", AccountType.BANK, 5000000L)
        var cash = AccountBalanceState("c1", "Cash", AccountType.CASH, 100000L)

        val initialNetWorth = rule.calculateNetWorth(listOf(bank, cash))

        val withdrawAmount = 200000L // ₹2,000
        val bankDelta = rule.computeBalanceDelta(bank.type, TransactionType.TRANSFER, withdrawAmount, isSourceAccount = true)
        val cashDelta = rule.computeBalanceDelta(cash.type, TransactionType.TRANSFER, withdrawAmount, isSourceAccount = false)

        bank = bank.copy(balanceMinor = bank.balanceMinor + bankDelta)
        cash = cash.copy(balanceMinor = cash.balanceMinor + cashDelta)

        assertEquals(4800000L, bank.balanceMinor)
        assertEquals(300000L, cash.balanceMinor)

        val finalNetWorth = rule.calculateNetWorth(listOf(bank, cash))
        assertEquals(initialNetWorth.amountMinor, finalNetWorth.amountMinor)
    }

    @Test
    fun testMistakeGuardDetectsCardBillPayment() {
        val card = AccountBalanceState("c1", "Infinia Card", AccountType.CREDIT_CARD, 2450000L)

        val detected1 = rule.detectBillPaymentMistake("Paid Infinia Card bill", 2450000L, listOf(card))
        assertNotNull(detected1)
        assertEquals("c1", detected1!!.accountId)

        val detected2 = rule.detectBillPaymentMistake("Statement payment", 2450000L, listOf(card))
        assertNotNull(detected2)

        // Regular grocery spend should not trigger mistake guard
        val notCard = rule.detectBillPaymentMistake("Grocery at DMart", 350000L, listOf(card))
        assertEquals(null, notCard)
    }
}
