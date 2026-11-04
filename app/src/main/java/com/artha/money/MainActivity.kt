package com.artha.money

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.artha.data.preferences.UserPreferences
import com.artha.feature.accounts.AccountsViewModel
import com.artha.feature.accounts.list.AccountListScreen
import com.artha.feature.insight.InsightScreen
import com.artha.feature.insight.InsightViewModel
import com.artha.feature.planning.PlanningScreen
import com.artha.feature.planning.PlanningViewModel
import com.artha.feature.transactions.TransactionsViewModel
import com.artha.feature.transactions.list.TransactionListScreen
import com.artha.money.settings.SettingsScreen
import com.artha.ui.theme.ArthaTheme
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.ThemeMode
import com.artha.ui.theme.UiDensity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-Edge immersion
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        val app = application as ArthaApp
        val prefsRepo = app.preferencesRepository

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
                val txViewModel = remember {
                    TransactionsViewModel(
                        transactionRepository = app.transactionRepository,
                        accountRepository = app.accountRepository,
                        categoryRepository = app.categoryRepository
                    )
                }

                val accountsViewModel = remember {
                    AccountsViewModel(
                        accountRepository = app.accountRepository,
                        transactionRepository = app.transactionRepository
                    )
                }

                val planningViewModel = remember {
                    PlanningViewModel(
                        budgetRepository = app.budgetRepository,
                        billSubscriptionRepository = app.billSubscriptionRepository,
                        goalRepository = app.goalRepository,
                        categoryRepository = app.categoryRepository,
                        transactionRepository = app.transactionRepository,
                        accountRepository = app.accountRepository
                    )
                }

                val insightViewModel = remember {
                    InsightViewModel(
                        transactionRepository = app.transactionRepository,
                        categoryRepository = app.categoryRepository,
                        debtLoanRepository = app.debtLoanRepository
                    )
                }

                MainAppScreen(
                    txViewModel = txViewModel,
                    accountsViewModel = accountsViewModel,
                    planningViewModel = planningViewModel,
                    insightViewModel = insightViewModel,
                    userPreferences = userPrefs,
                    preferencesRepository = prefsRepo,
                    ruleRepository = app.ruleRepository,
                    categoryRepository = app.categoryRepository
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    txViewModel: TransactionsViewModel,
    accountsViewModel: AccountsViewModel,
    planningViewModel: PlanningViewModel,
    insightViewModel: InsightViewModel,
    userPreferences: UserPreferences,
    preferencesRepository: com.artha.data.preferences.UserPreferencesRepository,
    ruleRepository: com.artha.data.repository.RuleRepository? = null,
    categoryRepository: com.artha.data.repository.CategoryRepository? = null
) {
    val colors = LocalArthaColors.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        Triple("Transactions", Icons.AutoMirrored.Rounded.ReceiptLong, 0),
        Triple("Accounts", Icons.Rounded.AccountBalance, 1),
        Triple("Planning", Icons.Rounded.TrackChanges, 2),
        Triple("Insights", Icons.AutoMirrored.Rounded.ShowChart, 3),
        Triple("Settings", Icons.Rounded.Settings, 4)
    )

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { (label, icon, index) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary,
                            selectedTextColor = colors.primary,
                            indicatorColor = colors.primary.copy(alpha = 0.15f),
                            unselectedIconColor = colors.onBackground.copy(alpha = 0.5f),
                            unselectedTextColor = colors.onBackground.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TransactionListScreen(viewModel = txViewModel)
                1 -> AccountListScreen(viewModel = accountsViewModel)
                2 -> PlanningScreen(viewModel = planningViewModel)
                3 -> InsightScreen(viewModel = insightViewModel)
                4 -> SettingsScreen(
                    userPreferences = userPreferences,
                    preferencesRepository = preferencesRepository,
                    ruleRepository = ruleRepository,
                    categoryRepository = categoryRepository
                )
            }
        }
    }
}
