package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.ui.IconUtils
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.Purple40
import com.example.bilance.ui.theme.Purple80
import com.example.bilance.ui.theme.PurpleGrey80
import com.example.bilance.ui.theme.LeagueSpartan
import com.example.bilance.ui.theme.Poppins
import com.example.bilance.ui.theme.SurfaceWhite
import com.example.bilance.ui.theme.TextGrey
import com.example.bilance.ui.theme.TextMuted
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.viewmodel.TransactionViewModelFactory
import com.example.bilance.viewmodel.SMSViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.bilance.UserPreferences

// UI Model for transaction display
data class TransactionDisplay(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val time: String,
    val date: String,
    val category: String,
    val amount: String,
    val amountColor: Color,
    val recipient: String? = null
)

// getMonthName function defined in MainActivity.kt

@Composable
fun TransactionScreen(
    navController: NavController? = null,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val expensePercentage by viewModel.expensePercentage.collectAsState()
    
    // Fix: Calculate expense base correctly
    val initialBalance = remember { UserPreferences.getInitialBalance() }
    val expenseBase = remember(initialBalance, totalIncome) { 
        initialBalance + totalIncome 
    }
    
    // Fix: Calculate actual balance including initial balance
    val actualBalance = remember(initialBalance, totalIncome, totalExpense) {
        initialBalance + totalIncome - totalExpense
    }
    
    // SMS ViewModel for unprocessed transactions
    val smsViewModel = remember { SMSViewModel(context.contentResolver) }
    
    val topCategories = remember(transactions) {
        transactions
            .filter { it.amountType == "expense" }
            .groupBy { it.category }
            .mapValues { (_, list: List<com.example.bilance.data.Transaction>) -> 
                list.sumOf { transaction -> transaction.amount } 
            }
            .toList()
            .sortedByDescending { (_, amount: Double) -> amount }
            .take(3)
    }
    
    // Calculate transactions by month reactively
    val transactionsByMonth = remember(transactions) {
        println("DEBUG: TransactionScreen - Recalculating transactionsByMonth with ${transactions.size} transactions")
        val grouped = transactions.groupBy { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            com.example.bilance.getMonthName(month) + " " + year
        }
        println("DEBUG: TransactionScreen - Grouped into ${grouped.size} months: ${grouped.keys}")
        grouped
    }
    
    // Ensure transactions are loaded when screen is displayed
    LaunchedEffect(Unit) {
        println("DEBUG: TransactionScreen LaunchedEffect - Loading transactions")
        viewModel.loadTransactions()
    }
    
    // Debug logging for balance calculations
    LaunchedEffect(actualBalance, totalBalance, totalIncome, totalExpense) {
        println("DEBUG: Balance calculations:")
        println("  Initial Balance: $initialBalance")
        println("  Total Income: $totalIncome")
        println("  Total Expense: $totalExpense")
        println("  ViewModel Balance: $totalBalance")
        println("  Calculated Balance: $actualBalance")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        com.example.bilance.ui.theme.GradientStart,
                        com.example.bilance.ui.theme.GradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Transactions",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (navController != null) {
                                navController.navigate("notifications_from_transactions")
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = com.example.bilance.ui.theme.TextOnPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.loadTransactions() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = com.example.bilance.ui.theme.TextOnPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Header styled like the reference - Fix: Use actualBalance instead of totalBalance
            AccountBalanceHeader(
                totalBalance = actualBalance, // Use calculated balance
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                expensePercentage = expensePercentage,
                expenseBase = expenseBase,
                modifier = Modifier.padding(horizontal = 18.dp)
            )

            // SMS Transactions Section (in gradient area)
            if (smsViewModel.notifications.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New Transactions (${smsViewModel.notifications.size})",
                            color = com.example.bilance.ui.theme.TextOnPrimary,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        TextButton(
                            onClick = { navController?.navigate("notifications_from_transactions") }
                        ) {
                            Text(
                                text = "View All",
                                color = com.example.bilance.ui.theme.AccentOrange,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Show first few SMS transactions as cards
                    smsViewModel.notifications.take(2).forEach { sms ->
                        SMSTransactionCard(
                            sms = sms,
                            onAddTransaction = { smsTransaction ->
                                viewModel.addTransactionFromSMS(
                                    smsTransaction = smsTransaction,
                                    finalCategory = smsTransaction.category.ifEmpty { "Uncategorized" },
                                    finalAmount = smsTransaction.amount
                                )
                                smsViewModel.removeSMS(smsTransaction)
                            },
                            onDeleteTransaction = { smsTransaction ->
                                smsViewModel.deleteSMS(smsTransaction)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Top categories (chips)
            if (topCategories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    topCategories.forEach { entry ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    "${entry.first} · ₹${String.format("%.0f", entry.second)}",
                                    color = SurfaceWhite,
                                    fontFamily = Poppins,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_layers),
                                    contentDescription = null,
                                    tint = Purple80
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Purple80.copy(alpha = 0.20f),
                                labelColor = SurfaceWhite
                            )
                        )
                    }
                }
            }

            // Check message outside card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = Purple80,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${String.format("%.0f", expensePercentage)}% of your expenses, looks good.",
                    color = SurfaceWhite,
                    fontFamily = LeagueSpartan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Transactions list with rounded background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(SurfaceWhite)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    // Regular Transactions
                    if (transactionsByMonth.isEmpty()) {
                        item {
                            // Show message when no transactions
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No transactions found",
                                    color = Purple40.copy(alpha = 0.6f),
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add a transaction to get started",
                                    color = TextMuted,
                                    fontFamily = Poppins,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        transactionsByMonth.forEach { (month: String, monthTransactions: List<com.example.bilance.data.Transaction>) ->
                            item {
                                Text(
                                    text = month,
                                    color = Purple40,
                                    fontSize = 20.sp,
                                    fontFamily = LeagueSpartan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        top = 16.dp,
                                        bottom = 12.dp
                                    )
                                )
                            }
                            items(monthTransactions) { transaction ->
                                TransactionItem(convertToDisplayTransaction(transaction))
                            }
                        }
                    }
                }
            }
            
            // Floating Action Button to add transaction
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        navController?.navigate("addExpense/Manual")
                    },
                    modifier = Modifier.padding(24.dp),
                    containerColor = Purple80
                ) {
                    Text(
                        text = "+",
                        color = SurfaceWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun convertToDisplayTransaction(transaction: com.example.bilance.data.Transaction): TransactionDisplay {
    val dateFormat = SimpleDateFormat("HH:mm - MMM dd", Locale.getDefault())
    val icon = IconUtils.getIconFromName(transaction.iconName)
    val amountColor =
        if (transaction.amountType == "income") Color(0xFF7B1FA2) else Color(0xFFB00020)
    val amountPrefix = if (transaction.amountType == "income") "" else "-"
    
    return TransactionDisplay(
        icon = icon,
        title = transaction.title,
        time = transaction.time,
        date = dateFormat.format(transaction.date),
        category = transaction.category,
        amount = "₹$amountPrefix${String.format("%.2f", transaction.amount)}",
        amountColor = amountColor,
        recipient = transaction.recipientName
    )
}

@Composable
fun AccountBalanceHeader(
    modifier: Modifier = Modifier,
    totalBalance: Double = 0.0,
    totalIncome: Double = 0.0,
    totalExpense: Double = 0.0,
    expensePercentage: Double = 0.0,
    expenseBase: Double = 0.0
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Top row: labels and amounts (styled like design)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Total Balance",
                    color = Purple40,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                // Fix: Format balance properly and handle negative values
                Text(
                    text = if (totalBalance >= 0) {
                        "₹${String.format("%,.2f", totalBalance)}"
                    } else {
                        "-₹${String.format("%,.2f", kotlin.math.abs(totalBalance))}"
                    },
                    color = if (totalBalance >= 0) SurfaceWhite else com.example.bilance.ui.theme.AccentRed,
                    fontSize = 28.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Expense",
                    color = Purple40,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${String.format("%,.2f", totalExpense)}", // Remove negative sign since it's already labeled as expense
                    color = Purple80,
                    fontSize = 28.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Progress pill (30% | goal amount)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(27.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Purple40),
        ) {
            val progress = (expensePercentage / 100.0).toFloat().coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Purple80),
            )
            Text(
                text = "${String.format("%.0f", expensePercentage)}%",
                color = SurfaceWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )
            Text(
                text = "₹${String.format("%,.2f", expenseBase)}",
                color = SurfaceWhite,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Two small cards: Income and Expense
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallStatCard(
                title = "Income",
                value = "₹${String.format("%,.2f", totalIncome)}",
                iconRes = R.drawable.ic_trending_up,
                container = Purple80.copy(alpha = 0.20f),
                modifier = Modifier.weight(1f)
            )
            SmallStatCard(
                title = "Expense",
                value = "₹${String.format("%,.2f", totalExpense)}",
                iconRes = R.drawable.ic_trending_down,
                container = Purple40.copy(alpha = 0.20f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    iconRes: Int,
    container: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(title, color = SurfaceWhite, fontSize = 14.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium)
            }
            Text(value, color = SurfaceWhite, fontSize = 18.sp, fontFamily = Poppins, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionDisplay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Purple80.copy(alpha = 0.20f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.icon,
                contentDescription = transaction.title,
                tint = Purple80,
                modifier = Modifier.size(28.dp)
            )
        }

        // Transaction details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                transaction.title,
                color = Purple40,
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    transaction.date,
                    color = Purple80,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
                Text(
                    "  |  ",
                    color = TextGrey,
                    fontSize = 14.sp
                )
                Text(
                    transaction.category,
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontFamily = LeagueSpartan
                )
                if (!transaction.recipient.isNullOrBlank()) {
                    Text(
                        "  |  ",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                    Text(
                        transaction.recipient ?: "",
                        color = TextGrey,
                        fontSize = 14.sp,
                        fontFamily = LeagueSpartan
                    )
                }
            }
        }

        // Amount
        Text(
            transaction.amount,
            color = transaction.amountColor,
            fontSize = 16.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SwipeableTransactionCard(
    sms: com.example.bilance.model.TransactionSMS,
    onAddTransaction: (com.example.bilance.model.TransactionSMS) -> Unit,
    onDeleteTransaction: (com.example.bilance.model.TransactionSMS) -> Unit,
    onViewDetails: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteTransaction(sms)
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Background shown when swiping (red delete background)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
        },
        content = {
            SMSTransactionCard(
                sms = sms,
                onAddTransaction = onAddTransaction,
                onViewDetails = onViewDetails
            )
        }
    )
}

@Composable
fun SMSTransactionCard(
    sms: com.example.bilance.model.TransactionSMS,
    onAddTransaction: (com.example.bilance.model.TransactionSMS) -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Purple80.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen.copy(alpha = 0.2f)
                                else com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (sms.type == "income") R.drawable.ic_trending_up 
                                     else R.drawable.ic_trending_down
                            ),
                            contentDescription = null,
                            tint = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "SMS Transaction",
                            color = SurfaceWhite,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = sms.amount,
                            color = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                // Add button
                OutlinedButton(
                    onClick = { onAddTransaction(sms) },
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Purple80
                    ),
                    border = BorderStroke(
                        1.dp, 
                        Purple80
                    )
                ) {
                    Text(
                        text = "Add",
                        fontSize = 12.sp,
                        fontFamily = Poppins
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = sms.message,
                color = SurfaceWhite.copy(alpha = 0.8f),
                fontFamily = Poppins,
                fontSize = 12.sp,
                maxLines = 2
            )
            
            if (!sms.recipient.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "To: ${sms.recipient}",
                    color = SurfaceWhite.copy(alpha = 0.6f),
                    fontFamily = Poppins,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SMSTransactionCard(
    sms: com.example.bilance.model.TransactionSMS,
    onAddTransaction: (com.example.bilance.model.TransactionSMS) -> Unit,
    onDeleteTransaction: (com.example.bilance.model.TransactionSMS) -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.95f)
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen.copy(alpha = 0.15f)
                                else com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(
                                id = if (sms.type == "income") R.drawable.ic_trending_up 
                                     else R.drawable.ic_trending_down
                            ),
                            contentDescription = null,
                            tint = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        androidx.compose.material3.Text(
                            text = "SMS Transaction",
                            color = com.example.bilance.ui.theme.TextPrimary,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        androidx.compose.material3.Text(
                            text = sms.amount,
                            color = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (sms.category.isNotEmpty()) {
                            androidx.compose.material3.Text(
                                text = sms.category,
                                color = com.example.bilance.ui.theme.TextSecondary,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    androidx.compose.material3.Button(
                        onClick = { onAddTransaction(sms) },
                        modifier = Modifier.height(36.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = com.example.bilance.ui.theme.AccentGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Add",
                            fontSize = 12.sp,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            color = com.example.bilance.ui.theme.TextOnPrimary
                        )
                    }
                    
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onDeleteTransaction(sms) },
                        modifier = Modifier.height(36.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.bilance.ui.theme.AccentRed
                        ),
                        border = BorderStroke(1.dp, com.example.bilance.ui.theme.AccentRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Delete",
                            fontSize = 12.sp,
                            fontFamily = com.example.bilance.ui.theme.Poppins
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CleanSMSTransactionCard(
    sms: com.example.bilance.model.TransactionSMS,
    onAddTransaction: (com.example.bilance.model.TransactionSMS) -> Unit,
    onDeleteTransaction: (com.example.bilance.model.TransactionSMS) -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = com.example.bilance.ui.theme.BackgroundPrimary
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen.copy(alpha = 0.2f)
                                else com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(
                                id = if (sms.type == "income") R.drawable.ic_trending_up 
                                     else R.drawable.ic_trending_down
                            ),
                            contentDescription = null,
                            tint = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        androidx.compose.material3.Text(
                            text = "SMS Transaction",
                            color = com.example.bilance.ui.theme.TextPrimary,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        androidx.compose.material3.Text(
                            text = sms.amount,
                            color = if (sms.type == "income") com.example.bilance.ui.theme.AccentGreen 
                                   else com.example.bilance.ui.theme.AccentRed,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onAddTransaction(sms) },
                        modifier = Modifier.height(32.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.bilance.ui.theme.AccentGreen
                        ),
                        border = BorderStroke(1.dp, com.example.bilance.ui.theme.AccentGreen)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Add",
                            fontSize = 12.sp,
                            fontFamily = com.example.bilance.ui.theme.Poppins
                        )
                    }
                    
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onDeleteTransaction(sms) },
                        modifier = Modifier.height(32.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.bilance.ui.theme.AccentRed
                        ),
                        border = BorderStroke(1.dp, com.example.bilance.ui.theme.AccentRed)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Delete",
                            fontSize = 12.sp,
                            fontFamily = com.example.bilance.ui.theme.Poppins
                        )
                    }
                }
            }
            
            if (sms.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    text = sms.message.take(100) + if (sms.message.length > 100) "..." else "",
                    color = com.example.bilance.ui.theme.TextSecondary,
                    fontFamily = com.example.bilance.ui.theme.Poppins,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val factory = TransactionViewModelFactory(BilanceDatabase.getDatabase(context))
    val viewModel: TransactionViewModel = viewModel(factory = factory)
    BilanceTheme {
        TransactionScreen(navController = navController, viewModel = viewModel)
    }
}