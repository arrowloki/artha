package com.artha.feature.transactions.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.TransactionEntity
import com.artha.feature.transactions.TransactionsViewModel
import com.artha.model.CurrencyConfig
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import com.artha.ui.theme.CategoryVisuals
import com.artha.ui.theme.LocalArthaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionSheet(
    transaction: TransactionEntity,
    viewModel: TransactionsViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedType by remember { mutableStateOf(transaction.type) }
    var amountString by remember {
        val initialDollars = (transaction.amountMinor / 100.0).toString()
        mutableStateOf(if (initialDollars.endsWith(".0")) initialDollars.dropLast(2) else initialDollars)
    }
    var selectedCurrency by remember { mutableStateOf(transaction.currency) }
    var selectedAccountId by remember { mutableStateOf(transaction.accountId) }
    var selectedToAccountId by remember { mutableStateOf(transaction.toAccountId) }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var selectedPaymentMethod by remember { mutableStateOf(transaction.paymentMethod) }
    var note by remember { mutableStateOf(transaction.note.replace(Regex("#\\w+"), "").trim()) }

    // Tag management
    val existingTags = remember(transaction.note) { TransactionsViewModel.extractTags(transaction.note).toMutableSet() }
    var activeTags by remember { mutableStateOf(existingTags.toSet()) }
    var newTagInput by remember { mutableStateOf("") }
    var showNewTagField by remember { mutableStateOf(false) }

    val presetTags = listOf("Personal", "Work", "Vacation", "TaxSaver", "Family", "Reimbursable", "Online", "Cash")

    var showCurrencyDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header: Close, Title, Save Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close", tint = colors.onBackground)
                }

                Text(
                    text = "Edit Transaction",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                IconButton(
                    onClick = {
                        val parsed = amountString.toDoubleOrNull() ?: 0.0
                        val minor = (parsed * 100.0).toLong()
                        if (minor > 0L) {
                            val tagString = if (activeTags.isNotEmpty()) " " + activeTags.joinToString(" ") { "#$it" } else ""
                            val finalNote = (note.trim() + tagString).trim()
                            val updated = transaction.copy(
                                type = selectedType,
                                amountMinor = minor,
                                currency = selectedCurrency,
                                accountId = selectedAccountId,
                                toAccountId = if (selectedType == TransactionType.TRANSFER) selectedToAccountId else null,
                                categoryId = if (selectedType != TransactionType.TRANSFER) selectedCategoryId else null,
                                paymentMethod = selectedPaymentMethod,
                                note = finalNote,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.updateExistingTransaction(transaction, updated) {
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                    ) {
                        Icon(imageVector = Icons.Rounded.Check, contentDescription = "Save", tint = colors.onPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transaction Type Selector (Expense, Income, Transfer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(3.dp)
            ) {
                listOf(
                    TransactionType.EXPENSE to "Expense",
                    TransactionType.INCOME to "Income",
                    TransactionType.TRANSFER to "Transfer"
                ).forEach { (type, label) ->
                    val isSelected = selectedType == type
                    val color = when (type) {
                        TransactionType.EXPENSE -> colors.expense
                        TransactionType.INCOME -> colors.income
                        TransactionType.TRANSFER -> colors.transfer
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isSelected) color else Color.Transparent)
                            .clickable { selectedType = type }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else colors.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input with Currency Chooser Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Currency Picker Pill
                val currConfig = CurrencyConfig.forCode(selectedCurrency)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(0.5.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .clickable { showCurrencyDialog = true }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currConfig.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = currConfig.code, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.onBackground)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = currConfig.symbol, color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Amount Text Field
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    placeholder = { Text("0.00", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(0.5.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selector with Visual Icons (for Expense / Income)
            if (selectedType != TransactionType.TRANSFER) {
                Text(
                    text = "CATEGORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.filter { it.type == selectedType.name }) { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        val visual = CategoryVisuals.resolve(cat.id, cat.name, cat.iconName, cat.colorHex)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) visual.color.copy(alpha = 0.18f) else colors.surface)
                                .border(
                                    width = if (isSelected) 2.dp else 0.5.dp,
                                    color = if (isSelected) visual.color else colors.cardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategoryId = cat.id }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(visual.color.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = visual.icon,
                                    contentDescription = cat.name,
                                    tint = visual.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.name,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) visual.color else colors.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Account Selection
            Text(
                text = if (selectedType == TransactionType.TRANSFER) "FROM ACCOUNT" else "ACCOUNT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(accounts) { acc ->
                    val isSelected = selectedAccountId == acc.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else colors.surface)
                            .border(0.5.dp, if (isSelected) Color.Transparent else colors.cardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedAccountId = acc.id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = acc.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.onPrimary else colors.onBackground
                        )
                    }
                }
            }

            if (selectedType == TransactionType.TRANSFER) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "TO DESTINATION ACCOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts.filter { it.id != selectedAccountId }) { acc ->
                        val isSelected = selectedToAccountId == acc.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.transfer else colors.surface)
                                .border(0.5.dp, if (isSelected) Color.Transparent else colors.cardBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedToAccountId = acc.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = acc.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else colors.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Description / Note
            Text(
                text = "NOTE / MERCHANT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("What was this for? (e.g. Starbucks, Dinner with friends)") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(0.5.dp, colors.cardBorder, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Custom Labels / Tags Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.Label, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CUSTOM LABELS / TAGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "+ Add Custom",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable { showNewTagField = !showNewTagField }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Custom tag inline input
            AnimatedVisibility(visible = showNewTagField) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    OutlinedTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it.filter { char -> char.isLetterOrDigit() || char == '_' } },
                        placeholder = { Text("Label name (e.g. project_x, trip2026)") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedIndicatorColor = colors.primary,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(0.5.dp, colors.cardBorder, RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newTagInput.isNotBlank()) {
                                activeTags = activeTags + newTagInput.trim()
                                newTagInput = ""
                                showNewTagField = false
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("Add")
                    }
                }
            }

            // Tag Pills Row
            val allAvailableTags = (presetTags + activeTags).distinct()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allAvailableTags.forEach { tag ->
                    val isTagActive = activeTags.contains(tag)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isTagActive) colors.primary.copy(alpha = 0.18f) else colors.surface)
                            .border(
                                width = if (isTagActive) 1.5.dp else 0.5.dp,
                                color = if (isTagActive) colors.primary else colors.cardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                activeTags = if (isTagActive) {
                                    activeTags - tag
                                } else {
                                    activeTags + tag
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 12.sp,
                            fontWeight = if (isTagActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isTagActive) colors.primary else colors.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Payment Method Selection
            Text(
                text = "PAYMENT METHOD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedPaymentMethod == method
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else colors.surface)
                            .border(0.5.dp, if (isSelected) Color.Transparent else colors.cardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedPaymentMethod = method }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = method.name.replace("_", " "),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.onPrimary else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons: Save & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.expense),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val parsed = amountString.toDoubleOrNull() ?: 0.0
                        val minor = (parsed * 100.0).toLong()
                        if (minor > 0L) {
                            val tagString = if (activeTags.isNotEmpty()) " " + activeTags.joinToString(" ") { "#$it" } else ""
                            val finalNote = (note.trim() + tagString).trim()
                            val updated = transaction.copy(
                                type = selectedType,
                                amountMinor = minor,
                                currency = selectedCurrency,
                                accountId = selectedAccountId,
                                toAccountId = if (selectedType == TransactionType.TRANSFER) selectedToAccountId else null,
                                categoryId = if (selectedType != TransactionType.TRANSFER) selectedCategoryId else null,
                                paymentMethod = selectedPaymentMethod,
                                note = finalNote,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.updateExistingTransaction(transaction, updated) {
                                onDismiss()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = colors.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showCurrencyDialog) {
        CurrencyPickerDialog(
            currentCurrency = selectedCurrency,
            onSelectCurrency = {
                selectedCurrency = it
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
}

@Composable
fun CurrencyPickerDialog(
    currentCurrency: String,
    onSelectCurrency: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalArthaColors.current
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            CurrencyConfig.ALL_CURRENCIES
        } else {
            CurrencyConfig.ALL_CURRENCIES.filter {
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Choose Currency", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search currency by code, name...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(0.5.dp, colors.cardBorder, RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    filtered.forEach { curr ->
                        val isSelected = curr.code.equals(currentCurrency, ignoreCase = true)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onSelectCurrency(curr.code) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = curr.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "${curr.code} — ${curr.name}", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = colors.onBackground)
                                    Text(text = "Symbol: ${curr.symbol}", fontSize = 11.5.sp, color = colors.onBackground.copy(alpha = 0.6f))
                                }
                            }
                            if (isSelected) {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = "Selected", tint = colors.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
