package com.artha.feature.insight.add

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.feature.insight.InsightViewModel
import com.artha.model.CurrencyConfig
import com.artha.model.DebtType
import com.artha.ui.theme.LocalArthaColors
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtSheet(
    viewModel: InsightViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var personName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DebtType.LENT) }
    var amountInput by remember { mutableStateOf("") }
    var interestRateInput by remember { mutableStateOf("") }
    var tenureMonthsInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val debtTypes = listOf(
        Pair(DebtType.LENT, "Money Lent (I gave)"),
        Pair(DebtType.BORROWED, "Money Borrowed (I owe)"),
        Pair(DebtType.EMI_LOAN, "Bank Loan / EMI")
    )

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
                    text = "Add Debt / Loan / IOU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
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

            // Debt Type Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                debtTypes.forEach { (type, label) ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colors.primary else colors.surface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedType = type }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.background else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Person or Institution Name
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                label = {
                    Text(
                        if (selectedType == DebtType.EMI_LOAN) "Bank / Institution (e.g. HDFC Home Loan)"
                        else "Person / Contact Name (e.g. Alex, Rahul)"
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

            Spacer(modifier = Modifier.height(14.dp))

            // Amount
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Principal Amount (₹)") },
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

            // When EMI Loan: Rate and Tenure
            if (selectedType == DebtType.EMI_LOAN) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = interestRateInput,
                        onValueChange = { interestRateInput = it },
                        label = { Text("Interest Rate (% APR)") },
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
                        value = tenureMonthsInput,
                        onValueChange = { tenureMonthsInput = it },
                        label = { Text("Tenure (Months)") },
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

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Purpose (Optional)") },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            val isFormValid = personName.isNotBlank() && amountInput.isNotBlank()
            Button(
                onClick = {
                    if (isFormValid) {
                        val config = CurrencyConfig.forCode("INR")
                        val principalMinor = try {
                            BigDecimal(amountInput.trim()).movePointRight(config.decimalPlaces).toLong()
                        } catch (e: Exception) {
                            0L
                        }

                        val rate = interestRateInput.toDoubleOrNull() ?: 0.0
                        val tenure = tenureMonthsInput.toIntOrNull() ?: 0

                        viewModel.createDebt(
                            personOrInstitution = personName,
                            type = selectedType,
                            principalMinor = principalMinor,
                            interestRateApr = rate,
                            tenureMonths = tenure,
                            notes = notes
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
                    text = "Save Entry",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
