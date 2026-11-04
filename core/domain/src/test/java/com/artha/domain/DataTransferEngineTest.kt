package com.artha.domain

import com.artha.database.entity.TransactionEntity
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataTransferEngineTest {

    @Test
    fun testExportCsvFormat() {
        val txs = listOf(
            TransactionEntity(
                id = "tx-1",
                accountId = "acc-hdfc",
                categoryId = "cat-food",
                amountMinor = 45000L, // ₹450
                type = TransactionType.EXPENSE,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                note = "Blue Tokai Coffee, Cold Brew",
                timestamp = 1700000000000L
            )
        )
        val accMap = mapOf("acc-hdfc" to "HDFC Millennia")
        val catMap = mapOf("cat-food" to "Food & Dining")

        val csv = DataTransferEngine.exportTransactionsCsv(txs, accMap, catMap)
        assertTrue(csv.contains("Date,Type,Amount,Currency,Account,Category,ToAccount,Note,PaymentMethod,Reconciled"))
        assertTrue(csv.contains("HDFC Millennia"))
        assertTrue(csv.contains("Food & Dining"))
        assertTrue(csv.contains("450.00"))
        assertTrue(csv.contains("\"Blue Tokai Coffee, Cold Brew\"")) // Escaped quotes for comma
    }

    @Test
    fun testParseBankStatementCsvAutoDetect() {
        val sampleBankCsv = """
            Transaction Date,Value Date,Description,Debit,Credit,Balance
            2026-04-10,2026-04-10,"UPI/Blue Tokai/Coffee",450.00,,49550.00
            2026-04-11,2026-04-11,"SALARY CREDIT ACME CORP",,85000.00,134550.00
        """.trimIndent()

        val rows = DataTransferEngine.parseBankCsv(sampleBankCsv)
        assertEquals(2, rows.size)

        val row1 = rows[0]
        assertEquals(TransactionType.EXPENSE, row1.type)
        assertEquals(45000L, row1.amountMinor)
        assertEquals("UPI/Blue Tokai/Coffee", row1.description)
        assertEquals(4955000L, row1.balanceMinor)

        val row2 = rows[1]
        assertEquals(TransactionType.INCOME, row2.type)
        assertEquals(8500000L, row2.amountMinor)
        assertEquals("SALARY CREDIT ACME CORP", row2.description)
        assertEquals(13455000L, row2.balanceMinor)
    }

    @Test
    fun testReceiptOcrExtractor() {
        val sampleReceiptText = """
            BLUE TOKAI COFFEE ROASTERS
            Store #14, Indiranagar, Bangalore
            Date: 12/03/2026
            
            Cold Brew Bottle 220.00
            Croissant 180.00
            
            Subtotal 400.00
            GST 18.00
            Grand Total 418.00
            Thank you for visiting!
        """.trimIndent()

        val parsed = ReceiptOcrExtractor.extract(sampleReceiptText)
        assertEquals("BLUE TOKAI COFFEE ROASTERS", parsed.merchantName)
        assertEquals(41800L, parsed.totalMinor) // ₹418.00
        assertEquals(1800L, parsed.taxMinor) // ₹18.00
        assertNotNull(parsed.dateMillis)
        assertEquals(2, parsed.lineItems.size)
        assertEquals(22000L, parsed.lineItems[0].amountMinor)
    }
}
