package com.artha.feature.transactions.add

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.AccountEntity
import com.artha.feature.transactions.TransactionsViewModel
import com.artha.model.AccountType
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import com.artha.ui.components.MathKeypad
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastAddTransactionSheet(
    viewModel: TransactionsViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val expression by viewModel.addExpression.collectAsState()
    val evaluatedMinor by viewModel.addEvaluatedAmountMinor.collectAsState()
    val selectedType by viewModel.addType.collectAsState()
    val selectedAccountId by viewModel.addAccountId.collectAsState()
    val selectedToAccountId by viewModel.addToAccountId.collectAsState()
    val selectedCategoryId by viewModel.addCategoryId.collectAsState()
    val selectedPaymentMethod by viewModel.addPaymentMethod.collectAsState()
    val note by viewModel.addNote.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showNoteField by remember { mutableStateOf(false) }

    // Default accounts if not selected
    val activeAccount = accounts.firstOrNull { it.id == selectedAccountId } ?: accounts.firstOrNull()
    val destinationAccount = accounts.firstOrNull { it.id == selectedToAccountId }
        ?: accounts.firstOrNull { it.id != activeAccount?.id }

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
                .padding(bottom = 12.dp)
        ) {
            // Header Bar: Close, Segmented Type Control, Done Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cancel",
                        tint = colors.onBackground
                    )
                }

                // Segmented Type Pills (Expense, Income, Transfer)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.6f))
                        .padding(3.dp)
                ) {
                    TypePill(
                        label = "Expense",
                        isSelected = selectedType == TransactionType.EXPENSE,
                        selectedColor = colors.expense,
                        onClick = { viewModel.setAddType(TransactionType.EXPENSE) }
                    )
                    TypePill(
                        label = "Income",
                        isSelected = selectedType == TransactionType.INCOME,
                        selectedColor = colors.income,
                        onClick = { viewModel.setAddType(TransactionType.INCOME) }
                    )
                    TypePill(
                        label = "Transfer",
                        isSelected = selectedType == TransactionType.TRANSFER,
                        selectedColor = colors.transfer,
                        onClick = { viewModel.setAddType(TransactionType.TRANSFER) }
                    )
                }

                // Save Checkmark Button
                val canSave = evaluatedMinor != null && evaluatedMinor!! > 0L
                IconButton(
                    onClick = {
                        if (canSave) {
                            viewModel.saveFastTransaction(onSaved = onDismiss)
                        }
                    },
                    enabled = canSave
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (canSave) colors.primary else colors.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Save",
                            tint = if (canSave) colors.onPrimary else colors.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount & Live Expression Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                val currency = activeAccount?.currency ?: "INR"
                val displayAmount = if (evaluatedMinor != null) {
                    Money(evaluatedMinor!!, currency).format()
                } else {
                    Money(0L, currency).format()
                }

                Text(
                    text = displayAmount,
                    color = when (selectedType) {
                        TransactionType.EXPENSE -> colors.expense
                        TransactionType.INCOME -> colors.income
                        TransactionType.TRANSFER -> colors.transfer
                    },
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Math expression preview (e.g. "120+45*2")
                if (expression.isNotEmpty()) {
                    Text(
                        text = expression,
                        color = colors.onSurfaceVariant,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Account & Destination Selectors
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Source Account Pill
                AccountChip(
                    account = activeAccount,
                    label = if (selectedType == TransactionType.TRANSFER) "From" else "Account",
                    colors = colors,
                    onClick = {
                        // Switch through available accounts
                        val nextIdx = (accounts.indexOf(activeAccount) + 1) % accounts.size.coerceAtLeast(1)
                        if (accounts.isNotEmpty()) {
                            viewModel.setAddAccount(accounts[nextIdx].id)
                        }
                    }
                )

                if (selectedType == TransactionType.TRANSFER) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.SwapHoriz,
                        contentDescription = "to",
                        tint = colors.transfer,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Destination Account Pill
                    AccountChip(
                        account = destinationAccount,
                        label = "To",
                        colors = colors,
                        onClick = {
                            val available = accounts.filter { it.id != activeAccount?.id }
                            val nextIdx = (available.indexOf(destinationAccount) + 1) % available.size.coerceAtLeast(1)
                            if (available.isNotEmpty()) {
                                viewModel.setAddToAccount(available[nextIdx].id)
                            }
                        }
                    )
                }
            }

            // Category Chips Row (for Expense / Income)
            if (selectedType != TransactionType.TRANSFER && categories.isNotEmpty()) {
                val relevantCategories = categories.filter {
                    it.type == selectedType.name
                }
                if (relevantCategories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        relevantCategories.forEach { cat ->
                            val isSelected = selectedCategoryId == cat.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) colors.primary.copy(alpha = 0.2f)
                                        else colors.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) colors.primary else colors.cardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (isSelected) viewModel.setAddCategory("")
                                        else viewModel.setAddCategory(cat.id)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.primary else colors.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Payment Methods Row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(PaymentMethod.UPI, PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD, PaymentMethod.CASH, PaymentMethod.NET_BANKING).forEach { pm ->
                    val isSelected = selectedPaymentMethod == pm
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surfaceVariant.copy(alpha = 0.3f))
                            .border(
                                width = 0.5.dp,
                                color = if (isSelected) colors.primary else colors.cardBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setAddPaymentMethod(pm) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = pm.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colors.primary else colors.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Note Field Toggle
            AnimatedVisibility(visible = showNoteField) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { viewModel.setAddNote(it) },
                    placeholder = { Text("Add note / merchant name…", fontSize = 13.sp) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = colors.surfaceVariant.copy(alpha = 0.4f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (!showNoteField) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showNoteField = true }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EditNote,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (note.isBlank()) "Add Note" else note,
                            color = colors.primary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Math Keypad (Thumb Zone)
            MathKeypad(
                onDigitClick = { viewModel.onKeypadDigit(it) },
                onOperatorClick = { viewModel.onKeypadOperator(it) },
                onBackspaceClick = { viewModel.onKeypadBackspace() },
                onClearClick = { viewModel.onKeypadClear() },
                onEvaluateClick = { viewModel.onKeypadEvaluate() }
            )
        }
    }
}

@Composable
private fun TypePill(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) selectedColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else LocalArthaColors.current.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun AccountChip(
    account: AccountEntity?,
    label: String,
    colors: com.artha.ui.theme.ArthaColors,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .border(0.5.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        val icon = when (account?.type) {
            AccountType.CREDIT_CARD -> Icons.Rounded.CreditCard
            else -> Icons.Rounded.AccountBalance
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ${account?.name ?: "Select"}",
            color = colors.onSurface,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
