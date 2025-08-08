package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.ui.IconUtils
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.SplashBackgroundBlue
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// UI Model for transaction display
data class TransactionDisplay(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val time: String,
    val date: String,
    val category: String,
    val amount: String,
    val amountColor: Color
)

private fun getMonthName(month: Int): String {
    return when (month) {
        Calendar.JANUARY -> "January"
        Calendar.FEBRUARY -> "February"
        Calendar.MARCH -> "March"
        Calendar.APRIL -> "April"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "August"
        Calendar.SEPTEMBER -> "September"
        Calendar.OCTOBER -> "October"
        Calendar.NOVEMBER -> "November"
        Calendar.DECEMBER -> "December"
        else -> "Unknown"
    }
}

@Composable
fun TransactionScreen(
    navController: NavController? = null,
    viewModel: TransactionViewModel
) {
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val expensePercentage by viewModel.expensePercentage.collectAsState()
    
    // Calculate transactions by month reactively
    val transactionsByMonth = remember(transactions) {
        println("DEBUG: TransactionScreen - Recalculating transactionsByMonth with ${transactions.size} transactions")
        val grouped = transactions.groupBy { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            getMonthName(month) + " " + year
        }
        println("DEBUG: TransactionScreen - Grouped into ${grouped.size} months: ${grouped.keys}")
        grouped
    }
    
    // Ensure transactions are loaded when screen is displayed
    LaunchedEffect(Unit) {
        println("DEBUG: TransactionScreen LaunchedEffect - Loading transactions")
        viewModel.loadTransactions()
    }
    
    // Debug logging for transactions
    LaunchedEffect(transactions) {
        println("DEBUG: TransactionScreen - Transactions updated: ${transactions.size} transactions")
        println("DEBUG: TransactionScreen - Transaction titles: ${transactions.map { it.title }}")
        println("DEBUG: TransactionScreen - ViewModel instance: ${viewModel.hashCode()}")
    }
    
    // Debug logging for transactionsByMonth
    LaunchedEffect(transactionsByMonth) {
        println("DEBUG: TransactionScreen - transactionsByMonth updated: ${transactionsByMonth.size} months")
        transactionsByMonth.forEach { (month, monthTransactions) ->
            println("DEBUG: TransactionScreen - Month: $month, Transactions: ${monthTransactions.size}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF283A5F)) // Navy blue background for entire screen
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Transaction",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { navController?.navigate("notifications") }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(onClick = { viewModel.loadTransactions() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Balance Card
            BalanceCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                totalBalance = totalBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                expensePercentage = expensePercentage
            )

            // Check message outside card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)

                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "30% Of Your Expenses, Looks Good.",
                    color = Color.White,
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
                    .background(Color(0xFFF8F9FA))
            ) {
                if (transactionsByMonth.isEmpty()) {
                    // Show message when no transactions
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No transactions found",
                            color = Color(0xFF6B7280),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add a transaction to get started",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp
                        )
                        println("DEBUG: Showing empty state - transactionsByMonth is empty")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                    ) {
                        println("DEBUG: LazyColumn - Rendering ${transactionsByMonth.size} months")
                        transactionsByMonth.forEach { entry ->
                            val month = entry.key
                            val monthTransactions = entry.value
                            println("DEBUG: LazyColumn - Rendering month: $month with ${monthTransactions.size} transactions")
                            item {
                                Text(
                                    text = month,
                                    color = Color(0xFF052224),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        top = 16.dp,
                                        bottom = 12.dp
                                    )
                                )
                            }
                            items(monthTransactions) { transaction ->
                                println("DEBUG: LazyColumn - Rendering transaction: ${transaction.title}")
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
                    containerColor = SplashBackgroundBlue
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Transaction",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

fun convertToDisplayTransaction(transaction: com.example.bilance.data.Transaction): TransactionDisplay {
    val dateFormat = SimpleDateFormat("HH:mm - MMM dd", Locale.getDefault())
    val icon = IconUtils.getIconFromName(transaction.iconName)
    val amountColor = if (transaction.amountType == "income") Color(0xFF388E3C) else Color(0xFFB00020)
    val amountPrefix = if (transaction.amountType == "income") "" else "-"
    
    return TransactionDisplay(
        icon = icon,
        title = transaction.title,
        time = transaction.time,
        date = dateFormat.format(transaction.date),
        category = transaction.category,
        amount = "₹$amountPrefix${String.format("%.2f", transaction.amount)}",
        amountColor = amountColor
    )
}

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    totalBalance: Double = 0.0,
    totalIncome: Double = 0.0,
    totalExpense: Double = 0.0,
    expensePercentage: Double = 0.0
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Top balance card (white rounded)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total Balance",
                color = Color(0xFF052224),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "₹${String.format("%.2f", totalBalance)}",
                color = Color(0xFF052224),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance and Expense row (on navy background)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total Income column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Income",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "₹${String.format("%.2f", totalIncome)}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Total Expense column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Expense",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "₹${String.format("%.2f", totalExpense)}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            // Background bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
            )
            // Progress bar
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .fillMaxWidth((expensePercentage / 100).toFloat())
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF052224))
            )
            // Progress text
            Text(
                "${String.format("%.0f", expensePercentage)}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )
            // Goal amount
            Text(
                "₹20,000.00",
                color = Color(0xFF052224),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
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
                .background(SplashBackgroundBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.icon,
                contentDescription = transaction.title,
                tint = SplashBackgroundBlue,
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
                color = Color(0xFF052224),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    transaction.date,
                    color = SplashBackgroundBlue,
                    fontSize = 14.sp
                )
                Text(
                    "  |  ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    transaction.category,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Amount
        Text(
            transaction.amount,
            color = transaction.amountColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
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