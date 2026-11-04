package com.artha.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastCheckerTest {

    @Test
    fun testBlackAndWhiteContrast() {
        val result = ContrastChecker.calculateContrast(Color.Black, Color.White)
        assertEquals(21.0, result.ratio, 0.1)
        assertEquals(WcagCompliance.AAA, result.compliance)
        assertTrue(result.isAccessibleForBodyText)
        assertTrue(result.isAccessibleForLargeText)
    }

    @Test
    fun testLowContrastFails() {
        // Very light gray on white background
        val lightGray = Color(0xFFE0E0E0)
        val result = ContrastChecker.calculateContrast(lightGray, Color.White)

        assertTrue(result.ratio < 3.0)
        assertEquals(WcagCompliance.FAIL, result.compliance)
        assertFalse(result.isAccessibleForBodyText)
        assertFalse(result.isAccessibleForLargeText)
    }

    @Test
    fun testEmeraldWealthColorsContrast() {
        // Dark theme: OnSurface text on Surface background
        val darkSurface = ThemePresets.EmeraldWealthDark.surface
        val darkOnSurface = ThemePresets.EmeraldWealthDark.onSurface
        val darkResult = ContrastChecker.calculateContrast(darkOnSurface, darkSurface)
        assertTrue("Dark theme onSurface contrast must pass AA", darkResult.ratio >= 4.5)

        // Light theme: OnSurface text on Surface background
        val lightSurface = ThemePresets.EmeraldWealthLight.surface
        val lightOnSurface = ThemePresets.EmeraldWealthLight.onSurface
        val lightResult = ContrastChecker.calculateContrast(lightOnSurface, lightSurface)
        assertTrue("Light theme onSurface contrast must pass AA", lightResult.ratio >= 4.5)
    }

    @Test
    fun testRelativeLuminance() {
        assertEquals(0.0, ContrastChecker.relativeLuminance(Color.Black), 0.001)
        assertEquals(1.0, ContrastChecker.relativeLuminance(Color.White), 0.001)
    }
}
