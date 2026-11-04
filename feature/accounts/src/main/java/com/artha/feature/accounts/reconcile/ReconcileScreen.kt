package com.artha.feature.accounts.reconcile

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.TransactionEntity
import com.artha.feature.accounts.AccountsViewModel
import com.artha.model.CurrencyConfig
import com.artha.model.Money
import com.artha.model.TransactionType
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReconcileScreen(
    account: AccountEntity,
    viewModel: AccountsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    val transactions by viewModel.getTransactionsForAccountFlow(account.id)
        .collectAsState(initial = emptyList())

    val config = remember(account.currency) { CurrencyConfig.forCode(account.currency) }

    var statementBalanceInput by remember { mutableStateOf("") }

    // Reconciled balance calculation
    // Starting with opening balance + reconciled additions - reconciled deductions
    val reconciledBalanceMinor = remember(transactions, account) {
        var bal = account.openingBalanceMinor
        transactions.filter { it.isReconciled }.forEach { tx ->
            when (tx.type) {
                TransactionType.INCOME -> bal += tx.amountMinor
                TransactionType.EXPENSE -> bal -= tx.amountMinor
                TransactionType.TRANSFER -> {
                    if (tx.accountId == account.id) bal -= tx.amountMinor
                    if (tx.toAccountId == account.id) bal += tx.amountMinor
                }
            }
        }
        bal
    }

    val targetStatementMinor = try {
        if (statementBalanceInput.isBlank()) null
        else BigDecimal(statementBalanceInput.trim())
            .movePointRight(config.decimalPlaces).toLong()
    } catch (e: Exception) {
        null
    }

    val differenceMinor = targetStatementMinor?.let { it - reconciledBalanceMinor }
    val isBalanced = differenceMinor == 0L

    Surface(
        color = colors.background,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Reconcile ${account.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Tick off transactions against your statement",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RECONCILED BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackground.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Money(reconciledBalanceMinor, account.currency).formatted(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onBackground
                            )
                        }

                        if (differenceMinor != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isBalanced) Color(0xFF10B981).copy(alpha = 0.15f)
                                        else Color(0xFFEF4444).copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isBalanced) Icons.Rounded.CheckCircle else Icons.Rounded.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBalanced) "Balanced!"
                                        else "Diff: " + Money(differenceMinor, account.currency).formatted(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBalanced) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = statementBalanceInput,
                        onValueChange = { statementBalanceInput = it },
                        label = { Text("Statement Ending Balance (${account.currency})") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.background,
                            unfocusedContainerColor = colors.background,
                            focusedTextColor = colors.onBackground,
                            unfocusedTextColor = colors.onBackground,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                            focusedIndicatorColor = colors.primary,
                            unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction checklist
            Text(
                text = "TRANSACTIONS (${transactions.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found for this account",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { tx ->
                        ReconcileTransactionRow(
                            tx = tx,
                            onToggle = { viewModel.toggleReconciled(tx) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReconcileTransactionRow(
    tx: TransactionEntity,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sdf = remember { SimpleDateFormat("dd MMM, yyyy", Locale.US) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = tx.isReconciled,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                checkmarkColor = colors.background,
                uncheckedColor = colors.onBackground.copy(alpha = 0.4f)
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (tx.note.isNotBlank()) tx.note else tx.type.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sdf.format(Date(tx.timestamp)) + " • " + tx.paymentMethod.displayName,
                fontSize = 11.sp,
                color = colors.onBackground.copy(alpha = 0.5f)
            )
        }

        val sign = when (tx.type) {
            TransactionType.INCOME -> "+"
            TransactionType.EXPENSE -> "-"
            TransactionType.TRANSFER -> "⇄ "
        }
        val amountColor = when (tx.type) {
            TransactionType.INCOME -> Color(0xFF10B981)
            TransactionType.EXPENSE -> colors.onBackground
            TransactionType.TRANSFER -> Color(0xFF3B82F6)
        }

        Text(
            text = "$sign${Money(tx.amountMinor, tx.currency).formatted()}",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}
