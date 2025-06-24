package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
// Removed material icons imports to fix compilation issues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.LaunchBackgroundLight
import com.example.bilance.ui.theme.LaunchButtonBlue
import com.example.bilance.ui.theme.LaunchLinkText
import com.example.bilance.ui.theme.LaunchTextBlue
import com.example.bilance.ui.theme.LaunchTextDark
import com.example.bilance.ui.theme.LaunchTextLight
import com.example.bilance.ui.theme.SplashBackgroundBlue
import com.example.bilance.ui.theme.SplashOverlayBlack
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

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

        // Welcome text - Figma: x=143, y=100, width=145, height=22
        Text(
            text = "Welcome",
            color = LaunchTextLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(145.dp)
                .offset(x = 143.dp, y = 100.dp)
        )

        // Base shape (white form area) - Figma: x=0, y=188, width=430, height=745
        Box(
            modifier = Modifier
                .width(430.dp)
                .height(745.dp)
                .offset(x = 0.dp, y = 188.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(LaunchBackgroundLight)
        )

        // Username or email label - Figma: x=53, y=277, width=145, height=23
        Text(
            text = "Username or email",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(145.dp)
                .offset(x = 53.dp, y = 277.dp)
        )

        // Email input field background - Figma: x=37, y=308, width=356, height=41
        Box(
            modifier = Modifier
                .width(356.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 308.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background RGB(0.788, 0.914, 0.965)
                    RoundedCornerShape(18.dp)
                )
                .border(
                    1.dp,
                    Color.Black,
                    RoundedCornerShape(18.dp)
                )
        ) {
            BasicTextField(
                value = email,
                onValueChange = { email = it },
                textStyle = TextStyle(
                    color = LaunchLinkText,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 34.dp, y = 11.dp)
                    ) {
                        if (email.isEmpty()) {
                            Text(
                                text = "example@example.com",
                                color = LaunchLinkText,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Password label - Figma: x=53, y=372, width=73, height=23
        Text(
            text = "Password",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(73.dp)
                .offset(x = 53.dp, y = 372.dp)
        )

        // Password input field background - Figma: x=37, y=398, width=356, height=41
        Box(
            modifier = Modifier
                .width(356.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 398.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background
                    RoundedCornerShape(18.dp)
                )
        ) {
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                textStyle = TextStyle(
                    color = LaunchLinkText,
                    fontSize = 14.sp
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 34.dp, y = 13.dp)
                    ) {
                        if (password.isEmpty()) {
                            Text(
                                text = "●●●●●●●●",
                                color = LaunchLinkText,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxSize()
            )
            
            // Eye icon - Figma: x=354, y=416 (relative to password field: x=317, y=18)
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .offset(x = 317.dp, y = 8.dp)
            ) {
                Text(
                    text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                    color = LaunchLinkText,
                    fontSize = 16.sp
                )
            }
        }

        // Security message - Figma: x=74, y=470, width=273, height=28
        Text(
            text = "Store your password securely to avoid losing access",
            color = LaunchTextBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(290.dp)
                
                .offset(x = 74.dp, y = 470.dp)
        )

        // Error message display
        if (loginError.isNotEmpty()) {
            Text(
                text = loginError,
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(273.dp)
                    .offset(x = 74.dp, y = 520.dp)
            )
        }

        // Login Button - Figma: x=107, y=558, width=207, height=45
        Button(
            onClick = { 
                if (email.isBlank() || password.isBlank()) {
                    loginError = "Please fill in all fields"
                    return@Button
                }
                
                isLoading = true
                loginError = ""
                
                coroutineScope.launch {
                    try {
                        val user = database.userDao().login(email.trim(), password)
                        if (user != null) {
                            onLoginSuccess()
                        } else {
                            loginError = "Invalid email or password"
                        }
                    } catch (e: Exception) {
                        loginError = "Login failed: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = LaunchButtonBlue,
                contentColor = LaunchTextLight
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .width(207.dp)
                .height(45.dp)
                .offset(x = 107.dp, y = 558.dp)
        ) {
            Text(
                text = if (isLoading) "Logging in..." else "Log In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Sign Up Button - Figma: x=112, y=621, width=207, height=45
        OutlinedButton(
            onClick = onSignUpClick,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFFC9E9F6),
                contentColor = LaunchTextBlue
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .width(207.dp)
                .height(45.dp)
                .offset(x = 107.dp, y = 621.dp)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Sign up link text - Figma: x=78, y=674, width=273, height=28
        Text(
            text = "Don't have an account? Sign Up",
            color = LaunchTextDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(273.dp)
                .offset(x = 78.dp, y = 674.dp)
                .clickable { onSignUpClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BilanceTheme {
        LoginScreen()
    }
} 