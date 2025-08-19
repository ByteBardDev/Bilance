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
    val iconRes: Int,
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
        "food", "food & dining" -> CategoryData(categoryName, R.drawable.ic_food, Color(0xFF6CB5FD), amount)
        "transport", "transportation" -> CategoryData(categoryName, R.drawable.ic_transport, Color(0xFF84CC16), amount)
        "groceries" -> CategoryData(categoryName, R.drawable.ic_groceries, Color(0xFF7BB3FF), amount)
        "rent" -> CategoryData(categoryName, R.drawable.ic_rent, Color(0xFFEF4444), amount)
        "entertainment" -> CategoryData(categoryName, R.drawable.ic_chart, Color(0xFFEC4899), amount)
        "salary" -> CategoryData(categoryName, R.drawable.ic_salary, Color(0xFF10B981), amount)
        "transfer" -> CategoryData(categoryName, R.drawable.ic_transfer, Color(0xFF06B6D4), amount)
        "medicine", "medical", "health" -> CategoryData(categoryName, R.drawable.ic_help, Color(0xFFF59E0B), amount)
        "savings" -> CategoryData(categoryName, R.drawable.ic_salary, Color(0xFFC8E6C9), amount)
        else -> CategoryData(categoryName, R.drawable.ic_layers, Color(0xFF9CA3AF), amount)
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
        CategoryData("Food", R.drawable.ic_food, Color(0xFF6CB5FD), 0.0),
        CategoryData("Transport", R.drawable.ic_transport, Color(0xFF84CC16), 0.0),
        CategoryData("Groceries", R.drawable.ic_groceries, Color(0xFF7BB3FF), 0.0),
        CategoryData("Rent", R.drawable.ic_rent, Color(0xFFEF4444), 0.0),
        CategoryData("Entertainment", R.drawable.ic_chart, Color(0xFFEC4899), 0.0),
        CategoryData("Salary", R.drawable.ic_salary, Color(0xFF10B981), 0.0)
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
        displayCategories.add(CategoryData("More", R.drawable.ic_layers, Color(0xFFB2B2B2), 0.0))
        
        displayCategories
    }

    Scaffold(
        containerColor = ModernCategoryBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Categories",
                        fontSize = 22.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = com.example.bilance.ui.theme.TextOnPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = com.example.bilance.ui.theme.TextOnPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("addCustomCategory") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Custom Category",
                            tint = com.example.bilance.ui.theme.TextOnPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ModernAppBar
                ),
                modifier = Modifier.height(64.dp)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            com.example.bilance.ui.theme.BackgroundPrimary,
                            com.example.bilance.ui.theme.BackgroundSecondary
                        )
                    )
                )
        ) {
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
                        .height(90.dp),
                    colors = CardDefaults.cardColors(containerColor = ModernAppBar),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Balance",
                            color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.US, "₹%,.2f", totalBalance),
                            color = com.example.bilance.ui.theme.TextOnPrimary,
                            fontFamily = LeagueSpartan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    colors = CardDefaults.cardColors(containerColor = ModernWhite),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Expense",
                            color = Color(0xFF8C8A8A),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.US, "-₹%,.2f", totalExpense),
                            color = Color(0xFF6CB5FD),
                            fontFamily = LeagueSpartan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
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
    val cardSize = 94.dp
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
    val CardBgWhite = Color(0xFFF1FFF2)
    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBgWhite),
        shape = RoundedCornerShape(17.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 8.dp, start = 7.dp, end = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = if (category.name == "More") 0.10f else 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = category.name,
                fontSize = 13.sp,
                fontFamily = LeagueSpartan,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF093030),
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            if (category.amount > 0) {
                Text(
                    text = String.format(Locale.US, "₹%,.0f", category.amount),
                    fontSize = 11.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9A9696)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

