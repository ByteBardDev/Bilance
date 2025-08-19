// File: CategoryDetailScreen.kt
package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilance.model.TransactionSMS
import com.example.bilance.viewmodel.TransactionViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryName: String,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val transactions by remember {
        derivedStateOf {
            viewModel.transactions.value.filter { it.category == categoryName }
        }
    }

    val totalAmount by remember {
        derivedStateOf {
            transactions.sumOf { transaction ->
                if (transaction.amountType == "expense") -transaction.amount else transaction.amount
            }
        }
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
            // Modern Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = categoryName,
                    fontSize = 20.sp,
                    fontFamily = com.example.bilance.ui.theme.Poppins,
                    fontWeight = FontWeight.Bold,
                    color = com.example.bilance.ui.theme.TextOnPrimary
                )

                androidx.compose.material3.FloatingActionButton(
                    onClick = {
                        navController.navigate("addExpense/${categoryName}")
                    },
                    containerColor = com.example.bilance.ui.theme.AccentGreen,
                    contentColor = com.example.bilance.ui.theme.TextOnPrimary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Modern card container
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.BackgroundPrimary
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    // Modern Category Summary Card
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = com.example.bilance.ui.theme.SurfaceElevated
                        ),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Total Spent",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", kotlin.math.abs(totalAmount))}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = if (totalAmount < 0) com.example.bilance.ui.theme.AccentRed else com.example.bilance.ui.theme.AccentGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${transactions.size} transactions",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextMuted
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Transactions List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (transactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "No transactions yet",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = com.example.bilance.ui.theme.Poppins,
                                            color = com.example.bilance.ui.theme.TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Tap the + button to add your first expense",
                                            fontSize = 14.sp,
                                            fontFamily = com.example.bilance.ui.theme.Poppins,
                                            color = com.example.bilance.ui.theme.TextMuted,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            items(transactions) { transaction ->
                                TransactionCard(
                                    transaction = transaction,
                                    onClick = {
                                        navController.navigate("transactionDetail/${transaction.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: com.example.bilance.data.Transaction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon/Indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (transaction.amountType == "expense")
                            Color(0xFFFFEBEE)
                        else
                            Color(0xFFE8F5E8)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (transaction.amountType == "expense") "−" else "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amountType == "expense")
                        Color(0xFFD32F2F)
                    else
                        Color(0xFF388E3C)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF283A5F)
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Amount
            Text(
                text = String.format(Locale.US, "₹%.2f", transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.amountType == "expense")
                    Color(0xFFD32F2F)
                else
                    Color(0xFF388E3C)
            )
        }
    }
}
