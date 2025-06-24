package com.example.bilance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.DatabaseUtils
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.LaunchBackgroundLight
import com.example.bilance.ui.theme.LaunchButtonBlue
import com.example.bilance.ui.theme.LaunchButtonLight
import com.example.bilance.ui.theme.LaunchLinkText
import com.example.bilance.ui.theme.LaunchTextBlue
import com.example.bilance.ui.theme.LaunchTextDark
import com.example.bilance.ui.theme.LaunchTextLight
import com.example.bilance.ui.theme.SplashBackgroundBlue
import com.example.bilance.ui.theme.SplashOverlayBlack
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val context = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(context) }
    
    LaunchedEffect(Unit) {
        // Initialize demo user
        DatabaseUtils.createDemoUser(database)
        
        delay(3000) // Show splash for 3 seconds
        currentScreen = "launch"
    }
    
    when (currentScreen) {
        "splash" -> SplashScreen()
        "launch" -> LaunchScreen(
            onLoginClick = { currentScreen = "login" },
            onSignUpClick = { currentScreen = "signup" }
        )
        "login" -> LoginScreen(
            onLoginSuccess = { currentScreen = "home" }, // TODO: Navigate to home
            onSignUpClick = { currentScreen = "signup" }
        )
        "signup" -> SignUpScreen(
            onSignUpSuccess = { currentScreen = "home" },
            onLoginClick = { currentScreen = "login" }
        )
        "home" -> {
            // TODO: Add HomeScreen
            Text("Welcome to Bilance!", modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackgroundBlue)
    ) {
        // Apply overlay effects as mentioned in Figma design
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SplashOverlayBlack)
            )
        }
        // Logo positioned based on Figma coordinates
        // Original: x=38, y=307, width=335.765625, height=174
        // Converting to dp approximations for responsive design
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
        // Logo positioned based on Figma coordinates
        // Original: x=38, y=307, width=335, height=174
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
        
        // Tagline text positioned based on Figma coordinates
        // Original: x=82, y=481, width=268, height=13
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
        
        // Primary Login Button positioned based on Figma coordinates
        // Original: x=112, y=530, width=207, height=45
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
        
        // Secondary Sign Up Button positioned based on Figma coordinates
        // Original: x=112, y=587, width=207, height=45
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
        
        // Forgot Password link positioned based on Figma coordinates
        // Original: x=152, y=644, width=127, height=13
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
                .clickable { /* TODO: Handle forgot password */ }
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
    BilanceTheme {
        SplashScreen()
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
fun GreetingPreview() {
    BilanceTheme {
        Greeting("Android")
    }
}

