// File: MainActivity.kt
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.DatabaseUtils
import com.example.bilance.ui.theme.*
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.viewmodel.TransactionViewModelFactory
import com.example.bilance.viewmodel.SMSViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
        DatabaseUtils.createSampleTransactions(database)
    }

    NavHost(navController = navController, startDestination = "splash") {
    composable("splash") {
        SplashScreen(navController = navController)
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
                navController.navigate("home/$email") {
                    popUpTo("signup") { inclusive = true }
                }
            },
            onLoginClick = { navController.navigate("login") }
        )
    }
    composable("home/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email") ?: ""
        MainNavApp(navController = navController, userEmail = email, sharedTransactionViewModel = sharedTransactionViewModel)
    }
        composable("notifications") {
            val smsViewModel = remember { SMSViewModel(context.contentResolver) }
            NotificationScreen(
                viewModel = smsViewModel,
                navController = navController
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
    }
}

@Composable
fun HomeScreen() {
    // This is now the greeting screen content
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome to Bilance!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF283A5F),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Your personal finance manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Analytics Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF283A5F)
        )
        Text(
            "View your spending analytics here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFC8D5E8)) // Light blue-gray background
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Icon
            NavBarItem(
                icon = Icons.Default.Home,
                isSelected = currentTab == "home",
                onClick = { onTabSelected("home") }
            )
            
            // Analytics Icon
            NavBarItem(
                icon = Icons.Default.Analytics,
                isSelected = currentTab == "analytics",
                onClick = { onTabSelected("analytics") }
            )
            
            // Transaction Icon (arrows) - Middle position
            NavBarItem(
                icon = Icons.Default.SwapHoriz,
                isSelected = currentTab == "transaction",
                onClick = { onTabSelected("transaction") }
            )
            
            // Categories Icon - 4th position
            NavBarItem(
                icon = Icons.Default.Layers,
                isSelected = currentTab == "categories",
                onClick = { onTabSelected("categories") }
            )
            
            // Profile Icon
            NavBarItem(
                icon = Icons.Default.Person,
                isSelected = currentTab == "profile",
                onClick = { onTabSelected("profile") }
            )
        }
    }
}

@Composable
fun NavBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) Color(0xFF283A5F) // Dark blue for selected
                else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else Color(0xFF283A5F),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("launch") {
            popUpTo("splash") { inclusive = true }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackgroundBlue)
    ) {
        // Apply overlay effects
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SplashOverlayBlack)
            )
        }
        
        // Center the logo
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo container
            Box(
                modifier = Modifier
                    .width(336.dp)
                    .height(174.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = "Bilance Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
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
            .background(LaunchBackgroundLight)
    ) {
        // Center all content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo section
            Box(
                modifier = Modifier
                    .width(335.dp)
                    .height(174.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Bilance Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline text
            Text(
                text = "One App. All Your Transactions. Zero Noise.",
                color = LaunchTextDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(268.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Log In Button
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LaunchButtonBlue,
                    contentColor = LaunchTextLight
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(207.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
            OutlinedButton(
                onClick = onSignUpClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = LaunchButtonLight,
                    contentColor = LaunchTextBlue
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(207.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Forgot Password link
            Text(
                text = "Forgot Password?",
                color = LaunchLinkText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { 
                    // Handle forgot password action
                }
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
fun MainNavApp(navController: NavController, userEmail: String = "", sharedTransactionViewModel: TransactionViewModel) {
    var currentTab by remember { mutableStateOf("home") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    "home" -> HomeScreen()
                    "analytics" -> AnalyticsScreen()
                    "transaction" -> TransactionScreen(navController = navController, viewModel = sharedTransactionViewModel)
                    "categories" -> CategoriesScreen(viewModel = sharedTransactionViewModel, navController = navController)
                    "profile" -> ProfileScreen(navController = navController, userEmail = userEmail)
                }
            }
            BottomNavBar(currentTab = currentTab, onTabSelected = { currentTab = it })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    BilanceTheme {
        SplashScreen(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun LaunchScreenPreview() {
    BilanceTheme {
        LaunchScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun BilanceAppPreview() {
    BilanceTheme {
        BilanceApp()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    BilanceTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    BilanceTheme {
        SignUpScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val factory = TransactionViewModelFactory(BilanceDatabase.getDatabase(context))
    val viewModel: TransactionViewModel = viewModel(factory = factory)
    BilanceTheme {
        MainNavApp(navController = navController, sharedTransactionViewModel = viewModel)
    }
}
