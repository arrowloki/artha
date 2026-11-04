package com.artha.money

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.artha.data.preferences.UserPreferences
import com.artha.model.Money
import com.artha.model.NumberGroupingStyle
import com.artha.ui.theme.ArthaTheme
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import com.artha.ui.theme.ThemeMode
import com.artha.ui.theme.UiDensity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-Edge full immersion
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        val prefsRepo = (application as ArthaApp).preferencesRepository

        setContent {
            val userPrefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())

            val themeMode = when (userPrefs.themeMode) {
                "AMOLED" -> ThemeMode.AMOLED
                "DARK" -> ThemeMode.DARK
                "LIGHT" -> ThemeMode.LIGHT
                else -> ThemeMode.SYSTEM
            }

            val density = when (userPrefs.uiDensity) {
                "COMPACT" -> UiDensity.COMPACT
                "SPACIOUS" -> UiDensity.SPACIOUS
                else -> UiDensity.COMFORTABLE
            }

            val isDark = when (themeMode) {
                ThemeMode.AMOLED, ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }

            ArthaTheme(
                mode = themeMode,
                density = density
            ) {
                Surface(
                    color = LocalArthaColors.current.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    FoundationDashboardScreen()
                }
            }
        }
    }
}

@Composable
fun FoundationDashboardScreen() {
    val colors = LocalArthaColors.current
    val density = LocalArthaDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Artha",
                    color = colors.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Milestone 1 · Foundation Verified",
                    color = colors.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalance,
                    contentDescription = "Vault",
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Net Worth Card preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(density.cornerRadius))
                .background(colors.surface)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(density.cornerRadius))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "TOTAL NET WORTH",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = Money(0L, "INR").format(style = NumberGroupingStyle.INDIAN_LAKH_CRORE),
                    color = colors.onSurface,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Encrypted with SQLCipher & Android Keystore",
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "VERIFIED FOUNDATION MODULES",
            color = colors.onSurfaceVariant,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Module Feature List
        FoundationFeatureItem(
            icon = Icons.Rounded.Speed,
            title = "Strict Minor-Unit Money Math",
            subtitle = "64-bit Long storage with Banker's Rounding & zero-loss split",
            colors = colors,
            cornerRadius = density.cornerRadius
        )

        Spacer(modifier = Modifier.height(10.dp))

        FoundationFeatureItem(
            icon = Icons.Rounded.CreditCard,
            title = "Single-Count Credit Card Rule",
            subtitle = "Spending recorded at purchase, bill payment is a non-spending transfer",
            colors = colors,
            cornerRadius = density.cornerRadius
        )

        Spacer(modifier = Modifier.height(10.dp))

        FoundationFeatureItem(
            icon = Icons.Rounded.Wallet,
            title = "Instant Incremental Aggregates",
            subtitle = "Pre-computed monthly summary tables updated in DB transactions",
            colors = colors,
            cornerRadius = density.cornerRadius
        )

        Spacer(modifier = Modifier.height(10.dp))

        FoundationFeatureItem(
            icon = Icons.Rounded.CheckCircle,
            title = "Live Token Design System",
            subtitle = "WCAG 2.1 contrast checker with Material You & AMOLED support",
            colors = colors,
            cornerRadius = density.cornerRadius
        )
    }
}

@Composable
private fun FoundationFeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: com.artha.ui.theme.ArthaColors,
    cornerRadius: androidx.compose.ui.unit.Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(colors.surface)
            .border(0.5.dp, colors.cardBorder, RoundedCornerShape(cornerRadius))
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = colors.onSurfaceVariant,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}
