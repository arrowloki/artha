package com.artha.feature.accounts.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.AccountEntity
import com.artha.feature.accounts.AccountsViewModel
import com.artha.model.Money
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayCardBillSheet(
    cardAccount: AccountEntity,
    viewModel: AccountsViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val density = LocalArthaDensity.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val assetAccounts by viewModel.assetAccounts.collectAsState()
    var selectedFromAccount by remember {
        mutableStateOf(assetAccounts.firstOrNull())
    }

    val totalDueMinor = cardAccount.currentBalanceMinor
    val minDueMinor = (totalDueMinor * 0.05).toLong().coerceAtLeast(0L)

    var amountInput by remember {
        mutableStateOf((totalDueMinor / 100.0).toString())
    }

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
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Pay Credit Card Bill",
                        color = colors.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cardAccount.name,
                        color = colors.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = colors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Balance & Statement Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(density.cornerRadius))
                    .background(colors.surface)
                    .border(0.5.dp, colors.cardBorder, RoundedCornerShape(density.cornerRadius))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "TOTAL DUE (OUTSTANDING)",
                            color = colors.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Money(totalDueMinor, cardAccount.currency).format(),
                            color = colors.expense,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "MINIMUM DUE (5%)",
                            color = colors.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Money(minDueMinor, cardAccount.currency).format(),
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Selection Chips: Full Due & Minimum Due
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AmountQuickChip(
                    label = "Pay Total Due",
                    amount = Money(totalDueMinor, cardAccount.currency).format(),
                    isSelected = amountInput == (totalDueMinor / 100.0).toString(),
                    colors = colors,
                    onClick = { amountInput = (totalDueMinor / 100.0).toString() },
                    modifier = Modifier.weight(1f)
                )

                AmountQuickChip(
                    label = "Pay Min Due",
                    amount = Money(minDueMinor, cardAccount.currency).format(),
                    isSelected = amountInput == (minDueMinor / 100.0).toString(),
                    colors = colors,
                    onClick = { amountInput = (minDueMinor / 100.0).toString() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Payment Amount Input
            Text(
                text = "PAYMENT AMOUNT",
                color = colors.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(0.5.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pay From (Asset Account) Selector
            Text(
                text = "PAY FROM ACCOUNT",
                color = colors.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(0.5.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        // Switch through asset accounts
                        if (assetAccounts.isNotEmpty()) {
                            val nextIdx = (assetAccounts.indexOf(selectedFromAccount) + 1) % assetAccounts.size
                            selectedFromAccount = assetAccounts[nextIdx]
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalance,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedFromAccount?.name ?: "Select Bank Account",
                        color = colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Available: ${Money(selectedFromAccount?.currentBalanceMinor ?: 0L, selectedFromAccount?.currency ?: "INR").format()}",
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Invariant Notice
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.transfer.copy(alpha = 0.1f))
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = colors.transfer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transfer between accounts. Does not change your spending aggregates.",
                    color = colors.transfer,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pay Button
            val payMinor = (amountInput.toDoubleOrNull() ?: 0.0 * 100.0).toLong()
            val canPay = payMinor > 0L && selectedFromAccount != null

            Button(
                onClick = {
                    if (canPay && selectedFromAccount != null) {
                        viewModel.payCreditCardBill(
                            cardAccount = cardAccount,
                            fromAccountId = selectedFromAccount!!.id,
                            amountMinor = (amountInput.toDouble() * 100.0).toLong(),
                            onSuccess = onDismiss
                        )
                    }
                },
                enabled = canPay,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Transfer Payment",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AmountQuickChip(
    label: String,
    amount: String,
    isSelected: Boolean,
    colors: com.artha.ui.theme.ArthaColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else colors.surface)
            .border(
                1.dp,
                if (isSelected) colors.primary else colors.cardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = colors.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                color = if (isSelected) colors.primary else colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
