package com.artha.money.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Rule
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
import com.artha.database.entity.CategoryEntity
import com.artha.database.entity.RuleEntity
import com.artha.ui.theme.LocalArthaColors
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleSheet(
    availableCategories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSaveRule: (RuleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var conditionField by remember { mutableStateOf("MERCHANT") } // "MERCHANT", "NOTE", "AMOUNT"
    var conditionOperator by remember { mutableStateOf("CONTAINS") } // "CONTAINS", "EQUALS", "GREATER_THAN"
    var conditionValue by remember { mutableStateOf("") }
    var actionField by remember { mutableStateOf("SET_CATEGORY") } // "SET_CATEGORY", "EXCLUDE_FROM_BUDGET"
    var selectedCategoryId by remember { mutableStateOf(availableCategories.firstOrNull()?.id ?: "") }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Rule,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New Smart Rule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Cancel",
                        tint = colors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rule Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Rule Name") },
                placeholder = { Text("e.g. Blue Tokai to Coffee") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // If Condition
            Text(
                text = "IF CONDITION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Condition Field Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val fields = listOf("MERCHANT", "NOTE", "AMOUNT")
                fields.forEach { field ->
                    val isSelected = conditionField == field
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else colors.surface)
                            .border(1.dp, if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .clickable { conditionField = field }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = field,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.background else colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Condition Value
            OutlinedTextField(
                value = conditionValue,
                onValueChange = { conditionValue = it },
                label = { Text(if (conditionField == "AMOUNT") "Amount (Minor units, e.g. 500000 = ₹5,000)" else "Text matches (contains)") },
                placeholder = { Text(if (conditionField == "AMOUNT") "500000" else "Blue Tokai") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Then Action
            Text(
                text = "THEN ACTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val actions = listOf(
                    Pair("SET_CATEGORY", "Assign Category"),
                    Pair("EXCLUDE_FROM_BUDGET", "Exclude Budget")
                )
                actions.forEach { (action, label) ->
                    val isSelected = actionField == action
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else colors.surface)
                            .border(1.dp, if (isSelected) colors.primary else colors.onBackground.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .clickable { actionField = action }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.background else colors.onBackground
                        )
                    }
                }
            }

            if (actionField == "SET_CATEGORY" && availableCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Select Category:",
                    fontSize = 12.sp,
                    color = colors.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableCategories.take(6).forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.primary.copy(alpha = 0.12f) else colors.surface)
                                .border(1.dp, if (isSelected) colors.primary else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { selectedCategoryId = cat.id }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = cat.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = colors.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            val canSave = name.isNotBlank() && conditionValue.isNotBlank()
            Button(
                onClick = {
                    if (canSave) {
                        val newRule = RuleEntity(
                            id = "rule_${UUID.randomUUID().toString().take(8)}",
                            name = name.trim(),
                            conditionField = conditionField,
                            conditionOperator = if (conditionField == "AMOUNT") "GREATER_THAN" else "CONTAINS",
                            conditionValue = conditionValue.trim(),
                            actionField = actionField,
                            actionValue = if (actionField == "SET_CATEGORY") selectedCategoryId else "true",
                            priority = 50,
                            isActive = true
                        )
                        onSaveRule(newRule)
                        onDismiss()
                    }
                },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Save Rule",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.background
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
