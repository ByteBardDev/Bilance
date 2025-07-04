package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.SplashBackgroundBlue

// Model for transaction
data class Transaction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val time: String,
    val date: String,
    val category: String,
    val amount: String,
    val amountColor: Color
)

@Composable
fun TransactionScreen(navController: NavController? = null) {
    val transactionsByMonth = mapOf(
        "April" to listOf(
            Transaction(
                icon = Icons.Default.AttachMoney,
                title = "Salary",
                time = "18:27",
                date = "April 30",
                category = "Monthly",
                amount = "$4,000.00",
                amountColor = Color(0xFF388E3C)
            ),
            Transaction(
                icon = Icons.Default.ShoppingCart,
                title = "Groceries",
                time = "17:00",
                date = "April 24",
                category = "Pantry",
                amount = "-$100.00",
                amountColor = Color(0xFFB00020)
            ),
            Transaction(
                icon = Icons.Default.Home,
                title = "Rent",
                time = "8:30",
                date = "April 15",
                category = "Rent",
                amount = "-$674.40",
                amountColor = Color(0xFFB00020)
            ),
            Transaction(
                icon = Icons.Default.DirectionsCar,
                title = "Transport",
                time = "9:30",
                date = "April 08",
                category = "Fuel",
                amount = "-$4.13",
                amountColor = Color(0xFFB00020)
            )
        ),
        "March" to listOf(
            Transaction(
                icon = Icons.Default.Restaurant,
                title = "Food",
                time = "19:30",
                date = "March 31",
                category = "Dinner",
                amount = "-$70.40",
                amountColor = Color(0xFFB00020)
            )
        )
    )

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
                IconButton(onClick = { /* Handle back navigation */ }) {
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
                IconButton(onClick = { 
                    navController?.navigate("notifications")
                }) {
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
            }

            // Balance Card
            BalanceCard(
                modifier = Modifier.padding(horizontal = 16.dp)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFFF1FFF3)),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                transactionsByMonth.forEach { (month, transactions) ->
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
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(modifier: Modifier = Modifier) {
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
                "$7,783.00",
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
            // Total Balance column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Total Balance",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "$7,783.00",
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
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Total Expense",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "-$1,187.40",
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
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF052224))
            )
            // Progress text
            Text(
                "30%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )
            // Goal amount
            Text(
                "$20,000.00",
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
fun TransactionItem(transaction: Transaction) {
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
                    "${transaction.time} - ${transaction.date}",
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
    BilanceTheme {
        TransactionScreen(navController = navController)
    }
}