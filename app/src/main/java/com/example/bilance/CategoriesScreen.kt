package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilance.viewmodel.TransactionViewModel
import java.util.Locale

data class CategoryData(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val amount: Double
)

private fun calculateCategoryAmount(transactions: List<com.example.bilance.data.Transaction>, category: String): Double {
    return transactions
        .filter {
            it.category.equals(category, ignoreCase = true) &&
                    it.amountType == "expense"
        }
        .sumOf { it.amount }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate total balance and expenses from actual transaction data
    val transactions = viewModel.transactions.collectAsState().value
    val totalBalance = transactions
        .filter { it.amountType == "income" }
        .sumOf { it.amount }
    val totalExpenses = transactions
        .filter { it.amountType == "expense" }
        .sumOf { it.amount }
    
    // Category data with actual amounts from transactions
    val categories = listOf(
        CategoryData("Food", Icons.Default.Restaurant, Color(0xFF3B82F6),
            calculateCategoryAmount(transactions, "Food")),
        CategoryData("Transport", Icons.Default.DirectionsCar, Color(0xFF8B5CF6),
            calculateCategoryAmount(transactions, "Transport")),
        CategoryData("Medicine", Icons.Default.LocalHospital, Color(0xFF10B981),
            calculateCategoryAmount(transactions, "Medicine")),
        CategoryData("Groceries", Icons.Default.ShoppingCart, Color(0xFFF59E0B),
            calculateCategoryAmount(transactions, "Groceries")),
        CategoryData("Rent", Icons.Default.Home, Color(0xFFEF4444),
            calculateCategoryAmount(transactions, "Rent")),
        CategoryData("Gift", Icons.Default.CardGiftcard, Color(0xFF06B6D4),
            calculateCategoryAmount(transactions, "Gift")),
        CategoryData("Savings", Icons.Default.Savings, Color(0xFF84CC16),
            calculateCategoryAmount(transactions, "Savings")),
        CategoryData("Entertainment", Icons.Default.Movie, Color(0xFFEC4899),
            calculateCategoryAmount(transactions, "Entertainment")),
        CategoryData("More", Icons.Default.Add, Color(0xFF6B7280), 0.0)
    )

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Categories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("addCustomCategory")
                    }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Custom Category",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF283A5F)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Balance Cards Section - Fixed overlapping
            BalanceCards(
                totalBalance = totalBalance,
                totalExpenses = totalExpenses,
                screenWidth = screenWidth
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Categories Grid - Improved responsiveness
            CategoriesGrid(
                categories = categories,
                navController = navController,
                screenWidth = screenWidth
            )
        }
    }
}

@Composable
fun BalanceCards(
    totalBalance: Double,
    totalExpenses: Double,
    screenWidth: Dp
) {
    // Responsive card height based on screen size
    val cardHeight = when {
        screenWidth < 360.dp -> 120.dp
        screenWidth < 400.dp -> 140.dp
        else -> 160.dp
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Balance Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(cardHeight),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF283A5F)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = String.format(Locale.US, "₹%.2f", totalBalance),
                    fontSize = when {
                        screenWidth < 360.dp -> 18.sp
                        screenWidth < 400.dp -> 20.sp
                        else -> 24.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "30%",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                LinearProgressIndicator(
                    progress = { 0.3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Text(
                    text = "of ₹20,000.00",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Total Expenses Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(cardHeight),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(Locale.US, "-₹%.2f", totalExpenses),
                    fontSize = when {
                        screenWidth < 360.dp -> 18.sp
                        screenWidth < 400.dp -> 20.sp
                        else -> 24.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total Expenses",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun CategoriesGrid(
    categories: List<CategoryData>,
    navController: NavController,
    screenWidth: Dp
) {
    // Responsive columns and card size
    val columns = when {
        screenWidth < 360.dp -> 2
        screenWidth < 600.dp -> 3
        else -> 4
    }

    // Calculate appropriate card size
    val cardSize = when {
        screenWidth < 360.dp -> 80.dp
        screenWidth < 400.dp -> 90.dp
        else -> 100.dp
    }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for navigation
        ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                cardSize = cardSize,
                onClick = {
                    if (category.name != "More") {
                        navController.navigate("categoryDetail/${category.name}")
                    } else {
                        navController.navigate("addCustomCategory")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryData,
    cardSize: Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = category.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748)
            )

            if (category.amount > 0) {
                Text(
                    text = String.format(Locale.US, "₹%.0f", category.amount),
                    fontSize = 8.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
