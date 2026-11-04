package com.artha.feature.transactions.list

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.TransactionEntity
import com.artha.feature.transactions.TransactionsViewModel
import com.artha.feature.transactions.add.FastAddTransactionSheet
import com.artha.model.Money
import com.artha.model.TransactionType
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionListScreen(
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    val transactions by viewModel.transactions.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val showUndo by viewModel.showUndoSnackbar.collectAsState()

    var showFastAdd by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Undo Snackbar trigger
    LaunchedEffect(showUndo) {
        if (showUndo) {
            val result = snackbarHostState.showSnackbar(
                message = "Transaction deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastDelete()
            }
            viewModel.dismissUndoSnackbar()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search transactions, notes, merchants…", fontSize = 13.5.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear",
                                    tint = colors.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(0.5.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Type Filter Chips Row (All, Expenses, Incomes, Transfers)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    FilterChip(
                        label = "All",
                        isSelected = filterState.type == null,
                        onClick = { viewModel.updateTypeFilter(null) }
                    )
                    FilterChip(
                        label = "Expenses",
                        isSelected = filterState.type == TransactionType.EXPENSE,
                        onClick = { viewModel.updateTypeFilter(TransactionType.EXPENSE) }
                    )
                    FilterChip(
                        label = "Income",
                        isSelected = filterState.type == TransactionType.INCOME,
                        onClick = { viewModel.updateTypeFilter(TransactionType.INCOME) }
                    )
                    FilterChip(
                        label = "Transfers",
                        isSelected = filterState.type == TransactionType.TRANSFER,
                        onClick = { viewModel.updateTypeFilter(TransactionType.TRANSFER) }
                    )
                }
            }

            // Transaction List
            if (transactions.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Receipt,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (filterState.query.isNotBlank()) "No transactions found" else "No transactions yet",
                            color = colors.onSurfaceVariant,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + below for instant 3-tap logging",
                            color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 12.5.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { tx ->
                        TransactionCard(
                            tx = tx,
                            onDuplicate = { viewModel.duplicateTransaction(tx) },
                            onDelete = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }
            }
        }

        // Floating Action Button (+)
        FloatingActionButton(
            onClick = { showFastAdd = true },
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Fast Add",
                modifier = Modifier.size(28.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Fast Add Transaction Bottom Sheet
        if (showFastAdd) {
            FastAddTransactionSheet(
                viewModel = viewModel,
                onDismiss = { showFastAdd = false }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalArthaColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primary else colors.surface)
            .border(
                0.5.dp,
                if (isSelected) Color.Transparent else colors.cardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun TransactionCard(
    tx: TransactionEntity,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val density = LocalArthaDensity.current

    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(tx.timestamp) { sdf.format(Date(tx.timestamp)) }

    val isExpense = tx.type == TransactionType.EXPENSE
    val isIncome = tx.type == TransactionType.INCOME

    val amountColor = when {
        isExpense -> colors.expense
        isIncome -> colors.income
        else -> colors.transfer
    }

    val typeIcon = when {
        isExpense -> Icons.Rounded.ArrowDownward
        isIncome -> Icons.Rounded.ArrowUpward
        else -> Icons.Rounded.SwapHoriz
    }

    val sign = when {
        isExpense -> "-"
        isIncome -> "+"
        else -> ""
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(density.cornerRadius))
            .background(colors.surface)
            .border(0.5.dp, colors.cardBorder, RoundedCornerShape(density.cornerRadius))
            .padding(14.dp)
    ) {
        // Icon Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(amountColor.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = typeIcon,
                contentDescription = null,
                tint = amountColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Note / Title and Timestamp / Payment Method
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (tx.note.isNotBlank()) tx.note else tx.type.name.lowercase().replaceFirstChar { it.uppercase() },
                color = colors.onSurface,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattedDate,
                    color = colors.onSurfaceVariant,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Payment Method Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tx.paymentMethod.name,
                        color = colors.onSurfaceVariant,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Formatted Amount
        Column(horizontalAlignment = Alignment.End) {
            val formattedAmount = Money(tx.amountMinor, tx.currency).format(includeSymbol = true)
            Text(
                text = "$sign$formattedAmount",
                color = amountColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Quick duplicate / delete action row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Duplicate",
                        tint = colors.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = colors.expense.copy(alpha = 0.6f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
