package com.artha.money.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Rule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.data.preferences.UserPreferences
import com.artha.data.preferences.UserPreferencesRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.RuleRepository
import com.artha.database.entity.RuleEntity
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    preferencesRepository: UserPreferencesRepository,
    ruleRepository: RuleRepository? = null,
    categoryRepository: CategoryRepository? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val density = LocalArthaDensity.current
    val coroutineScope = rememberCoroutineScope()

    val rules by (ruleRepository?.getAllRulesFlow() ?: flowOf(emptyList())).collectAsState(initial = emptyList())
    val categories by (categoryRepository?.getActiveCategoriesFlow() ?: flowOf(emptyList())).collectAsState(initial = emptyList())

    var showAddRuleSheet by remember { mutableStateOf(false) }
    var rerunStatusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )
        Text(
            text = "Privacy, Security & Customization",
            fontSize = 13.sp,
            color = colors.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Security,
                        contentDescription = "Encrypted",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Encrypted & Offline-First",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "SQLCipher with 256-bit AES key managed via Android Keystore. Zero cloud transmission.",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SMART RULES ENGINE SECTION ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SMART RULES ENGINE (${rules.count { it.isActive }} ACTIVE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )

            IconButton(onClick = { showAddRuleSheet = true }) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Rule",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Rerun Rules Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Batch Re-run Engine",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Apply active rules across all past transactions",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val updated = ruleRepository?.rerunAllRulesOnHistory() ?: 0
                                rerunStatusMessage = "Updated $updated transactions with rules!"
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Re-run", fontSize = 12.sp, color = colors.primary)
                    }
                }

                if (rerunStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rerunStatusMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Active Rules
        rules.forEach { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rule.isActive) colors.surface else colors.surface.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rule.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        val conditionDesc = "If ${rule.conditionField} ${rule.conditionOperator.lowercase()} \"${rule.conditionValue}\""
                        val actionDesc = "Then ${rule.actionField.lowercase().replace("_", " ")}"
                        Text(
                            text = "$conditionDesc → $actionDesc",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = rule.isActive,
                            onCheckedChange = { checked ->
                                coroutineScope.launch {
                                    ruleRepository?.updateRule(rule.copy(isActive = checked))
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.background,
                                checkedTrackColor = colors.primary
                            )
                        )

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    ruleRepository?.deleteRule(rule.id)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                tint = colors.onBackground.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- PRIVACY & SECURITY TOGGLES ---
        Text(
            text = "PRIVACY & SECURITY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Biometrics Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biometric Lock",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Require fingerprint / face unlock to open app",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Switch(
                    checked = userPreferences.isBiometricEnabled,
                    onCheckedChange = { enabled ->
                        coroutineScope.launch {
                            preferencesRepository.setBiometricEnabled(enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.background,
                        checkedTrackColor = colors.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Screenshot Protection Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Screenshot & Recents Protection",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Block screenshots and obscure app in recents overview",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Switch(
                    checked = userPreferences.isHideBalancesInRecents,
                    onCheckedChange = { hide ->
                        coroutineScope.launch {
                            preferencesRepository.setHideBalancesInRecents(hide)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.background,
                        checkedTrackColor = colors.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- THEME PRESETS ---
        Text(
            text = "THEME & APPEARANCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        val themes = listOf(
            Triple("EMERALD", "Emerald Wealth", "Light & Dark with Emerald green accents"),
            Triple("AMOLED", "Midnight AMOLED", "Pure #000000 true black for OLED displays"),
            Triple("NORDIC", "Nordic Slate", "Minimalist matte grey-slate design")
        )

        themes.forEach { (mode, name, desc) ->
            val isSelected = userPreferences.themeMode == mode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        coroutineScope.launch {
                            preferencesRepository.updateThemeMode(mode)
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, colors.primary) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Selected",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- UI DENSITY ---
        Text(
            text = "LAYOUT DENSITY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        val densities = listOf(
            Pair("COMPACT", "Compact (Max density for power users)"),
            Pair("COMFORTABLE", "Comfortable (Balanced spacing)"),
            Pair("SPACIOUS", "Spacious (Large touch targets)")
        )

        densities.forEach { (dens, label) ->
            val isSelected = userPreferences.uiDensity == dens
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        coroutineScope.launch {
                            preferencesRepository.updateUiDensity(dens)
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, colors.primary) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = colors.onBackground
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Selected",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showAddRuleSheet) {
        AddRuleSheet(
            availableCategories = categories,
            onDismiss = { showAddRuleSheet = false },
            onSaveRule = { newRule ->
                coroutineScope.launch {
                    ruleRepository?.insertRule(newRule)
                }
            }
        )
    }
}
