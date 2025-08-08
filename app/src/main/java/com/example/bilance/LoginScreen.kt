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

        // Welcome text - Centered
        Text(
            text = "Welcome",
            color = LaunchTextLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 100.dp)
        )

        // Base shape (white form area) - Full width
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(745.dp)
                .offset(y = 188.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(LaunchBackgroundLight)
        )

        // Center all content within the form area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 250.dp)
                .padding(horizontal = 37.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Username or email label
            Text(
                text = "Username or email",
                color = LaunchTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            // Email input field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(41.dp)
                    .background(
                        Color(0xFFC9E9F6),
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
                                .padding(horizontal = 16.dp, vertical = 11.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Password label
            Text(
                text = "Password",
                color = LaunchTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            // Password input field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(41.dp)
                    .background(
                        Color(0xFFC9E9F6),
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
                                .padding(horizontal = 16.dp, vertical = 13.dp)
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

                // Eye icon
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                        color = LaunchLinkText,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security message
            Text(
                text = "Store your password securely to avoid losing access",
                color = LaunchTextBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            if (loginError.isNotEmpty()) {
                Text(
                    text = loginError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Login Button
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
                    text = if (isLoading) "Logging in..." else "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
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
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up link text
            Text(
                text = "Don't have an account? Sign Up",
                color = LaunchTextDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onSignUpClick() }
            )
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