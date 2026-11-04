package com.artha.feature.accounts

import com.artha.domain.AccountBalanceState
import com.artha.domain.SingleCountCreditCardRule
import com.artha.model.AccountType
import com.artha.model.Money
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountsLogicTest {

    private val singleCountRule = SingleCountCreditCardRule()

    @Test
    fun testNetWorthSummaryCalculation() {
        // Given Bank (₹50,000 asset) + Cash (₹5,000 asset) + Credit Card (₹12,000 debt/liability)
        val accounts = listOf(
            AccountBalanceState(
                accountId = "acc_bank",
                name = "Bank",
                type = AccountType.BANK,
                balanceMinor = 5000000L,
                currency = "INR"
            ),
            AccountBalanceState(
                accountId = "acc_cash",
                name = "Cash",
                type = AccountType.CASH,
                balanceMinor = 500000L,
                currency = "INR"
            ),
            AccountBalanceState(
                accountId = "acc_card",
                name = "Card",
                type = AccountType.CREDIT_CARD,
                balanceMinor = 1200000L,
                currency = "INR",
                creditLimitMinor = 10000000L
            )
        )

        val totalAssetsMinor = accounts.filterNot { it.type.isLiability }.sumOf { it.balanceMinor }
        val totalLiabilitiesMinor = accounts.filter { it.type.isLiability }.sumOf { it.balanceMinor }
        val netWorth = singleCountRule.calculateNetWorth(accounts, "INR")

        // Assets = 50,000 + 5,000 = 55,000
        assertEquals(5500000L, totalAssetsMinor)
        // Liabilities = 12,000
        assertEquals(1200000L, totalLiabilitiesMinor)
        // Net Worth = 55,000 - 12,000 = 43,000
        assertEquals(4300000L, netWorth.amountMinor)
    }

    @Test
    fun testCreditCardBillPaymentPreservesNetWorth() {
        // Paying a ₹12,000 bill from Bank to Credit Card
        // Before payment: Bank ₹50,000, Card Debt ₹12,000 -> Net Worth ₹38,000
        val before = listOf(
            AccountBalanceState("b", "Bank", AccountType.BANK, 5000000L, "INR"),
            AccountBalanceState("c", "Card", AccountType.CREDIT_CARD, 1200000L, "INR")
        )
        val netWorthBefore = singleCountRule.calculateNetWorth(before, "INR")
        assertEquals(3800000L, netWorthBefore.amountMinor)

        // After paying ₹12,000:
        // Bank drops to ₹38,000 (-12,000)
        // Card debt drops to ₹0 (-12,000)
        val after = listOf(
            AccountBalanceState("b", "Bank", AccountType.BANK, 3800000L, "INR"),
            AccountBalanceState("c", "Card", AccountType.CREDIT_CARD, 0L, "INR")
        )
        val netWorthAfter = singleCountRule.calculateNetWorth(after, "INR")

        // Single-count invariant: Transfer payment of credit card bill NEVER changes Net Worth!
        assertEquals(3800000L, netWorthAfter.amountMinor)
        assertEquals(netWorthBefore.amountMinor, netWorthAfter.amountMinor)
    }
}
