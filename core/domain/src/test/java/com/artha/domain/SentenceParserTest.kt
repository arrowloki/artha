package com.artha.domain

import com.artha.database.entity.CategoryEntity
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SentenceParserTest {

    private val sampleCategories = listOf(
        CategoryEntity(id = "cat-food", name = "Food & Dining", type = "EXPENSE"),
        CategoryEntity(id = "cat-groceries", name = "Groceries", type = "EXPENSE"),
        CategoryEntity(id = "cat-transport", name = "Transport & Fuel", type = "EXPENSE"),
        CategoryEntity(id = "cat-salary", name = "Salary", type = "INCOME")
    )

    @Test
    fun parseSimpleCoffeeExpenseWithCard() {
        val result = SentenceParser.parse("45 coffee at blue tokai card", sampleCategories)
        assertTrue(result.isValid)
        assertEquals(4500L, result.amountMinor)
        assertEquals(TransactionType.EXPENSE, result.type)
        assertEquals(PaymentMethod.CREDIT_CARD, result.paymentMethod)
        assertEquals("Blue Tokai", result.merchant)
        assertEquals("Coffee & Dining", result.categoryName)
    }

    @Test
    fun parseRupeeGroceriesUpi() {
        val result = SentenceParser.parse("₹250 groceries at nature basket upi", sampleCategories)
        assertTrue(result.isValid)
        assertEquals(25000L, result.amountMinor)
        assertEquals("INR", result.currencyCode)
        assertEquals(PaymentMethod.UPI, result.paymentMethod)
        assertEquals("Nature Basket", result.merchant)
        assertEquals("Groceries", result.categoryName)
        assertEquals("cat-groceries", result.categoryId)
    }

    @Test
    fun parseSalaryIncomeBank() {
        val result = SentenceParser.parse("salary 85000 bank", sampleCategories)
        assertTrue(result.isValid)
        assertEquals(8500000L, result.amountMinor)
        assertEquals(TransactionType.INCOME, result.type)
        assertEquals(PaymentMethod.NET_BANKING, result.paymentMethod)
        assertEquals("Salary", result.categoryName)
        assertEquals("cat-salary", result.categoryId)
    }

    @Test
    fun parseLakhAndKiloUnits() {
        val petrol = SentenceParser.parse("2.5k petrol at shell by card", sampleCategories)
        assertTrue(petrol.isValid)
        assertEquals(250000L, petrol.amountMinor) // 2,500 * 100
        assertEquals("Shell", petrol.merchant)
        assertEquals(PaymentMethod.CREDIT_CARD, petrol.paymentMethod)

        val car = SentenceParser.parse("1.5L car downpayment", sampleCategories)
        assertTrue(car.isValid)
        assertEquals(15000000L, car.amountMinor) // 150,000 * 100
    }

    @Test
    fun parseYesterdayDate() {
        val yesterday = SentenceParser.parse("120 lunch cash yesterday", sampleCategories)
        assertTrue(yesterday.isValid)
        assertEquals(12000L, yesterday.amountMinor)
        assertEquals(PaymentMethod.CASH, yesterday.paymentMethod)
        assertTrue(yesterday.timestamp < System.currentTimeMillis())
    }

    @Test
    fun parseUsdCurrency() {
        val result = SentenceParser.parse("$14.99 netflix subscription card")
        assertTrue(result.isValid)
        assertEquals(1499L, result.amountMinor)
        assertEquals("USD", result.currencyCode)
        assertEquals("Netflix", result.merchant)
        assertEquals(PaymentMethod.CREDIT_CARD, result.paymentMethod)
    }
}
