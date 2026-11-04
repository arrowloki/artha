package com.artha.feature.accounts.list

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.AccountEntity
import com.artha.feature.accounts.AccountsViewModel
import com.artha.feature.accounts.add.AddAccountSheet
import com.artha.feature.accounts.card.PayCardBillSheet
import com.artha.feature.accounts.reconcile.ReconcileScreen
import com.artha.model.AccountType
import com.artha.model.Money
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity

@Composable
fun AccountListScreen(
    viewModel: AccountsViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    val accounts by viewModel.accounts.collectAsState()
    val netWorthSummary by viewModel.netWorthSummary.collectAsState()

    var showAddAccountSheet by remember { mutableStateOf(false) }
    var cardToPayBill by remember { mutableStateOf<AccountEntity?>(null) }
    var accountToReconcile by remember { mutableStateOf<AccountEntity?>(null) }

    if (accountToReconcile != null) {
        ReconcileScreen(
            account = accountToReconcile!!,
            viewModel = viewModel,
            onBack = { accountToReconcile = null }
        )
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Net Worth Summary Header Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                NetWorthHeaderCard(
                    netWorth = netWorthSummary.netWorth,
                    totalAssets = netWorthSummary.totalAssets,
                    totalLiabilities = netWorthSummary.totalLiabilities
                )
            }

            // Bank Accounts Section
            val bankAccounts = accounts.filter { it.type == AccountType.BANK || it.type == AccountType.CASH }
            if (bankAccounts.isNotEmpty()) {
                item {
                    AccountGroupHeader(title = "CASH & BANK", count = bankAccounts.size)
                }
                items(bankAccounts, key = { it.id }) { acc ->
                    StandardAccountCard(
                        account = acc,
                        onReconcile = { accountToReconcile = acc }
                    )
                }
            }

            // Credit Cards Section (with bill payment & limit utilization)
            val creditCards = accounts.filter { it.type == AccountType.CREDIT_CARD }
            if (creditCards.isNotEmpty()) {
                item {
                    AccountGroupHeader(title = "CREDIT CARDS", count = creditCards.size)
                }
                items(creditCards, key = { it.id }) { card ->
                    CreditCardItemCard(
                        account = card,
                        onPayBill = { cardToPayBill = card },
                        onReconcile = { accountToReconcile = card }
                    )
                }
            }

            // Wallets Section
            val wallets = accounts.filter { it.type == AccountType.WALLET }
            if (wallets.isNotEmpty()) {
                item {
                    AccountGroupHeader(title = "DIGITAL WALLETS", count = wallets.size)
                }
                items(wallets, key = { it.id }) { acc ->
                    StandardAccountCard(
                        account = acc,
                        onReconcile = { accountToReconcile = acc }
                    )
                }
            }

            // Investments Section
            val investments = accounts.filter { it.type == AccountType.INVESTMENT }
            if (investments.isNotEmpty()) {
                item {
                    AccountGroupHeader(title = "INVESTMENTS & ASSETS", count = investments.size)
                }
                items(investments, key = { it.id }) { acc ->
                    StandardAccountCard(
                        account = acc,
                        onReconcile = { accountToReconcile = acc }
                    )
                }
            }

            // Loans & Liabilities
            val loans = accounts.filter { it.type == AccountType.LOAN }
            if (loans.isNotEmpty()) {
                item {
                    AccountGroupHeader(title = "LOANS & MORTGAGES", count = loans.size)
                }
                items(loans, key = { it.id }) { acc ->
                    StandardAccountCard(
                        account = acc,
                        onReconcile = { accountToReconcile = acc }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add Account
        FloatingActionButton(
            onClick = { showAddAccountSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = colors.primary,
            contentColor = colors.background,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Account"
            )
        }

        // Add Account Bottom Sheet
        if (showAddAccountSheet) {
            AddAccountSheet(
                viewModel = viewModel,
                onDismiss = { showAddAccountSheet = false }
            )
        }

        // Pay Credit Card Bill Sheet
        if (cardToPayBill != null) {
            PayCardBillSheet(
                cardAccount = cardToPayBill!!,
                viewModel = viewModel,
                onDismiss = { cardToPayBill = null }
            )
        }
    }
}

@Composable
private fun NetWorthHeaderCard(
    netWorth: Money,
    totalAssets: Money,
    totalLiabilities: Money,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "TOTAL NET WORTH",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = netWorth.formatted(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (netWorth.amountMinor >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Assets Breakdown
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Assets",
                            fontSize = 12.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = totalAssets.formatted(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                    }
                }

                // Liabilities Breakdown
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingDown,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Liabilities",
                            fontSize = 12.sp,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = totalLiabilities.formatted(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountGroupHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Text(
            text = "$count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun StandardAccountCard(
    account: AccountEntity,
    onReconcile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val icon = when (account.type) {
        AccountType.BANK -> Icons.Rounded.AccountBalance
        AccountType.CASH -> Icons.Rounded.MonetizationOn
        AccountType.WALLET -> Icons.Rounded.Wallet
        AccountType.INVESTMENT -> Icons.Rounded.ShowChart
        AccountType.LOAN -> Icons.Rounded.Payment
        else -> Icons.Rounded.AccountBalance
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = account.name,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = account.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    Text(
                        text = account.type.name,
                        fontSize = 11.5.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = Money(account.currentBalanceMinor, account.currency).formatted(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onReconcile,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Checklist,
                        contentDescription = "Reconcile",
                        tint = colors.onBackground.copy(alpha = 0.45f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreditCardItemCard(
    account: AccountEntity,
    onPayBill: () -> Unit,
    onReconcile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val outstandingMinor = account.currentBalanceMinor
    val limitMinor = account.creditLimitMinor
    val availableMinor = (limitMinor - outstandingMinor).coerceAtLeast(0L)

    val utilizationFraction = if (limitMinor > 0) {
        (outstandingMinor.toFloat() / limitMinor.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val utilizationPercent = (utilizationFraction * 100).toInt()
    val utilizationColor = when {
        utilizationPercent < 30 -> Color(0xFF10B981) // Green
        utilizationPercent < 70 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFEF4444) // Red
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE91E63).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CreditCard,
                            contentDescription = account.name,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = account.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Due on ${account.dueDay}th of month",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                IconButton(
                    onClick = onReconcile,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Checklist,
                        contentDescription = "Reconcile",
                        tint = colors.onBackground.copy(alpha = 0.45f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Outstanding and Available
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Due (Liability)",
                        fontSize = 11.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = Money(outstandingMinor, account.currency).formatted(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Available Limit",
                        fontSize = 11.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = Money(availableMinor, account.currency).formatted(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onBackground
                    )
                }
            }

            // Limit utilization bar
            if (limitMinor > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { utilizationFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = utilizationColor,
                    trackColor = colors.onBackground.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$utilizationPercent% limit used",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = utilizationColor
                    )
                    Text(
                        text = "Limit: ${Money(limitMinor, account.currency).formatted()}",
                        fontSize = 10.5.sp,
                        color = colors.onBackground.copy(alpha = 0.45f)
                    )
                }
            }

            // Quick Pay Bill Button
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onPayBill,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.background
                )
            ) {
                Text(
                    text = "Pay Bill (Transfer)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
