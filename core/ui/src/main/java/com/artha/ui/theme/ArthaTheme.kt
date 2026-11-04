package com.artha.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ArthaTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    customColors: ArthaColors? = null,
    density: UiDensity = UiDensity.COMFORTABLE,
    typographyConfig: ArthaTypographyConfig = ArthaTypographyConfig(),
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()

    val effectiveColors = when {
        customColors != null -> customColors
        mode == ThemeMode.AMOLED -> ThemePresets.MidnightAmoled
        mode == ThemeMode.DARK -> ThemePresets.EmeraldWealthDark
        mode == ThemeMode.LIGHT -> ThemePresets.EmeraldWealthLight
        systemInDark -> ThemePresets.EmeraldWealthDark
        else -> ThemePresets.EmeraldWealthLight
    }

    val materialColors = if (effectiveColors.isDark) {
        darkColorScheme(
            primary = effectiveColors.primary,
            onPrimary = effectiveColors.onPrimary,
            primaryContainer = effectiveColors.primaryContainer,
            onPrimaryContainer = effectiveColors.onPrimaryContainer,
            background = effectiveColors.background,
            onBackground = effectiveColors.onBackground,
            surface = effectiveColors.surface,
            onSurface = effectiveColors.onSurface,
            surfaceVariant = effectiveColors.surfaceVariant,
            onSurfaceVariant = effectiveColors.onSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = effectiveColors.primary,
            onPrimary = effectiveColors.onPrimary,
            primaryContainer = effectiveColors.primaryContainer,
            onPrimaryContainer = effectiveColors.onPrimaryContainer,
            background = effectiveColors.background,
            onBackground = effectiveColors.onBackground,
            surface = effectiveColors.surface,
            onSurface = effectiveColors.onSurface,
            surfaceVariant = effectiveColors.surfaceVariant,
            onSurfaceVariant = effectiveColors.onSurfaceVariant
        )
    }

    val shapes = ArthaShapes(
        cardCorner = density.cornerRadius
    )

    CompositionLocalProvider(
        LocalArthaColors provides effectiveColors,
        LocalArthaShapes provides shapes,
        LocalArthaDensity provides density,
        LocalArthaTypographyConfig provides typographyConfig
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}
