package com.artha.ui.theme

import androidx.compose.ui.graphics.Color

object ThemePresets {

    // 1. Signature Emerald Wealth
    val EmeraldWealthDark = ArthaColors(
        primary = Color(0xFF10B981),        // Radiant Emerald
        onPrimary = Color(0xFF022C22),
        primaryContainer = Color(0xFF064E3B),
        onPrimaryContainer = Color(0xFFA7F3D0),
        background = Color(0xFF0F172A),      // Deep Slate
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),         // Card Surface
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = Color(0xFF334155),
        onSurfaceVariant = Color(0xFFCBD5E1),
        expense = Color(0xFFF43F5E),         // Rose
        onExpense = Color.White,
        income = Color(0xFF10B981),          // Emerald
        onIncome = Color.White,
        transfer = Color(0xFF38BDF8),        // Sky Blue
        onTransfer = Color.White,
        warning = Color(0xFFF59E0B),         // Amber
        cardBorder = Color(0x26FFFFFF),
        divider = Color(0x1AFFFFFF),
        isDark = true
    )

    val EmeraldWealthLight = ArthaColors(
        primary = Color(0xFF059669),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD1FAE5),
        onPrimaryContainer = Color(0xFF065F46),
        background = Color(0xFFF8FAFC),
        onBackground = Color(0xFF0F172A),
        surface = Color.White,
        onSurface = Color(0xFF1E293B),
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF475569),
        expense = Color(0xFFE11D48),
        onExpense = Color.White,
        income = Color(0xFF059669),
        onIncome = Color.White,
        transfer = Color(0xFF0284C7),
        onTransfer = Color.White,
        warning = Color(0xFFD97706),
        cardBorder = Color(0x1A000000),
        divider = Color(0x0F000000),
        isDark = false
    )

    // 2. Midnight AMOLED (True 0x000000 Black)
    val MidnightAmoled = ArthaColors(
        primary = Color(0xFF00E676),        // High Contrast Mint
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF00381B),
        onPrimaryContainer = Color(0xFFB9F6CA),
        background = Color(0xFF000000),      // Pure Pitch Black
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF121212),         // Dark OLED Surface
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF1E1E1E),
        onSurfaceVariant = Color(0xFFAAAAAA),
        expense = Color(0xFFFF5252),
        onExpense = Color.White,
        income = Color(0xFF00E676),
        onIncome = Color.Black,
        transfer = Color(0xFF40C4FF),
        onTransfer = Color.Black,
        warning = Color(0xFFFFD740),
        cardBorder = Color(0x33FFFFFF),
        divider = Color(0x22FFFFFF),
        isDark = true
    )

    // 3. Nordic Slate
    val NordicSlateDark = ArthaColors(
        primary = Color(0xFF818CF8),        // Indigo
        onPrimary = Color(0xFF1E1B4B),
        primaryContainer = Color(0xFF312E81),
        onPrimaryContainer = Color(0xFFE0E7FF),
        background = Color(0xFF18181B),
        onBackground = Color(0xFFF4F4F5),
        surface = Color(0xFF27272A),
        onSurface = Color(0xFFF4F4F5),
        surfaceVariant = Color(0xFF3F3F46),
        onSurfaceVariant = Color(0xFFA1A1AA),
        expense = Color(0xFFFB7185),
        onExpense = Color.White,
        income = Color(0xFF34D399),
        onIncome = Color.White,
        transfer = Color(0xFF60A5FA),
        onTransfer = Color.White,
        warning = Color(0xFFFBBF24),
        cardBorder = Color(0x26FFFFFF),
        divider = Color(0x1AFFFFFF),
        isDark = true
    )
}
