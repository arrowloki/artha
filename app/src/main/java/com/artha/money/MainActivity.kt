package com.artha.money

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.artha.data.preferences.UserPreferences
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.DataTransferRepository
import com.artha.data.repository.RuleRepository
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

class MainActivity : FragmentActivity() {

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

            // Dynamic FLAG_SECURE for screenshot & recents privacy
            LaunchedEffect(userPrefs.isHideBalancesInRecents) {
                if (userPrefs.isHideBalancesInRecents) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            var isAppUnlocked by remember { mutableStateOf(!userPrefs.isBiometricEnabled) }

            // Auto-prompt biometrics if enabled and locked
            LaunchedEffect(userPrefs.isBiometricEnabled) {
                if (userPrefs.isBiometricEnabled && !isAppUnlocked) {
                    promptBiometrics { isAppUnlocked = true }
                }
            }

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
                if (userPrefs.isBiometricEnabled && !isAppUnlocked) {
                    AppLockScreen(
                        onUnlockClick = { promptBiometrics { isAppUnlocked = true } }
                    )
                } else {
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
                        categoryRepository = app.categoryRepository,
                        dataTransferRepository = app.dataTransferRepository
                    )
                }
            }
        }
    }

    private fun promptBiometrics(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Artha")
            .setSubtitle("Authenticate to access your private encrypted ledger")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        prompt.authenticate(promptInfo)
    }
}

@Composable
fun AppLockScreen(
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Locked",
                    tint = colors.primary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Artha is Locked",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Biometric protection is enabled for your private encrypted ledger.",
                fontSize = 13.sp,
                color = colors.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onUnlockClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fingerprint,
                    contentDescription = null,
                    tint = colors.background,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Unlock with Biometrics",
                    fontWeight = FontWeight.Bold,
                    color = colors.background
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
    ruleRepository: RuleRepository? = null,
    categoryRepository: CategoryRepository? = null,
    dataTransferRepository: DataTransferRepository? = null
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
                    categoryRepository = categoryRepository,
                    dataTransferRepository = dataTransferRepository
                )
            }
        }
    }
}
