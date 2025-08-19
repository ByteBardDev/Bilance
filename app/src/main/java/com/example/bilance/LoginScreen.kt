package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
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
    onLoginSuccess: (String) -> Unit = {},
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
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        com.example.bilance.ui.theme.GradientStart,
                        com.example.bilance.ui.theme.GradientEnd
                    )
                )
            )
    ) {
        // Subtle overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            com.example.bilance.ui.theme.PrimaryBlue.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        // Welcome text - Centered
        Text(
            text = "Welcome Back",
            color = com.example.bilance.ui.theme.TextOnPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = com.example.bilance.ui.theme.Poppins,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 100.dp)
        )
        
        Text(
            text = "Sign in to continue your financial journey",
            color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontFamily = com.example.bilance.ui.theme.Poppins,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 150.dp)
                .padding(horizontal = 32.dp)
        )

        // Modern form card
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
                .offset(y = 220.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = com.example.bilance.ui.theme.SurfaceElevated
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 20.dp
            )
        ) {
            // Subtle inner gradient for modern look
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                com.example.bilance.ui.theme.SurfaceElevated,
                                com.example.bilance.ui.theme.BackgroundPrimary.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }

        // Form content inside the card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 260.dp)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email input field with modern styling
            Text(
                text = "Email Address",
                color = com.example.bilance.ui.theme.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.BackgroundPrimary
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(0.dp)
            ) {
                BasicTextField(
                    value = email,
                    onValueChange = { email = it },
                    textStyle = TextStyle(
                        color = com.example.bilance.ui.theme.TextPrimary,
                        fontSize = 16.sp,
                        fontFamily = com.example.bilance.ui.theme.Poppins
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            if (email.isEmpty()) {
                                Text(
                                    text = "Enter your email",
                                    color = com.example.bilance.ui.theme.TextMuted,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.bilance.ui.theme.Poppins
                                )
                            }
                            innerTextField()
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password input field with modern styling
            Text(
                text = "Password",
                color = com.example.bilance.ui.theme.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.BackgroundPrimary
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(0.dp)
            ) {
                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    textStyle = TextStyle(
                        color = com.example.bilance.ui.theme.TextPrimary,
                        fontSize = 16.sp,
                        fontFamily = com.example.bilance.ui.theme.Poppins
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            if (password.isEmpty()) {
                                Text(
                                    text = "Enter your password",
                                    color = com.example.bilance.ui.theme.TextMuted,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.bilance.ui.theme.Poppins
                                )
                            }
                            innerTextField()
                            
                            // Eye icon positioned at the end
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Text(
                                    text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                                    color = com.example.bilance.ui.theme.TextMuted,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message display
            if (loginError.isNotEmpty()) {
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = loginError,
                        color = com.example.bilance.ui.theme.AccentRed,
                        fontSize = 14.sp,
                        fontFamily = com.example.bilance.ui.theme.Poppins,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Modern Login Button
            androidx.compose.material3.Button(
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
                                onLoginSuccess(user.email)
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
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = com.example.bilance.ui.theme.PrimaryBlue,
                    contentColor = com.example.bilance.ui.theme.TextOnPrimary,
                    disabledContainerColor = com.example.bilance.ui.theme.TextMuted
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = com.example.bilance.ui.theme.TextOnPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = com.example.bilance.ui.theme.Poppins
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign up link text
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = com.example.bilance.ui.theme.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = com.example.bilance.ui.theme.Poppins
                )
                Text(
                    text = "Sign Up",
                    color = com.example.bilance.ui.theme.PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = com.example.bilance.ui.theme.Poppins,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BilanceTheme {
        LoginScreen()
    }
} 