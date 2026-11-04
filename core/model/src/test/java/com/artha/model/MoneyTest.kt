package com.artha.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun testBasicArithmetic() {
        val m1 = Money(15000L, "INR") // ₹150.00
        val m2 = Money(5000L, "INR")  // ₹50.00

        val sum = m1 + m2
        assertEquals(20000L, sum.amountMinor)
        assertEquals("INR", sum.currency)

        val diff = m1 - m2
        assertEquals(10000L, diff.amountMinor)

        val neg = -m1
        assertEquals(-15000L, neg.amountMinor)
    }

    @Test
    fun testCurrencyMismatchThrowsException() {
        val inr = Money(100L, "INR")
        val usd = Money(100L, "USD")

        assertThrows(IllegalArgumentException::class.java) {
            inr + usd
        }
    }

    @Test
    fun testBankersRoundingOnScaling() {
        val m = Money(100L, "USD") // $1.00

        // 100 * 1.555 -> 155.5 -> rounds to 156 (even)
        val scaled1 = m * BigDecimal("1.555")
        assertEquals(156L, scaled1.amountMinor)

        // 100 * 1.545 -> 154.5 -> rounds to 154 (even)
        val scaled2 = m * BigDecimal("1.545")
        assertEquals(154L, scaled2.amountMinor)
    }

    @Test
    fun testBankersRoundingOnDivision() {
        // 15 / 2 = 7.5 -> rounds to 8 (nearest even)
        val m1 = Money(15L, "USD")
        assertEquals(8L, (m1 / 2L).amountMinor)

        // 25 / 2 = 12.5 -> rounds to 12 (nearest even)
        val m2 = Money(25L, "USD")
        assertEquals(12L, (m2 / 2L).amountMinor)
    }

    @Test
    fun testZeroLossSplitEven() {
        val m = Money(300L, "INR")
        val parts = m.split(3)
        assertEquals(3, parts.size)
        assertEquals(100L, parts[0].amountMinor)
        assertEquals(100L, parts[1].amountMinor)
        assertEquals(100L, parts[2].amountMinor)
        assertEquals(m.amountMinor, parts.sumOf { it.amountMinor })
    }

    @Test
    fun testZeroLossSplitWithRemainder() {
        // ₹1.00 split 3 ways: 100 paise / 3 = 34, 33, 33 (Total = 100)
        val m = Money(100L, "INR")
        val parts = m.split(3)
        assertEquals(3, parts.size)
        assertEquals(34L, parts[0].amountMinor)
        assertEquals(33L, parts[1].amountMinor)
        assertEquals(33L, parts[2].amountMinor)
        assertEquals(100L, parts.sumOf { it.amountMinor })
    }

    @Test
    fun testZeroLossSplitComplexPrimeParts() {
        val m = Money(1000000L, "INR") // ₹10,000.00
        val parts = m.split(7)
        assertEquals(7, parts.size)
        assertEquals(m.amountMinor, parts.sumOf { it.amountMinor })
    }

    @Test
    fun testZeroLossSplitNegativeAmount() {
        val m = Money(-100L, "INR")
        val parts = m.split(3)
        assertEquals(3, parts.size)
        assertEquals(-34L, parts[0].amountMinor)
        assertEquals(-33L, parts[1].amountMinor)
        assertEquals(-33L, parts[2].amountMinor)
        assertEquals(m.amountMinor, parts.sumOf { it.amountMinor })
    }

    @Test
    fun testProportionalAllocation() {
        val m = Money(100L, "USD")
        val allocated = m.allocate(listOf(1, 2, 1))
        assertEquals(3, allocated.size)
        assertEquals(25L, allocated[0].amountMinor)
        assertEquals(50L, allocated[1].amountMinor)
        assertEquals(25L, allocated[2].amountMinor)
        assertEquals(100L, allocated.sumOf { it.amountMinor })
    }

    @Test
    fun testIndianNumberFormatting() {
        // 1 lakh = 100,000 rupees -> 10,000,000 minor units
        val oneLakh = Money(10000000L, "INR")
        assertEquals("₹1,00,000.00", oneLakh.format(style = NumberGroupingStyle.INDIAN_LAKH_CRORE))

        // 12 lakh 34 thousand 567 rupees 89 paise
        val complex = Money(123456789L, "INR")
        assertEquals("₹12,34,567.89", complex.format(style = NumberGroupingStyle.INDIAN_LAKH_CRORE))
    }

    @Test
    fun testWesternNumberFormatting() {
        val million = Money(100000000L, "USD")
        assertEquals("$1,000,000.00", million.format(style = NumberGroupingStyle.WESTERN_MILLION_BILLION))

        val complex = Money(123456789L, "USD")
        assertEquals("$1,234,567.89", complex.format(style = NumberGroupingStyle.WESTERN_MILLION_BILLION))
    }

    @Test
    fun testMajorConversion() {
        val m = Money.fromMajor(150.75, "INR")
        assertEquals(15075L, m.amountMinor)
        assertEquals(BigDecimal("150.75"), m.toMajorBigDecimal())
    }

    @Test
    fun testComparison() {
        val m1 = Money(1000L, "INR")
        val m2 = Money(2000L, "INR")
        val m3 = Money(1000L, "INR")

        assertTrue(m1 < m2)
        assertTrue(m2 > m1)
        assertEquals(0, m1.compareTo(m3))
    }
}
