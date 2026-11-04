package com.artha.feature.accounts.add

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.Wallet
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.artha.feature.accounts.AccountsViewModel
import com.artha.model.AccountType
import com.artha.model.CurrencyConfig
import com.artha.ui.theme.LocalArthaColors
import com.artha.ui.theme.LocalArthaDensity
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountSheet(
    viewModel: AccountsViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK) }
    var currency by remember { mutableStateOf("INR") }
    var openingBalanceInput by remember { mutableStateOf("") }

    // Credit Card specific fields
    var creditLimitInput by remember { mutableStateOf("") }
    var billingDay by remember { mutableIntStateOf(1) }
    var dueDay by remember { mutableIntStateOf(20) }

    val accountTypes = listOf(
        Triple(AccountType.BANK, "Bank", Icons.Rounded.AccountBalance),
        Triple(AccountType.CREDIT_CARD, "Credit Card", Icons.Rounded.CreditCard),
        Triple(AccountType.CASH, "Cash", Icons.Rounded.MonetizationOn),
        Triple(AccountType.WALLET, "Wallet", Icons.Rounded.Wallet),
        Triple(AccountType.INVESTMENT, "Investment", Icons.Rounded.ShowChart),
        Triple(AccountType.LOAN, "Loan", Icons.Rounded.Payment)
    )

    val supportedCurrencies = listOf("INR", "USD", "EUR", "GBP", "AED", "SGD", "JPY")

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Add New Account",
                    color = colors.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name (e.g. HDFC Bank, ICICI Amazon Pay)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Account Type Selector
            Text(
                text = "ACCOUNT TYPE",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
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
                accountTypes.forEach { (type, label, icon) ->
                    val isSelected = selectedType == type
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) colors.primary else colors.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedType = type }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) colors.background else colors.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colors.background else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currency Selector
            Text(
                text = "CURRENCY",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
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
                supportedCurrencies.forEach { curr ->
                    val isSelected = currency == curr
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { currency = curr }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = curr,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.primary else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opening Balance or Outstanding Debt
            OutlinedTextField(
                value = openingBalanceInput,
                onValueChange = { openingBalanceInput = it },
                label = {
                    Text(
                        if (selectedType == AccountType.CREDIT_CARD) "Current Outstanding Balance ($currency)"
                        else "Opening Balance ($currency)"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Credit Card Specific fields
            if (selectedType == AccountType.CREDIT_CARD) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = creditLimitInput,
                    onValueChange = { creditLimitInput = it },
                    label = { Text("Credit Limit ($currency)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground,
                        focusedLabelColor = colors.primary,
                        unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                        focusedIndicatorColor = colors.primary,
                        unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = billingDay.toString(),
                        onValueChange = { billingDay = it.toIntOrNull()?.coerceIn(1, 31) ?: 1 },
                        label = { Text("Bill Day (1-31)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedTextColor = colors.onBackground,
                            unfocusedTextColor = colors.onBackground,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                            focusedIndicatorColor = colors.primary,
                            unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dueDay.toString(),
                        onValueChange = { dueDay = it.toIntOrNull()?.coerceIn(1, 31) ?: 20 },
                        label = { Text("Due Day (1-31)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedTextColor = colors.onBackground,
                            unfocusedTextColor = colors.onBackground,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f),
                            focusedIndicatorColor = colors.primary,
                            unfocusedIndicatorColor = colors.onBackground.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            val isFormValid = name.isNotBlank()
            Button(
                onClick = {
                    if (isFormValid) {
                        val config = CurrencyConfig.forCode(currency)
                        val openingMinor = try {
                            if (openingBalanceInput.isBlank()) 0L
                            else BigDecimal(openingBalanceInput.trim())
                                .movePointRight(config.decimalPlaces).toLong()
                        } catch (e: Exception) {
                            0L
                        }

                        val creditLimitMinor = try {
                            if (creditLimitInput.isBlank()) 0L
                            else BigDecimal(creditLimitInput.trim())
                                .movePointRight(config.decimalPlaces).toLong()
                        } catch (e: Exception) {
                            0L
                        }

                        viewModel.createAccount(
                            name = name,
                            type = selectedType,
                            currency = currency,
                            openingBalanceMinor = openingMinor,
                            creditLimitMinor = creditLimitMinor,
                            billingDay = billingDay,
                            dueDay = dueDay
                        )
                        onDismiss()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.background,
                    disabledContainerColor = colors.onBackground.copy(alpha = 0.12f),
                    disabledContentColor = colors.onBackground.copy(alpha = 0.38f)
                )
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
