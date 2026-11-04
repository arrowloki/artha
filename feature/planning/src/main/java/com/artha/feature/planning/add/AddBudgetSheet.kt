package com.artha.feature.planning.add

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.artha.feature.planning.PlanningViewModel
import com.artha.model.BudgetPeriod
import com.artha.model.CurrencyConfig
import com.artha.ui.theme.LocalArthaColors
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetSheet(
    viewModel: PlanningViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var alertThreshold by remember { mutableIntStateOf(80) }
    var rolloverEnabled by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }

    val categories by viewModel.categories.collectAsState()
    var selectedCategoryIds by remember { mutableStateOf(setOf<String>()) }

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
                    text = "Create Budget",
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

            // Budget Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Budget Name (e.g. Monthly Living, Dining Out)") },
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

            // Budget Limit Amount
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Budget Limit (₹)") },
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

            // Category Selection
            Text(
                text = "LINKED CATEGORIES (OPTIONAL)",
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
                categories.filter { it.type == "EXPENSE" }.forEach { cat ->
                    val isSelected = selectedCategoryIds.contains(cat.id)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedCategoryIds = if (isSelected) {
                                    selectedCategoryIds - cat.id
                                } else {
                                    selectedCategoryIds + cat.id
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.primary else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alert Threshold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Alert Threshold",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Alert when spending reaches $alertThreshold%",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(50, 80, 90, 100).forEach { threshold ->
                        val isSelected = alertThreshold == threshold
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colors.primary else colors.surface)
                                .clickable { alertThreshold = threshold }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$threshold%",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) colors.background else colors.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rollover Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rollover Surplus",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Carry forward unspent money into the next month's envelope",
                        fontSize = 11.5.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }
                Switch(
                    checked = rolloverEnabled,
                    onCheckedChange = { rolloverEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.primary,
                        checkedTrackColor = colors.primary.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Button
            val isFormValid = name.isNotBlank() && amountInput.isNotBlank()
            Button(
                onClick = {
                    if (isFormValid) {
                        val config = CurrencyConfig.forCode("INR")
                        val amountMinor = try {
                            BigDecimal(amountInput.trim()).movePointRight(config.decimalPlaces).toLong()
                        } catch (e: Exception) {
                            0L
                        }

                        viewModel.createBudget(
                            name = name,
                            amountMinor = amountMinor,
                            currency = "INR",
                            periodType = selectedPeriod,
                            alertThresholdPercentage = alertThreshold,
                            rolloverEnabled = rolloverEnabled,
                            categoryIds = selectedCategoryIds.toList()
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
                    text = "Save Budget",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
