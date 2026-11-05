package com.artha.money.settings

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Rule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.data.preferences.UserPreferences
import com.artha.data.preferences.UserPreferencesRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.DataTransferRepository
import com.artha.data.repository.RuleRepository
import com.artha.database.entity.RuleEntity
import com.artha.feature.transactions.edit.CurrencyPickerDialog
import com.artha.model.CurrencyConfig
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    preferencesRepository: UserPreferencesRepository,
    ruleRepository: RuleRepository? = null,
    categoryRepository: CategoryRepository? = null,
    dataTransferRepository: DataTransferRepository? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val density = LocalArthaDensity.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val rules by (ruleRepository?.getAllRulesFlow() ?: flowOf(emptyList())).collectAsState(initial = emptyList())
    val categories by (categoryRepository?.getActiveCategoriesFlow() ?: flowOf(emptyList())).collectAsState(initial = emptyList())

    var showAddRuleSheet by remember { mutableStateOf(false) }
    var rerunStatusMessage by remember { mutableStateOf<String?>(null) }
    var transferMessage by remember { mutableStateOf<String?>(null) }
    var showBankCsvDialog by remember { mutableStateOf(false) }
    var bankCsvText by remember { mutableStateOf("") }

    var showCurrencyChooser by remember { mutableStateOf(false) }
    var showGoogleAccountDialog by remember { mutableStateOf(false) }
    var googleAccountEmailInput by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current
    var showGoogleCloudConfigDialog by remember { mutableStateOf(false) }
    var webClientIdInput by remember { mutableStateOf(userPreferences.googleWebClientId ?: "") }

    // Native Android System Account Picker Launcher (Zero setup required)
    val accountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                coroutineScope.launch {
                    preferencesRepository.linkGoogleAccount(accountName)
                    transferMessage = "✅ Linked Google Account: $accountName"
                }
            }
        }
    }

    // Google Play Services Auth Launcher (when OAuth Web Client ID is configured)
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val email = account.email ?: account.displayName ?: "Connected Account"
                coroutineScope.launch {
                    preferencesRepository.linkGoogleAccount(email)
                    transferMessage = "✅ Signed in with Google: $email"
                }
            } catch (e: ApiException) {
                // If developer error (code 10), fallback immediately to native account picker
                try {
                    val intent = AccountManager.newChooseAccountIntent(
                        null, null, arrayOf("com.google"), null, null, null, null
                    )
                    accountPickerLauncher.launch(intent)
                } catch (_: Exception) {
                    transferMessage = "⚠️ Google Play Services (${e.statusCode}): Setup Google Cloud Client ID."
                    showGoogleCloudConfigDialog = true
                }
            }
        }
    }

    // Storage Access Framework (SAF) - Direct Save to Google Drive / Files
    val saveToDriveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = dataTransferRepository?.exportFullJsonBackup() ?: "{}"
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray(Charsets.UTF_8))
                        os.flush()
                    }
                    preferencesRepository.recordBackup()
                    transferMessage = "✅ Successfully saved backup to Google Drive / Files!"
                } catch (e: Exception) {
                    transferMessage = "❌ Backup failed: ${e.localizedMessage}"
                }
            }
        }
    }

    // Storage Access Framework (SAF) - Direct Restore from Google Drive / Files
    val restoreFromDriveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { isStream ->
                        isStream.bufferedReader().readText()
                    } ?: ""
                    if (json.isNotBlank()) {
                        val count = dataTransferRepository?.importJsonBackup(json) ?: 0
                        transferMessage = "✅ Successfully restored $count items from Google Drive file!"
                    } else {
                        transferMessage = "⚠️ Selected backup file was empty."
                    }
                } catch (e: Exception) {
                    transferMessage = "❌ Restore failed: ${e.localizedMessage}"
                }
            }
        }
    }

    val triggerGoogleSignIn: () -> Unit = {
        val webClientId = userPreferences.googleWebClientId
        if (!webClientId.isNullOrBlank()) {
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(context, gso)
                googleSignInLauncher.launch(client.signInIntent)
            } catch (e: Exception) {
                try {
                    val intent = AccountManager.newChooseAccountIntent(
                        null, null, arrayOf("com.google"), null, null, null, null
                    )
                    accountPickerLauncher.launch(intent)
                } catch (_: Exception) {
                    showGoogleAccountDialog = true
                }
            }
        } else {
            try {
                val intent = AccountManager.newChooseAccountIntent(
                    null, null, arrayOf("com.google"), null, null, null, null
                )
                accountPickerLauncher.launch(intent)
            } catch (e: Exception) {
                showGoogleAccountDialog = true
            }
        }
    }

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
            text = "Privacy, Security & Cloud Sync",
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
                        text = "SQLCipher with 256-bit AES key managed via Android Keystore. Zero telemetry, 100% on-device.",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ACCOUNT & GOOGLE DRIVE CLOUD BACKUP ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACCOUNT & GOOGLE DRIVE BACKUP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            IconButton(
                onClick = { showGoogleCloudConfigDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Cloud Credentials Setup",
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val isLinked = userPreferences.linkedGoogleAccount != null
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isLinked) colors.primary.copy(alpha = 0.15f) else colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLinked) Icons.Rounded.CloudDone else Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            tint = if (isLinked) colors.primary else colors.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLinked) "Google Account Connected" else "Google Account Not Linked",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = if (isLinked) userPreferences.linkedGoogleAccount!! else "Sign in to enable 1-tap Google Drive cloud sync",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    if (isLinked) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    preferencesRepository.unlinkGoogleAccount()
                                    transferMessage = "Google Account unlinked."
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Unlink", fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = { triggerGoogleSignIn() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Text("Sign In", fontSize = 12.sp, color = colors.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Backup Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val defaultName = "Artha_Encrypted_Backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.json"
                            saveToDriveLauncher.launch(defaultName)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.15f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.CloudUpload, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup to Drive", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            restoreFromDriveLauncher.launch(arrayOf("application/json", "*/*"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Data", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary options: Share sheet & Manual JSON paste
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share via App / Email",
                        fontSize = 11.sp,
                        color = colors.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                coroutineScope.launch {
                                    val json = dataTransferRepository?.exportFullJsonBackup() ?: ""
                                    preferencesRepository.recordBackup()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, json)
                                        putExtra(Intent.EXTRA_TITLE, "Artha_Encrypted_Backup_${System.currentTimeMillis()}.json")
                                        type = "application/json"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share Artha Backup")
                                    context.startActivity(shareIntent)
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    Text(
                        text = "Paste JSON Manually",
                        fontSize = 11.sp,
                        color = colors.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showRestoreDialog = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Status transferMessage alert banner
                if (transferMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transferMessage!!,
                                fontSize = 11.5.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                fontSize = 12.sp,
                                color = colors.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clickable { transferMessage = null }
                                    .padding(start = 6.dp)
                            )
                        }
                    }
                }

                // Last backup timestamp indicator
                if (userPreferences.lastBackupTimestamp > 0L) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val formattedTime = sdf.format(Date(userPreferences.lastBackupTimestamp))
                    Text(
                        text = "Last Google Drive backup: $formattedTime • 256-bit encrypted",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto-backup toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Backup to Drive",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Daily background snapshot to cloud folder",
                            fontSize = 11.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = userPreferences.isAutoDriveBackupEnabled,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                preferencesRepository.setAutoDriveBackupEnabled(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.background,
                            checkedTrackColor = colors.primary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- PRIMARY BASE CURRENCY & FORMAT ---
        Text(
            text = "PRIMARY BASE CURRENCY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val currentBaseConfig = CurrencyConfig.forCode(userPreferences.baseCurrency)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { showCurrencyChooser = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = currentBaseConfig.flag, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${currentBaseConfig.code} — ${currentBaseConfig.name}",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${currentBaseConfig.symbol})",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                        Text(
                            text = "Tap to choose default currency for new entries & dashboards",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Button(
                    onClick = { showCurrencyChooser = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.15f))
                ) {
                    Text("Change", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.Bold)
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

        // --- DATA BACKUP & IMPORT ---
        Text(
            text = "DATA PORTABILITY & IMPORT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Export Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Offline Export & Bank Statement Engine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackground
                )
                Text(
                    text = "Export standard CSV or import bank statements from HDFC, SBI, ICICI, etc.",
                    fontSize = 11.5.sp,
                    color = colors.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val csv = dataTransferRepository?.exportAllTransactionsCsv() ?: ""
                                val lineCount = maxOf(0, csv.lines().size - 2)
                                transferMessage = "Exported $lineCount transactions as CSV!"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/csv"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share / Save CSV"))
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV Export", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val json = dataTransferRepository?.exportFullJsonBackup() ?: ""
                                transferMessage = "Full JSON backup generated (${json.length} bytes)!"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, json)
                                    type = "application/json"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share / Save JSON"))
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("JSON Backup", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showBankCsvDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.FileUpload, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import Bank Statement (CSV)", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
                }

                if (transferMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = transferMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
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

    if (showBankCsvDialog) {
        AlertDialog(
            onDismissRequest = { showBankCsvDialog = false },
            title = {
                Text(
                    text = "Import Bank Statement (CSV)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Paste CSV rows or statement text below. Artha auto-detects HDFC, ICICI, SBI, Chase and standard bank formats.",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = bankCsvText,
                        onValueChange = { bankCsvText = it },
                        placeholder = { Text("Date,Description,Debit,Credit,Balance...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val imported = dataTransferRepository?.importBankStatementCsv(bankCsvText, "acc_primary_bank") ?: 0
                            transferMessage = "Imported $imported bank transactions!"
                            showBankCsvDialog = false
                            bankCsvText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Import", color = colors.background, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBankCsvDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Google Cloud OAuth Credentials & Keys Setup Dialog
    if (showGoogleCloudConfigDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleCloudConfigDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Sign-In & Drive Setup",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Artha supports 1-tap native device account selection automatically. To connect official Google Play Services OAuth ID tokens or custom Google Cloud Drive sync, register this app in your Google Cloud Console:",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Package Name Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Package Name", fontSize = 10.sp, color = colors.onSurfaceVariant.copy(alpha = 0.6f))
                                Text("com.artha.money", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("com.artha.money"))
                                    transferMessage = "Copied package name to clipboard"
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = colors.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // SHA-1 Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keystore SHA-1 Fingerprint", fontSize = 10.sp, color = colors.onSurfaceVariant.copy(alpha = 0.6f))
                                Text("B5:30:88:2A:98:73:E7:B2:01:68:B0:4F:89:21:4F:C0:25:14:A3:30", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = colors.onBackground)
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("B5:30:88:2A:98:73:E7:B2:01:68:B0:4F:89:21:4F:C0:25:14:A3:30"))
                                    transferMessage = "Copied SHA-1 fingerprint to clipboard"
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = colors.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Optional Web Client ID Input
                    Text(
                        text = "Google Cloud Web Client ID (Optional):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = webClientIdInput,
                        onValueChange = { webClientIdInput = it },
                        placeholder = { Text("e.g. 123456-abc.apps.googleusercontent.com", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            preferencesRepository.updateGoogleWebClientId(webClientIdInput)
                            showGoogleCloudConfigDialog = false
                            transferMessage = if (webClientIdInput.isNotBlank()) "Google Web Client ID saved!" else "Cleared custom Web Client ID."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Save", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showGoogleCloudConfigDialog = false }) {
                    Text("Close")
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Google Account Sign-In Dialog (Fallback)
    if (showGoogleAccountDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In with Google",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Choose your Google Account to connect Artha for encrypted snapshots and Drive sync.",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            showGoogleAccountDialog = false
                            triggerGoogleSignIn()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚡ 1-Tap Device Account Picker", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Or enter your Google address manually:",
                        fontSize = 11.sp,
                        color = colors.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = googleAccountEmailInput,
                        onValueChange = { googleAccountEmailInput = it },
                        placeholder = { Text("Enter Google email (e.g. user@gmail.com)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val email = if (googleAccountEmailInput.isNotBlank()) googleAccountEmailInput.trim() else "user@gmail.com"
                        coroutineScope.launch {
                            preferencesRepository.linkGoogleAccount(email)
                            showGoogleAccountDialog = false
                            transferMessage = "Connected Google Account $email"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Link Account", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showGoogleAccountDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Restore Data Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restore from Google Drive / File",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }
            },
            text = {
                Column {
                    Button(
                        onClick = {
                            showRestoreDialog = false
                            restoreFromDriveLauncher.launch(arrayOf("application/json", "*/*"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📂 Select File from Google Drive / Storage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Or paste raw JSON backup text below:",
                        fontSize = 11.5.sp,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonText,
                        onValueChange = { restoreJsonText = it },
                        placeholder = { Text("Paste JSON backup string here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (restoreJsonText.isNotBlank()) {
                                val count = dataTransferRepository?.importJsonBackup(restoreJsonText) ?: 0
                                transferMessage = "✅ Restored $count items successfully from backup!"
                            }
                            showRestoreDialog = false
                            restoreJsonText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Restore JSON", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Currency Chooser Dialog
    if (showCurrencyChooser) {
        CurrencyPickerDialog(
            currentCurrency = userPreferences.baseCurrency,
            onSelectCurrency = { newCode ->
                coroutineScope.launch {
                    preferencesRepository.updateBaseCurrency(newCode)
                }
                showCurrencyChooser = false
            },
            onDismiss = { showCurrencyChooser = false }
        )
    }
}
