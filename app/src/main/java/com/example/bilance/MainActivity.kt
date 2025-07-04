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

    LaunchedEffect(Unit) {
        DatabaseUtils.createDemoUser(database)
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
                onLoginSuccess = { navController.navigate("notifications") },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate("notifications") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("notifications") {
            val factory = remember { TransactionViewModelFactory(context.contentResolver) }
            val viewModel: TransactionViewModel = viewModel(factory = factory)
            NotificationScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("transactionDetail/{smsId}") { backStackEntry ->
            val smsId = backStackEntry.arguments?.getString("smsId")?.toIntOrNull() ?: return@composable
            val factory = remember { TransactionViewModelFactory(context.contentResolver) }
            val viewModel: TransactionViewModel = viewModel(factory = factory)
            TransactionDetailsScreen(smsId = smsId, viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
fun MainNavApp(currentTab: String, onTabSelected: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    "home" -> HomeScreen()
                    "analytics" -> AnalyticsScreen()
                    "transaction" -> TransactionScreen()
                    "categories" -> CategoriesScreen()
                    "profile" -> ProfileScreen()
                }
            }
            BottomNavBar(currentTab = currentTab, onTabSelected = onTabSelected)
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Bilance!", modifier = Modifier.padding(bottom = 16.dp))
        // Add more home content here
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
fun CategoriesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Categories Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF283A5F)
        )
        Text(
            "Manage your expense categories here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Profile Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF283A5F)
        )
        Text(
            "Manage your profile settings here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
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
            
            // Transaction Icon (arrows)
            NavBarItem(
                icon = Icons.Default.SwapHoriz,
                isSelected = currentTab == "transaction",
                onClick = { onTabSelected("transaction") }
            )
            
            // Layers/Categories Icon
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
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SplashOverlayBlack)
            )
        }
        Box(
            modifier = Modifier
                .width(336.dp)
                .height(174.dp)
                .offset(x = 38.dp, y = 307.dp),
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
        Box(
            modifier = Modifier
                .width(335.dp)
                .height(174.dp)
                .offset(x = 38.dp, y = 307.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Bilance Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = "One App. All Your Transactions. Zero Noise.",
            color = LaunchTextDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(268.dp)
                .offset(x = 82.dp, y = 481.dp)
        )

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
                .offset(x = 112.dp, y = 530.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

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
                .offset(x = 112.dp, y = 587.dp)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Forgot Password?",
            color = LaunchLinkText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(127.dp)
                .offset(x = 152.dp, y = 644.dp)
                .clickable { }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
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
fun TransactionPreview() {
    BilanceTheme {
        TransactionScreen()
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
fun GreetingPreview() {
    var currentTab by remember { mutableStateOf("home") }
    
    BilanceTheme {
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
                        Greeting("Android")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Current tab: $currentTab",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                
                // Bottom Navigation Bar
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    }
}
