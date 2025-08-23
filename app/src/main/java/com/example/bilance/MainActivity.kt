package com.example.bilance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.DatabaseUtils
import com.example.bilance.ui.theme.*
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.viewmodel.TransactionViewModelFactory
import com.example.bilance.viewmodel.SMSViewModel
import kotlinx.coroutines.delay
import com.example.bilance.ui.SetupInitialBalanceScreen
import java.util.Calendar

// Data classes moved to top level
data class CategoryAnalyticsData(
    val categoryName: String,
    val amount: Double,
    val percentage: Int
)

data class NavTabItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// Helper function moved to top level
fun getMonthName(month: Int): String {
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UserPreferences.init(applicationContext)

        // Request SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), 1)
        }

        setContent {
            BilanceTheme {
                BilanceApp()
            }
        }
    }
}

@Composable
fun BilanceApp() {
    var currentScreen by remember { mutableStateOf("splash") }
    var currentTab by remember { mutableStateOf("home") }
    val context = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(context) }
    val navController = rememberNavController()

    // Create a single shared ViewModel instance
    val factory = remember { TransactionViewModelFactory(database) }
    val sharedTransactionViewModel: TransactionViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        DatabaseUtils.createDemoUser(database)
        // Removed automatic creation of sample transactions
        // Users should start with a clean slate
    }

    val startDestination = remember {
        val lastEmail = UserPreferences.getLoggedInEmail()
        if (!lastEmail.isNullOrEmpty()) "home/$lastEmail" else "splash"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            AppSplashScreen(navController = navController) // Renamed to avoid conflict
        }
        composable("launch") {
            LaunchScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email ->
                    UserPreferences.setLoggedInEmail(email)
                    navController.navigate("home/$email") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { email ->
                    UserPreferences.setLoggedInEmail(email)
                    navController.navigate("setupBalance/$email") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("setupBalance/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SetupInitialBalanceScreen(
                onDone = {
                    navController.navigate("setupExpenseThreshold/$email") {
                        popUpTo("setupBalance/$email") { inclusive = true }
                    }
                }
            )
        }

        composable("setupExpenseThreshold/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            com.example.bilance.ui.SetupExpenseThresholdScreen(
                onDone = {
                    navController.navigate("home/$email") {
                        popUpTo("setupExpenseThreshold/$email") { inclusive = true }
                    }
                }
            )
        }
        composable("home/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainNavApp(navController = navController, userEmail = email, sharedTransactionViewModel = sharedTransactionViewModel)
        }
        composable("notifications") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController,
                sourceTab = "home",
                userEmail = currentUserEmail
            )
        }
        composable("notifications_from_home") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController,
                sourceTab = "home",
                userEmail = currentUserEmail
            )
        }
        composable("notifications_from_transactions") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController,
                sourceTab = "transactions",
                userEmail = currentUserEmail
            )
        }
        composable("notifications_from_categories") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController,
                sourceTab = "categories",
                userEmail = currentUserEmail
            )
        }
        composable("notifications_from_profile") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController,
                sourceTab = "profile",
                userEmail = currentUserEmail
            )
        }
        composable("transactionDetail/{smsId}") { backStackEntry ->
            val smsId = backStackEntry.arguments?.getString("smsId")?.toIntOrNull() ?: return@composable
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            TransactionDetailsScreen(smsId = smsId, viewModel = smsViewModel, navController = navController)
        }
        composable("categoryDetail/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: return@composable
            CategoryDetailScreen(categoryName = categoryName, viewModel = sharedTransactionViewModel, navController = navController)
        }
        composable("addExpense/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: return@composable
            AddExpenseScreen(categoryName = categoryName, viewModel = sharedTransactionViewModel, navController = navController)
        }
        composable("addCustomCategory") {
            AddCustomCategoryScreen(navController = navController)
        }
        composable("edit_profile") {
            val currentUserEmail = UserPreferences.getLoggedInEmail() ?: ""
            EditProfileScreen(navController = navController, userEmail = currentUserEmail)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("app_version") {
            AppVersionScreen(navController = navController)
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(navController = navController)
        }
        composable("about_bilance") {
            AboutBilanceScreen(navController = navController)
        }
    }
}

@Composable
fun HomeScreen(
    userEmail: String = "",
    sharedTransactionViewModel: TransactionViewModel
) {
    val localContext = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(localContext) }

    // Get real data from ViewModel
    val transactions by sharedTransactionViewModel.transactions.collectAsState()
    val totalIncome by sharedTransactionViewModel.totalIncome.collectAsState()
    val totalExpense by sharedTransactionViewModel.totalExpense.collectAsState()
    val totalBalance by sharedTransactionViewModel.totalBalance.collectAsState()

    // User data
    var userName by remember { mutableStateOf("User") }
    var userNotificationCount by remember { mutableStateOf(0) }

    // Load user data and SMS count
    LaunchedEffect(userEmail) {
        try {
            val user = if (userEmail.isNotEmpty()) {
                database.userDao().getUserByEmail(userEmail)
            } else {
                database.userDao().getUserByEmail("demo@bilance.com")
            }

            userName = user?.fullName?.split(" ")?.firstOrNull() ?: "User"

            // Get SMS notification count
            val smsViewModel = SMSViewModel(localContext.contentResolver)
            userNotificationCount = smsViewModel.notifications.size
        } catch (e: Exception) {
            userName = "User"
        }
    }

    // Get current time greeting
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    // Calculate category expenses
    val categoryExpenses = remember(transactions) {
        transactions
            .filter { it.amountType == "expense" }
            .groupBy { it.category }
            .mapValues { (_, list: List<com.example.bilance.data.Transaction>) ->
                list.sumOf { transaction -> transaction.amount }
            }
            .toList()
            .sortedByDescending { (_, amount: Double) -> amount }
            .take(4)
    }

    // Threshold Exceeded Notification Logic (additive)
    val thresholdExceeded by sharedTransactionViewModel.thresholdExceeded.collectAsState()
    var showThresholdDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val smsViewModel = remember { SMSViewModel(context.contentResolver) }

    // Register callback for threshold exceeded (additive)

    // Register callback for repeated transaction auto-categorization (additive)
    LaunchedEffect(Unit) {
        sharedTransactionViewModel.onAutoCategorized = { recipient, category ->
            smsViewModel.addAutoCategorizedNotification(recipient, category)
        }
    }
    LaunchedEffect(Unit) {
        sharedTransactionViewModel.onThresholdExceeded = {
            // Show in-app dialog
            showThresholdDialog = true
            // Trigger system notification (with permission check for Android 13+)
            val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
            val channelId = "expense_threshold_channel"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    channelId,
                    "Expense Threshold Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            // Use an existing icon, e.g., ic_trending_up
            val iconRes = R.drawable.ic_trending_up
            val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconRes)
                .setContentTitle("Monthly Expense Limit Exceeded")
                .setContentText("You have exceeded your monthly expense threshold value as per your plan.")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            // Android 13+ requires POST_NOTIFICATIONS permission
            if (android.os.Build.VERSION.SDK_INT < 33 ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(1001, builder.build())
            }
            // Add to notifications section (additive)
            smsViewModel.addTransaction(
                title = "Expense Limit Exceeded",
                amount = "-",
                category = "Alert",
                type = "reminder"
            )
        }
    }

    if (thresholdExceeded && showThresholdDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showThresholdDialog = false },
            title = { androidx.compose.material3.Text("Monthly Expense Limit Exceeded") },
            text = { androidx.compose.material3.Text("You have exceeded your monthly expense threshold value as per your plan. This may affect your savings.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showThresholdDialog = false }) {
                    androidx.compose.material3.Text("OK")
                }
            }
        )
    }

    // Modern gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Modern top greeting section with real user data
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = greeting,
                        color = TextOnPrimary.copy(alpha = 0.8f),
                        fontFamily = LeagueSpartan,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userName,
                        color = TextOnPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                // Modern profile avatar with notification badge
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        TextOnPrimary.copy(alpha = 0.2f),
                                        TextOnPrimary.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = TextOnPrimary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Profile",
                            tint = TextOnPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Notification badge with real count
                    if (userNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .offset(x = 8.dp, y = (-8).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRed)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userNotificationCount.toString(),
                                color = TextOnPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Modern balance card with real data
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Gradient overlay for modern look
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.05f),
                                        SecondaryPurple.copy(alpha = 0.03f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top section with real balance
                        Column {
                            Text(
                                text = "Total Balance",
                                color = TextSecondary,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "₹${String.format("%,.2f", totalBalance)}",
                                color = TextPrimary,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            )
                        }

                        // Bottom section with real income/expense
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Income card with real data
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentGreen.copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trending_up),
                                            contentDescription = "Income",
                                            tint = AccentGreen,
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            "Income",
                                            color = AccentGreen,
                                            fontFamily = Poppins,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        "₹${String.format("%,.0f", totalIncome)}",
                                        color = AccentGreen,
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Expense card with real data
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentRed.copy(alpha = 0.1f)
                                ),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trending_down),
                                            contentDescription = "Expense",
                                            tint = AccentRed,
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            "Expense",
                                            color = AccentRed,
                                            fontFamily = Poppins,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        "₹${String.format("%,.0f", totalExpense)}",
                                        color = AccentRed,
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Category expenses section
            if (categoryExpenses.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Category Spending",
                        color = TextOnPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryExpenses.forEach { (category, amount) ->
                            HomeCategoryItem(
                                categoryName = category,
                                amount = amount,
                                totalExpense = totalExpense
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCategoryItem(
    categoryName: String,
    amount: Double,
    totalExpense: Double
) {
    val percentage = if (totalExpense > 0) ((amount / totalExpense) * 100).toInt() else 0

    val (icon, iconColor) = when (categoryName.lowercase()) {
        "food", "food & dining", "restaurant" -> Pair(Icons.Default.Restaurant, Color(0xFF6CB5FD))
        "transport", "transportation", "travel" -> Pair(Icons.Default.DirectionsCar, Color(0xFF84CC16))
        "groceries", "shopping" -> Pair(Icons.Default.LocalGroceryStore, Color(0xFF7BB3FF))
        "rent", "housing", "home" -> Pair(Icons.Default.Home, Color(0xFFEF4444))
        "entertainment", "movies", "games" -> Pair(Icons.Default.Movie, Color(0xFFEC4899))
        "salary", "income", "earnings" -> Pair(Icons.Default.AccountBalance, Color(0xFF10B981))
        "transfer", "banking" -> Pair(Icons.Default.SwapHoriz, Color(0xFF06B6D4))
        "medicine", "medical", "health", "healthcare" -> Pair(Icons.Default.LocalHospital, Color(0xFFF59E0B))
        "savings", "investment" -> Pair(Icons.Default.Savings, Color(0xFFC8E6C9))
        "bills", "utilities" -> Pair(Icons.Default.AttachMoney, Color(0xFF8B5CF6))
        "education", "school", "tuition" -> Pair(Icons.Default.School, Color(0xFF3B82F6))
        "fitness", "gym", "sports" -> Pair(Icons.Default.FitnessCenter, Color(0xFF10B981))
        "travel", "vacation", "flight" -> Pair(Icons.Default.Flight, Color(0xFFF59E0B))
        "credit card", "card payment" -> Pair(Icons.Default.CreditCard, Color(0xFFEF4444))
        else -> Pair(Icons.Default.Category, Color(0xFF9CA3AF))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TextOnPrimary.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = categoryName,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryName,
                    color = TextOnPrimary,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$percentage% of expenses",
                    color = TextOnPrimary.copy(alpha = 0.7f),
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "₹${String.format("%,.0f", amount)}",
                color = TextOnPrimary,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AnalyticsScreen(sharedTransactionViewModel: TransactionViewModel) {
    // Get real data from ViewModel
    val transactions by sharedTransactionViewModel.transactions.collectAsState()
    val totalExpense by sharedTransactionViewModel.totalExpense.collectAsState()
    val totalIncome by sharedTransactionViewModel.totalIncome.collectAsState()
    val expensePercentage by sharedTransactionViewModel.expensePercentage.collectAsState()

    // State for selected month/year
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }

    // Get available months from transactions
    val availableMonths = remember(transactions) {
        transactions.groupBy { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"
        }.keys.map { key ->
            val parts = key.split("-")
            Pair(parts[0].toInt(), parts[1].toInt())
        }.sortedByDescending { "${it.first}-${String.format("%02d", it.second)}" }
    }

    // Calculate selected month expenses
    val selectedMonthExpenses = remember(transactions, selectedMonth, selectedYear) {
        transactions.filter { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            calendar.get(Calendar.MONTH) == selectedMonth &&
                    calendar.get(Calendar.YEAR) == selectedYear &&
                    transaction.amountType == "expense"
        }.sumOf { it.amount }
    }

    // Calculate selected month income
    val selectedMonthIncome = remember(transactions, selectedMonth, selectedYear) {
        transactions.filter { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            calendar.get(Calendar.MONTH) == selectedMonth &&
                    calendar.get(Calendar.YEAR) == selectedYear &&
                    transaction.amountType == "income"
        }.sumOf { it.amount }
    }

    // Calculate category breakdown for selected month
    val categoryBreakdown = remember(transactions, selectedMonth, selectedYear) {
        transactions
            .filter { transaction ->
                val calendar = Calendar.getInstance().apply { time = transaction.date }
                calendar.get(Calendar.MONTH) == selectedMonth &&
                        calendar.get(Calendar.YEAR) == selectedYear &&
                        transaction.amountType == "expense"
            }
            .groupBy { it.category }
            .mapValues { (_, transactionList) ->
                transactionList.sumOf { transaction -> transaction.amount }
            }
            .toList()
            .sortedByDescending { (_, amount) -> amount }
            .take(5)
            .map { (category, amount) ->
                val percentage =
                    if (selectedMonthExpenses > 0) ((amount / selectedMonthExpenses) * 100).toInt() else 0
                CategoryAnalyticsData(
                    categoryName = category,
                    amount = amount,
                    percentage = percentage
                )
            }
    }

    // Calculate budget progress based on initial balance and income
    val initialBalance = UserPreferences.getInitialBalance()
    val budgetBase = initialBalance + totalIncome
    val budgetProgress = if (budgetBase > 0) ((totalExpense / budgetBase) * 100).toInt() else 0

    // Month picker dialog
    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = { Text("Select Month") },
            text = {
                LazyColumn {
                    items(availableMonths.size) { index ->
                        val (year, month) = availableMonths[index]
                        TextButton(
                            onClick = {
                                selectedMonth = month
                                selectedYear = year
                                showMonthPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${getMonthName(month)} $year",
                                color = if (month == selectedMonth && year == selectedYear)
                                    PrimaryBlue else TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("Close")
                }
            }
        )
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
                Spacer(modifier = Modifier.size(40.dp)) // Balance the layout

                Text(
                    text = "Analytics",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 22.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Track your spending patterns",
                    color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Month selector
                if (availableMonths.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMonthPicker = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${getMonthName(selectedMonth)} $selectedYear",
                                color = TextPrimary,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Month",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real spending overview card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(
                                            SecondaryPurple.copy(alpha = 0.1f),
                                            PrimaryBlue.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${getMonthName(selectedMonth)} $selectedYear",
                                        color = TextSecondary,
                                        fontFamily = Poppins,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = "₹${String.format("%,.2f", selectedMonthExpenses)}",
                                        color = TextPrimary,
                                        fontFamily = Poppins,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(
                                            if (budgetProgress > 80) AccentRed.copy(alpha = 0.15f)
                                            else AccentGreen.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (budgetProgress > 80) R.drawable.ic_trending_down
                                            else R.drawable.ic_trending_up
                                        ),
                                        contentDescription = "Spending trend",
                                        tint = if (budgetProgress > 80) AccentRed else AccentGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            // Real progress indicator
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Budget Progress",
                                        color = TextSecondary,
                                        fontFamily = Poppins,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = "${budgetProgress}%",
                                        color = if (budgetProgress > 80) AccentRed else AccentGreen,
                                        fontFamily = Poppins,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BorderColor)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((budgetProgress / 100f).coerceAtMost(1f))
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (budgetProgress > 80) AccentRed else AccentGreen)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pie Chart for Category Breakdown
                if (categoryBreakdown.isNotEmpty()) {
                    Text(
                        text = "Expense Distribution",
                        color = TextPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        ExpensePieChart(
                            categoryData = categoryBreakdown,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Category breakdown with real data
                if (categoryBreakdown.isNotEmpty()) {
                    Text(
                        text = "Top Categories",
                        color = TextPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real category items
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categoryBreakdown.forEach { categoryData ->
                            val (icon, iconColor) = when (categoryData.categoryName.lowercase()) {
                                "food", "food & dining", "restaurant" -> Pair(Icons.Default.Restaurant, Color(0xFF6CB5FD))
                                "transport", "transportation", "travel" -> Pair(Icons.Default.DirectionsCar, Color(0xFF84CC16))
                                "groceries", "shopping" -> Pair(Icons.Default.LocalGroceryStore, Color(0xFF7BB3FF))
                                "rent", "housing", "home" -> Pair(Icons.Default.Home, Color(0xFFEF4444))
                                "entertainment", "movies", "games" -> Pair(Icons.Default.Movie, Color(0xFFEC4899))
                                "salary", "income", "earnings" -> Pair(Icons.Default.AccountBalance, Color(0xFF10B981))
                                "transfer", "banking" -> Pair(Icons.Default.SwapHoriz, Color(0xFF06B6D4))
                                "medicine", "medical", "health", "healthcare" -> Pair(Icons.Default.LocalHospital, Color(0xFFF59E0B))
                                "savings", "investment" -> Pair(Icons.Default.Savings, Color(0xFFC8E6C9))
                                "bills", "utilities" -> Pair(Icons.Default.AttachMoney, Color(0xFF8B5CF6))
                                "education", "school", "tuition" -> Pair(Icons.Default.School, Color(0xFF3B82F6))
                                "fitness", "gym", "sports" -> Pair(Icons.Default.FitnessCenter, Color(0xFF10B981))
                                "travel", "vacation", "flight" -> Pair(Icons.Default.Flight, Color(0xFFF59E0B))
                                "credit card", "card payment" -> Pair(Icons.Default.CreditCard, Color(0xFFEF4444))
                                else -> Pair(Icons.Default.Category, Color(0xFF9CA3AF))
                            }

                            CategoryAnalyticsItem(
                                categoryName = categoryData.categoryName,
                                amount = "₹${String.format("%,.0f", categoryData.amount)}",
                                percentage = categoryData.percentage,
                                icon = icon,
                                iconColor = iconColor
                            )
                        }
                    }
                } else {
                    // Show message when no expense data
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No expense data available",
                            color = TextSecondary,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Start adding transactions to see analytics",
                            color = TextMuted,
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Add bottom padding for proper scrolling
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CategoryAnalyticsItem(
    categoryName: String,
    amount: String,
    percentage: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = categoryName,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryName,
                    color = TextPrimary,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BorderColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(iconColor)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = amount,
                    color = TextPrimary,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Text(
                    text = "$percentage%",
                    color = TextSecondary,
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun NotificationsTabScreen(navController: NavController) {
    val context = LocalContext.current
    val smsViewModel = remember { SMSViewModel(context.contentResolver) }
    NotificationScreen(
        viewModel = smsViewModel,
        navController = navController
    )
}

@Composable
fun BottomNavBar(currentTab: String, onTabSelected: (String) -> Unit) {
    val activeColor = PrimaryBlue
    val inactiveColor = TextMuted
    val bgColor = SurfaceElevated
    val selectedLabelColor = PrimaryBlue

    val navItems = listOf(
        NavTabItem("home", "Home", Icons.Default.Home),
        NavTabItem("analytics", "Analytics", Icons.Default.BarChart),
        NavTabItem("transaction", "Transactions", Icons.Default.Receipt),
        NavTabItem("categories", "Categories", Icons.Default.Category),
        NavTabItem("profile", "Profile", Icons.Default.Person),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentTab == item.route
                ModernNavBarItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = isSelected,
                    onClick = { onTabSelected(item.route) },
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    selectedLabelColor = selectedLabelColor
                )
            }
        }
    }
}

@Composable
fun ModernNavBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    selectedLabelColor: Color
) {
    Column(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Modern pill-shaped background for selected item
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected)
                        activeColor.copy(alpha = 0.12f)
                    else
                        Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected) selectedLabelColor else inactiveColor,
            fontSize = 11.sp,
            fontFamily = Poppins,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun AppSplashScreen(navController: NavController) { // Renamed from SplashScreen
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("launch") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Modern gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
    ) {
        // Modern gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.1f),
                            SecondaryPurple.copy(alpha = 0.05f)
                        ),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern logo container with subtle glow effect
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                TextOnPrimary.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 100f
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = "Bilance Logo",
                    modifier = Modifier.size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Modern loading indicator
            CircularProgressIndicator(
                color = TextOnPrimary.copy(alpha = 0.8f),
                strokeWidth = 2.dp,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading...",
                color = TextOnPrimary.copy(alpha = 0.8f),
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LaunchScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Modern logo container with light background
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            radius = 100f
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Bilance Logo",
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Modern tagline with better typography
            Text(
                text = "One App. All Your Transactions.",
                color = TextPrimary,
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Zero Noise.",
                color = TextSecondary,
                fontFamily = LeagueSpartan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Modern buttons with consistent design
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = TextOnPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSignUpClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = PrimaryBlue
                ),
                border = BorderStroke(2.dp, PrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Forgot Password?",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MainNavApp(
    navController: NavController,
    userEmail: String = "",
    sharedTransactionViewModel: TransactionViewModel
) {
    var currentTab by remember { mutableStateOf("home") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    "home" -> HomeScreen(
                        userEmail = userEmail,
                        sharedTransactionViewModel = sharedTransactionViewModel
                    )
                    "analytics" -> AnalyticsScreen(sharedTransactionViewModel = sharedTransactionViewModel)
                    "transaction" -> TransactionScreen(
                        navController = navController,
                        viewModel = sharedTransactionViewModel
                    )
                    "categories" -> CategoriesScreen(
                        viewModel = sharedTransactionViewModel,
                        navController = navController
                    )
                    "profile" -> ProfileScreen(
                        navController = navController,
                        userEmail = userEmail
                    )
                }
            }
            BottomNavBar(currentTab = currentTab, onTabSelected = { currentTab = it })
        }
    }
}

@Composable
fun ExpensePieChart(
    categoryData: List<CategoryAnalyticsData>,
    modifier: Modifier = Modifier
) {
    // Calculate total for percentage calculation
    val total = categoryData.sumOf { it.amount }
    
    if (total <= 0 || categoryData.isEmpty()) {
        // Show empty state
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No expense data available",
                color = TextSecondary,
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
        return
    }
    
    // Vibrant and distinct color palette for pie chart
    val colorPalette = listOf(
        Color(0xFFFF6B6B), // Bright Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Sky Blue
        Color(0xFF96CEB4), // Mint Green
        Color(0xFFFECA57), // Yellow
        Color(0xFFFF9FF3), // Pink
        Color(0xFF54A0FF), // Blue
        Color(0xFF5F27CD), // Purple
        Color(0xFFFF9F43), // Orange
        Color(0xFF00D2D3), // Cyan
        Color(0xFFFF6348), // Coral
        Color(0xFF2ED573), // Green
        Color(0xFFFF4757), // Red-Pink
        Color(0xFF3742FA), // Indigo
        Color(0xFFFFDA79)  // Light Orange
    )
    
    // Assign colors based on index to ensure each category gets a unique color
    val colors = categoryData.mapIndexed { index, _ ->
        colorPalette[index % colorPalette.size]
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pie Chart
        Box(
            modifier = Modifier
                .size(180.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val radius = size.minDimension / 2
                val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                var startAngle = -90f // Start from top

                categoryData.forEachIndexed { index, data ->
                    val sweepAngle = (data.amount / total * 360).toFloat()
                    
                    // Only draw if the angle is meaningful
                    if (sweepAngle > 1f) {
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                        )
                    }
                    startAngle += sweepAngle
                }
                
                // Draw center circle for donut effect
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White,
                    radius = radius * 0.4f,
                    center = center
                )
            }
            
            // Center text showing total
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = Poppins
                )
                Text(
                    text = "₹${String.format("%,.0f", total)}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }
        }

        // Legend
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categoryData.take(5).forEachIndexed { index, data ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = colors[index],
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = data.categoryName,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            maxLines = 1
                        )
                        Text(
                            text = "${data.percentage}%",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}