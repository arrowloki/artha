package com.artha.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

enum class UiDensity(val itemPadding: Dp, val cornerRadius: Dp) {
    COMPACT(itemPadding = 8.dp, cornerRadius = 12.dp),
    COMFORTABLE(itemPadding = 14.dp, cornerRadius = 20.dp),
    SPACIOUS(itemPadding = 20.dp, cornerRadius = 28.dp)
}

@Immutable
data class ArthaColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val expense: Color,
    val onExpense: Color,
    val income: Color,
    val onIncome: Color,
    val transfer: Color,
    val onTransfer: Color,
    val warning: Color,
    val cardBorder: Color,
    val divider: Color,
    val isDark: Boolean
)

@Immutable
data class ArthaShapes(
    val small: Dp = 10.dp,
    val medium: Dp = 18.dp,
    val large: Dp = 26.dp,
    val cardCorner: Dp = 20.dp
)

@Immutable
data class ArthaTypographyConfig(
    val textScaleMultiplier: Float = 1.0f,
    val amountTextScaleMultiplier: Float = 1.0f,
    val useTabularNumbers: Boolean = true
)

val LocalArthaColors = staticCompositionLocalOf<ArthaColors> {
    error("LocalArthaColors not provided")
}

val LocalArthaShapes = staticCompositionLocalOf {
    ArthaShapes()
}

val LocalArthaDensity = staticCompositionLocalOf {
    UiDensity.COMFORTABLE
}

val LocalArthaTypographyConfig = staticCompositionLocalOf {
    ArthaTypographyConfig()
}
