package com.artha.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

enum class WcagCompliance {
    AAA,      // Contrast ratio >= 7.0:1
    AA,       // Contrast ratio >= 4.5:1
    AA_LARGE, // Contrast ratio >= 3.0:1
    FAIL      // Contrast ratio < 3.0:1
}

data class ContrastResult(
    val ratio: Double,
    val compliance: WcagCompliance,
    val isAccessibleForBodyText: Boolean,
    val isAccessibleForLargeText: Boolean
)

/**
 * W3C WCAG 2.1 algorithm for relative luminance and contrast ratio checking.
 */
object ContrastChecker {

    /**
     * Calculates the contrast ratio between foreground and background colors.
     * Guaranteed result in range [1.0, 21.0].
     */
    fun calculateContrast(foreground: Color, background: Color): ContrastResult {
        val l1 = relativeLuminance(foreground)
        val l2 = relativeLuminance(background)

        val lighter = max(l1, l2)
        val darker = min(l1, l2)

        val ratio = (lighter + 0.05) / (darker + 0.05)
        val roundedRatio = Math.round(ratio * 100.0) / 100.0

        val compliance = when {
            roundedRatio >= 7.0 -> WcagCompliance.AAA
            roundedRatio >= 4.5 -> WcagCompliance.AA
            roundedRatio >= 3.0 -> WcagCompliance.AA_LARGE
            else -> WcagCompliance.FAIL
        }

        return ContrastResult(
            ratio = roundedRatio,
            compliance = compliance,
            isAccessibleForBodyText = roundedRatio >= 4.5,
            isAccessibleForLargeText = roundedRatio >= 3.0
        )
    }

    /**
     * Calculates sRGB relative luminance according to WCAG 2.1 specification.
     */
    fun relativeLuminance(color: Color): Double {
        val r = linearizeChannel(color.red)
        val g = linearizeChannel(color.green)
        val b = linearizeChannel(color.blue)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun linearizeChannel(component: Float): Double {
        val c = component.toDouble()
        return if (c <= 0.04045) {
            c / 12.92
        } else {
            ((c + 0.055) / 1.055).pow(2.4)
        }
    }
}
