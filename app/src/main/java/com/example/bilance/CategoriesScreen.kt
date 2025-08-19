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
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilance.data.Transaction
import com.example.bilance.ui.theme.LeagueSpartan
import com.example.bilance.ui.theme.Poppins
import com.example.bilance.viewmodel.TransactionViewModel
import java.util.Locale

data class CategoryData(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val amount: Double
)

private fun calculateCategoryAmount(transactions: List<Transaction>, category: String): Double {
    return transactions
        .filter {
            it.category.equals(category, ignoreCase = true) &&
                    it.amountType == "expense"
        }
        .sumOf { it.amount }
}

private fun getCategoryData(categoryName: String, amount: Double): CategoryData {
    return when (categoryName.lowercase()) {
        "food", "food & dining", "restaurant" -> CategoryData(categoryName, Icons.Default.Restaurant, Color(0xFF6CB5FD), amount)
        "transport", "transportation", "travel" -> CategoryData(categoryName, Icons.Default.DirectionsCar, Color(0xFF84CC16), amount)
        "groceries", "shopping" -> CategoryData(categoryName, Icons.Default.LocalGroceryStore, Color(0xFF7BB3FF), amount)
        "rent", "housing", "home" -> CategoryData(categoryName, Icons.Default.Home, Color(0xFFEF4444), amount)
        "entertainment", "movies", "games" -> CategoryData(categoryName, Icons.Default.Movie, Color(0xFFEC4899), amount)
        "salary", "income", "earnings" -> CategoryData(categoryName, Icons.Default.AccountBalance, Color(0xFF10B981), amount)
        "transfer", "banking" -> CategoryData(categoryName, Icons.Default.SwapHoriz, Color(0xFF06B6D4), amount)
        "medicine", "medical", "health", "healthcare" -> CategoryData(categoryName, Icons.Default.LocalHospital, Color(0xFFF59E0B), amount)
        "savings", "investment" -> CategoryData(categoryName, Icons.Default.Savings, Color(0xFFC8E6C9), amount)
        "bills", "utilities" -> CategoryData(categoryName, Icons.Default.AttachMoney, Color(0xFF8B5CF6), amount)
        "education", "school", "tuition" -> CategoryData(categoryName, Icons.Default.School, Color(0xFF3B82F6), amount)
        "fitness", "gym", "sports" -> CategoryData(categoryName, Icons.Default.FitnessCenter, Color(0xFF10B981), amount)
        "travel", "vacation", "flight" -> CategoryData(categoryName, Icons.Default.Flight, Color(0xFFF59E0B), amount)
        "shopping", "clothing", "fashion" -> CategoryData(categoryName, Icons.Default.ShoppingCart, Color(0xFFEC4899), amount)
        "credit card", "card payment" -> CategoryData(categoryName, Icons.Default.CreditCard, Color(0xFFEF4444), amount)
        else -> CategoryData(categoryName, Icons.Default.Category, Color(0xFF9CA3AF), amount)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val transactions = viewModel.transactions.collectAsState().value
    val totalIncome = transactions.filter { it.amountType == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.amountType == "expense" }.sumOf { it.amount }
    val totalBalance =
        com.example.bilance.UserPreferences.getInitialBalance() + totalIncome - totalExpense
    val ModernCategoryBg = com.example.bilance.ui.theme.BackgroundPrimary
    val ModernAppBar = com.example.bilance.ui.theme.PrimaryBlue
    val ModernWhite = com.example.bilance.ui.theme.SurfaceElevated
    val ModernLabelText = com.example.bilance.ui.theme.TextPrimary
    val ModernAmountText = com.example.bilance.ui.theme.TextSecondary
    // Get all unique categories from actual transactions
    val actualCategories = remember(transactions) {
        transactions
            .filter { it.category.isNotBlank() }
            .groupBy { it.category }
            .map { (category, categoryTransactions) ->
                getCategoryData(category, categoryTransactions.sumOf { if (it.amountType == "expense") it.amount else 0.0 })
            }
            .sortedByDescending { it.amount }
    }
    
    // Combine with predefined categories (only show predefined ones that have transactions or are commonly used)
    val predefinedCategories = listOf(
        CategoryData("Food", Icons.Default.Restaurant, Color(0xFF6CB5FD), 0.0),
        CategoryData("Transport", Icons.Default.DirectionsCar, Color(0xFF84CC16), 0.0),
        CategoryData("Groceries", Icons.Default.LocalGroceryStore, Color(0xFF7BB3FF), 0.0),
        CategoryData("Rent", Icons.Default.Home, Color(0xFFEF4444), 0.0),
        CategoryData("Entertainment", Icons.Default.Movie, Color(0xFFEC4899), 0.0),
        CategoryData("Salary", Icons.Default.AccountBalance, Color(0xFF10B981), 0.0),
        CategoryData("Bills", Icons.Default.AttachMoney, Color(0xFF8B5CF6), 0.0),
        CategoryData("Shopping", Icons.Default.ShoppingCart, Color(0xFFEC4899), 0.0),
        CategoryData("Health", Icons.Default.LocalHospital, Color(0xFFF59E0B), 0.0),
        CategoryData("Education", Icons.Default.School, Color(0xFF3B82F6), 0.0),
        CategoryData("Fitness", Icons.Default.FitnessCenter, Color(0xFF10B981), 0.0),
        CategoryData("Travel", Icons.Default.Flight, Color(0xFFF59E0B), 0.0)
    )
    
    val allCategories = remember(actualCategories) {
        val actualCategoryNames = actualCategories.map { it.name.lowercase() }
        val displayCategories = mutableListOf<CategoryData>()
        
        // Add actual categories with real amounts
        displayCategories.addAll(actualCategories)
        
        // Add predefined categories that don't have transactions yet (useful for quick access)
        predefinedCategories.forEach { predefined ->
            if (!actualCategoryNames.contains(predefined.name.lowercase())) {
                displayCategories.add(predefined)
            }
        }
        
        // Add "More" button at the end
        displayCategories.add(CategoryData("More", Icons.Default.Category, Color(0xFFB2B2B2), 0.0))
        
        displayCategories
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
            // Modern header
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
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Categories",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 22.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { navController.navigate("addCustomCategory") },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Custom Category",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Balance header (rounded, Figma spacing)
                Spacer(modifier = Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Total Balance",
                                color = com.example.bilance.ui.theme.TextSecondary.copy(alpha = 0.8f),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "₹%,.2f", totalBalance),
                                color = com.example.bilance.ui.theme.TextSecondary,
                                fontFamily = LeagueSpartan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = com.example.bilance.ui.theme.SurfaceElevated),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Total Expense",
                                color = com.example.bilance.ui.theme.TextSecondary,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "₹%,.2f", totalExpense),
                                color = com.example.bilance.ui.theme.PrimaryBlue,
                                fontFamily = LeagueSpartan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                // Category grid with real data (Modern spacing)
                CategoriesGridFigma(allCategories, navController)
            }
        }
    }
}

@Composable
fun CategoriesGridFigma(
    categories: List<CategoryData>,
    navController: NavController
) {
    val columns = 3 // Figma reference
    val cardSize = 112.dp
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        items(categories) { category ->
            CategoryCardFigma(
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
fun CategoryCardFigma(
    category: CategoryData,
    cardSize: Dp,
    onClick: () -> Unit
) {
    val CardBgWhite = Color(0xFFF8FFFE)
    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBgWhite),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 12.dp, start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (category.name == "More") {
                            category.color.copy(alpha = 0.15f)
                        } else {
                            category.color.copy(alpha = 0.25f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = category.name,
                fontSize = 15.sp,
                fontFamily = LeagueSpartan,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (category.amount > 0) {
                Text(
                    text = String.format(Locale.US, "₹%,.0f", category.amount),
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = category.color
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

