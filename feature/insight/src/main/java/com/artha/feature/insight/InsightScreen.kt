package com.artha.feature.insight

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
import androidx.compose.material.icons.rounded.CallMade
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.database.entity.DebtLoanEntity
import com.artha.domain.CashFlowSummary
import com.artha.domain.CategoryShare
import com.artha.domain.PeriodComparison
import com.artha.feature.insight.add.AddDebtSheet
import com.artha.model.DebtType
import com.artha.model.Money
import com.artha.ui.theme.LocalArthaColors

@Composable
fun InsightScreen(
    viewModel: InsightViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Reports, 1: Debts & Loans, 2: EMI Calculator
    var showAddDebtSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header & Segmented Tabs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Insights & Analytics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = "Cash flow, category breakdown, debts & EMI simulator",
                    fontSize = 12.5.sp,
                    color = colors.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("Cash Flow", "Debts & Loans", "EMI Simulator")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.primary else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp)
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
            }

            // Tab Content
            when (selectedTab) {
                0 -> ReportsTabContent(viewModel = viewModel)
                1 -> DebtsTabContent(viewModel = viewModel)
                2 -> EmiCalculatorTabContent(viewModel = viewModel)
            }
        }

        // FAB to Add Debt/Loan on tab 1
        if (selectedTab == 1) {
            FloatingActionButton(
                onClick = { showAddDebtSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = colors.primary,
                contentColor = colors.background,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Debt")
            }
        }

        if (showAddDebtSheet) {
            AddDebtSheet(viewModel = viewModel, onDismiss = { showAddDebtSheet = false })
        }
    }
}

@Composable
private fun ReportsTabContent(viewModel: InsightViewModel) {
    val colors = LocalArthaColors.current
    val cashFlow by viewModel.cashFlowSummary.collectAsState()
    val monthComparison by viewModel.monthOverMonthComparison.collectAsState()
    val categoryShares by viewModel.categoryShares.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cash Flow Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "MONTHLY CASH FLOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Money(cashFlow.netSavingsMinor, cashFlow.currency).format(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cashFlow.netSavingsMinor >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Inflow
                        Column {
                            Text(
                                text = "Total Inflow (Income)",
                                fontSize = 11.5.sp,
                                color = colors.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "+${Money(cashFlow.totalInflowMinor, cashFlow.currency).format()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }

                        // Outflow
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Outflow (Expense)",
                                fontSize = 11.5.sp,
                                color = colors.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "-${Money(cashFlow.totalOutflowMinor, cashFlow.currency).format()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Savings rate and month-over-month comparison badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Savings: ${cashFlow.savingsRatePercentage}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        if (monthComparison.previousPeriodExpenseMinor > 0L) {
                            val sign = if (monthComparison.isIncreased) "+" else ""
                            val badgeColor = if (monthComparison.isIncreased) Color(0xFFEF4444) else Color(0xFF10B981)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$sign${monthComparison.percentageChange.toInt()}% vs last mo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Breakdown Section
        item {
            Text(
                text = "EXPENSE BY CATEGORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        if (categoryShares.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No category spending recorded yet this month",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 13.5.sp
                    )
                }
            }
        } else {
            items(categoryShares, key = { it.categoryId }) { cat ->
                CategoryShareRow(cat = cat)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CategoryShareRow(
    cat: CategoryShare,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cat.categoryName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackground
                )
                Text(
                    text = Money(cat.totalAmountMinor, "INR").format(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (cat.sharePercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = colors.primary,
                trackColor = colors.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${cat.sharePercentage.toInt()}% of total spend",
                    fontSize = 11.sp,
                    color = colors.onBackground.copy(alpha = 0.5f)
                )
                Text(
                    text = "${cat.transactionCount} transactions",
                    fontSize = 11.sp,
                    color = colors.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun DebtsTabContent(viewModel: InsightViewModel) {
    val colors = LocalArthaColors.current
    val summary by viewModel.debtsSummary.collectAsState()
    val debts by viewModel.allDebts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Money Lent
                    Column {
                        Text(
                            text = "MONEY LENT (I AM OWED)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Money(summary.totalLentMinor, "INR").format(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }

                    // Money Borrowed
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "BORROWED & LOANS",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Money(summary.totalBorrowedMinor + summary.totalEmiDebtMinor, "INR").format(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        if (debts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active debts, loans or shared IOUs.\nTap + below to record money lent or borrowed.",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(debts, key = { it.id }) { debt ->
                DebtItemCard(
                    debt = debt,
                    onSettleUp = { viewModel.recordDebtPayment(debt.id, debt.remainingMinor) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DebtItemCard(
    debt: DebtLoanEntity,
    onSettleUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current

    val isLent = debt.type == DebtType.LENT
    val isEmi = debt.type == DebtType.EMI_LOAN
    val badgeColor = if (isLent) Color(0xFF10B981) else Color(0xFFEF4444)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = debt.personOrInstitution,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    Text(
                        text = if (isEmi) "EMI: ${Money(debt.emiAmountMinor, "INR").format()}/mo • ${debt.interestRateApr}% APR"
                        else if (isLent) "Lent to them" else "Borrowed from them",
                        fontSize = 11.5.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isLent) "+${Money(debt.remainingMinor, "INR").format()}"
                        else "-${Money(debt.remainingMinor, "INR").format()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            if (debt.remainingMinor > 0L) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onSettleUp,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Settle Up / Record Payment", fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmiCalculatorTabContent(viewModel: InsightViewModel) {
    val colors = LocalArthaColors.current
    val principal by viewModel.principalInput.collectAsState()
    val rate by viewModel.interestRateInput.collectAsState()
    val tenure by viewModel.tenureMonthsInput.collectAsState()
    val extraPayment by viewModel.extraMonthlyPaymentInput.collectAsState()
    val simState by viewModel.emiSimulationState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // EMI Result Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "MONTHLY EMI AMOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${Money(simState.monthlyEmiMinor, "INR").format()} / month",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Interest",
                                fontSize = 11.5.sp,
                                color = colors.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = Money(simState.totalInterestMinor, "INR").format(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Repayment",
                                fontSize = 11.5.sp,
                                color = colors.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = Money(simState.totalPayableMinor, "INR").format(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackground
                            )
                        }
                    }

                    // Prepayment Savings Alert
                    val prepay = simState.prepaymentResult
                    if (prepay != null && prepay.interestSavedMinor > 0L) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "PREPAYMENT BENEFIT",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Saves ${Money(prepay.interestSavedMinor, "INR").format()} interest & reduces tenure by ${prepay.monthsSaved} months!",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inputs Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "LOAN PARAMETERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = principal,
                        onValueChange = { viewModel.updateEmiInputs(principal = it) },
                        label = { Text("Loan Principal (₹)") },
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rate,
                            onValueChange = { viewModel.updateEmiInputs(rate = it) },
                            label = { Text("Interest Rate (% APR)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
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

                        OutlinedTextField(
                            value = tenure,
                            onValueChange = { viewModel.updateEmiInputs(tenure = it) },
                            label = { Text("Tenure (Months)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = extraPayment,
                        onValueChange = { viewModel.updateEmiInputs(extraPayment = it) },
                        label = { Text("Extra Prepayment / Month (₹)") },
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
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
