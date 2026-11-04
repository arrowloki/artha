package com.artha.feature.transactions

import com.artha.database.entity.TransactionEntity
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionFilterTest {

    private val sampleTransactions = listOf(
        TransactionEntity(
            id = "tx1",
            accountId = "acc_bank",
            amountMinor = 150000L,
            type = TransactionType.EXPENSE,
            paymentMethod = PaymentMethod.UPI,
            note = "Grocery store purchase",
            timestamp = 1000L
        ),
        TransactionEntity(
            id = "tx2",
            accountId = "acc_bank",
            amountMinor = 5000000L,
            type = TransactionType.INCOME,
            paymentMethod = PaymentMethod.NET_BANKING,
            note = "Monthly salary",
            timestamp = 2000L
        ),
        TransactionEntity(
            id = "tx3",
            accountId = "acc_bank",
            toAccountId = "acc_card",
            amountMinor = 1000000L,
            type = TransactionType.TRANSFER,
            paymentMethod = PaymentMethod.NET_BANKING,
            note = "Credit card bill payment",
            timestamp = 3000L
        )
    )

    private fun applyFilter(
        list: List<TransactionEntity>,
        filter: TransactionFilterState
    ): List<TransactionEntity> {
        val start = filter.startDate
        val end = filter.endDate
        return list.filter { tx ->
            if (filter.type != null && tx.type != filter.type) return@filter false
            if (filter.accountId != null && tx.accountId != filter.accountId && tx.toAccountId != filter.accountId) return@filter false
            if (filter.categoryId != null && tx.categoryId != filter.categoryId) return@filter false
            if (start != null && tx.timestamp < start) return@filter false
            if (end != null && tx.timestamp > end) return@filter false
            if (filter.query.isNotBlank()) {
                val q = filter.query.trim().lowercase()
                val matchNote = tx.note.lowercase().contains(q)
                val matchType = tx.type.name.lowercase().contains(q)
                if (!matchNote && !matchType) return@filter false
            }
            true
        }
    }

    @Test
    fun testFilterByType() {
        val expenseFilter = TransactionFilterState(type = TransactionType.EXPENSE)
        val expenses = applyFilter(sampleTransactions, expenseFilter)
        assertEquals(1, expenses.size)
        assertEquals("tx1", expenses[0].id)

        val incomeFilter = TransactionFilterState(type = TransactionType.INCOME)
        val incomes = applyFilter(sampleTransactions, incomeFilter)
        assertEquals(1, incomes.size)
        assertEquals("tx2", incomes[0].id)
    }

    @Test
    fun testFilterByDateRange() {
        val dateFilter = TransactionFilterState(startDate = 1500L, endDate = 2500L)
        val filtered = applyFilter(sampleTransactions, dateFilter)
        assertEquals(1, filtered.size)
        assertEquals("tx2", filtered[0].id)
    }

    @Test
    fun testFilterByQuery() {
        val queryFilter = TransactionFilterState(query = "grocery")
        val filtered = applyFilter(sampleTransactions, queryFilter)
        assertEquals(1, filtered.size)
        assertEquals("tx1", filtered[0].id)
    }
}
