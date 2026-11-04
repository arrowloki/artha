package com.artha.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class MathExpressionEvaluatorTest {

    @Test
    fun testSimpleNumber() {
        val result = MathExpressionEvaluator.evaluate("150")
        assertNotNull(result)
        assertEquals(0, BigDecimal("150").compareTo(result))
    }

    @Test
    fun testPrecedenceMultiplicationFirst() {
        // 120 + 45 * 2 = 120 + 90 = 210
        val result = MathExpressionEvaluator.evaluate("120+45*2")
        assertNotNull(result)
        assertEquals(0, BigDecimal("210").compareTo(result))
    }

    @Test
    fun testAdditionAndSubtraction() {
        // 500 - 200 + 50 = 350
        val result = MathExpressionEvaluator.evaluate("500-200+50")
        assertNotNull(result)
        assertEquals(0, BigDecimal("350").compareTo(result))
    }

    @Test
    fun testDivision() {
        val result = MathExpressionEvaluator.evaluate("500/2")
        assertNotNull(result)
        assertEquals(0, BigDecimal("250").compareTo(result))
    }

    @Test
    fun testTrailingOperatorForLivePreview() {
        val result1 = MathExpressionEvaluator.evaluate("120+")
        assertNotNull(result1)
        assertEquals(0, BigDecimal("120").compareTo(result1))

        val result2 = MathExpressionEvaluator.evaluate("45*")
        assertNotNull(result2)
        assertEquals(0, BigDecimal("45").compareTo(result2))
    }

    @Test
    fun testDecimals() {
        val result = MathExpressionEvaluator.evaluate("10.50*2")
        assertNotNull(result)
        assertEquals(0, BigDecimal("21").compareTo(result))
    }

    @Test
    fun testEmptyOrInvalid() {
        assertNull(MathExpressionEvaluator.evaluate(""))
        assertNull(MathExpressionEvaluator.evaluate("   "))
        assertNull(MathExpressionEvaluator.evaluate("+"))
    }
}
