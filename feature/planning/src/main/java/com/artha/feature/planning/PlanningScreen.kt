package com.artha.feature.planning

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.GoalEntity
import com.artha.domain.BudgetConsumption
import com.artha.domain.SubscriptionRenewalStatus
import com.artha.domain.SubscriptionSummary
import com.artha.feature.planning.add.AddBudgetSheet
import com.artha.feature.planning.add.AddGoalSheet
import com.artha.feature.planning.add.AddSubscriptionSheet
import com.artha.model.Money
import com.artha.ui.theme.LocalArthaColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanningScreen(
    viewModel: PlanningViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Budgets, 1: Subscriptions, 2: Goals

    var showAddBudgetSheet by remember { mutableStateOf(false) }
    var showAddSubSheet by remember { mutableStateOf(false) }
    var showAddGoalSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Header & Tab Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Planning & Budgets",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = "Envelopes, renewals, sinking funds & safe-to-spend",
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
                    val tabs = listOf("Budgets", "Subscriptions", "Goals")
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
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) colors.background else colors.onBackground
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> BudgetsTabContent(viewModel = viewModel)
                1 -> SubscriptionsTabContent(viewModel = viewModel)
                2 -> GoalsTabContent(viewModel = viewModel)
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                when (selectedTab) {
                    0 -> showAddBudgetSheet = true
                    1 -> showAddSubSheet = true
                    2 -> showAddGoalSheet = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = colors.primary,
            contentColor = colors.background,
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
        }

        if (showAddBudgetSheet) {
            AddBudgetSheet(viewModel = viewModel, onDismiss = { showAddBudgetSheet = false })
        }
        if (showAddSubSheet) {
            AddSubscriptionSheet(viewModel = viewModel, onDismiss = { showAddSubSheet = false })
        }
        if (showAddGoalSheet) {
            AddGoalSheet(viewModel = viewModel, onDismiss = { showAddGoalSheet = false })
        }
    }
}

@Composable
private fun BudgetsTabContent(viewModel: PlanningViewModel) {
    val colors = LocalArthaColors.current
    val consumptions by viewModel.budgetsWithConsumption.collectAsState()
    val daysLeft = viewModel.daysRemainingInMonth

    // Calculate overall daily safe-to-spend
    val totalRemainingMinor = consumptions.sumOf { it.remainingMinor.coerceAtLeast(0L) }
    val totalSafeDailyMinor = if (daysLeft > 0) totalRemainingMinor / daysLeft else 0L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Safe to spend banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "SAFE-TO-SPEND ALLOWANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${Money(totalSafeDailyMinor, "INR").format()} / day",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Calculated across $daysLeft days remaining this month",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        if (consumptions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No budgets created yet.\nTap + below to create a monthly envelope.",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(consumptions, key = { it.budgetId }) { item ->
                BudgetConsumptionCard(
                    consumption = item,
                    onDelete = { viewModel.deleteBudget(item.budgetId) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun BudgetConsumptionCard(
    consumption: BudgetConsumption,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val progress = consumption.consumptionRatio.coerceIn(0f, 1f)

    val barColor = when {
        consumption.isOverBudget -> Color(0xFFEF4444) // Red
        consumption.isNearThreshold -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFF10B981) // Green
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = consumption.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Spent ${Money(consumption.spentMinor, consumption.currency).format()} of ${Money(consumption.budgetAmountMinor, consumption.currency).format()}",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.6f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (consumption.isOverBudget) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Exceeded",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    } else if (consumption.isNearThreshold) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Near Limit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = colors.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor,
                trackColor = colors.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(consumption.consumptionRatio * 100).toInt()}% used",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = barColor
                )
                Text(
                    text = "${Money(consumption.remainingMinor, consumption.currency).format()} remaining",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SubscriptionsTabContent(viewModel: PlanningViewModel) {
    val colors = LocalArthaColors.current
    val summary by viewModel.subscriptionSummary.collectAsState()
    val statuses by viewModel.subscriptionRenewalStatuses.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Annual Rollup Summary Hero Card
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
                        text = "YEARLY SUBSCRIPTIONS COST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Money(summary.totalYearlyCostMinor, summary.currency).format(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Text(
                        text = "Equivalent to ${Money(summary.totalMonthlyCostMinor, summary.currency).format()} / month across ${summary.activeCount} active subscriptions",
                        fontSize = 12.sp,
                        color = colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (statuses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No subscriptions added.\nTap + below to track recurring bills & services.",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(statuses, key = { it.subscription.id }) { item ->
                SubscriptionItemCard(
                    status = item,
                    onMarkPaid = { viewModel.markSubscriptionPaid(item.subscription) },
                    onDelete = { viewModel.deleteSubscription(item.subscription.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun SubscriptionItemCard(
    status: SubscriptionRenewalStatus,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val sub = status.subscription

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Subscriptions,
                            contentDescription = sub.name,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = sub.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "${sub.frequency.name.lowercase().replaceFirstChar { it.uppercase() }} • Due day ${sub.dueDay}",
                            fontSize = 11.5.sp,
                            color = colors.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Money(sub.amountMinor, sub.currency).format(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = colors.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (status.isDueSoon) {
                    Text(
                        text = "Due in ${status.daysUntilDue} days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                } else {
                    Text(
                        text = "Annual: ${Money(status.yearlyEquivalentMinor, sub.currency).format()}",
                        fontSize = 11.5.sp,
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }

                Button(
                    onClick = onMarkPaid,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.background
                    )
                ) {
                    Text("Mark Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GoalsTabContent(viewModel: PlanningViewModel) {
    val colors = LocalArthaColors.current
    val goals by viewModel.goals.collectAsState()

    val sdf = remember { SimpleDateFormat("MMM yyyy", Locale.US) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (goals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No savings goals created.\nTap + below to set a target for a trip, fund or purchase.",
                        color = colors.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(goals, key = { it.id }) { goal ->
                val progress = if (goal.targetAmountMinor > 0) {
                    (goal.savedAmountMinor.toFloat() / goal.targetAmountMinor.toFloat()).coerceIn(0f, 1f)
                } else 0f
                val percent = (progress * 100).toInt()

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Flag,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = goal.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onBackground
                                    )
                                    Text(
                                        text = "Target: ${sdf.format(Date(goal.targetDate))}",
                                        fontSize = 11.5.sp,
                                        color = colors.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$percent%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteGoal(goal.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        tint = colors.onBackground.copy(alpha = 0.4f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = colors.onBackground.copy(alpha = 0.1f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved: ${Money(goal.savedAmountMinor, goal.currency).format()} / ${Money(goal.targetAmountMinor, goal.currency).format()}",
                                fontSize = 11.5.sp,
                                color = colors.onBackground.copy(alpha = 0.6f)
                            )

                            // Quick add contribution
                            OutlinedButton(
                                onClick = { viewModel.contributeToGoal(goal.id, 100000L) }, // +₹1,000
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("+₹1,000", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
