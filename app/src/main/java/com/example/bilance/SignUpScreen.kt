package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.User
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.LaunchBackgroundLight
import com.example.bilance.ui.theme.LaunchButtonBlue
import com.example.bilance.ui.theme.LaunchLinkText
import com.example.bilance.ui.theme.LaunchTextDark
import com.example.bilance.ui.theme.LaunchTextLight
import com.example.bilance.ui.theme.SplashBackgroundBlue
import com.example.bilance.ui.theme.SplashOverlayBlack
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var signUpError by remember { mutableStateOf("") }
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
        // Modern overlay for depth
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

        // Create Account title - Modern styling
        Text(
            text = "Create Account",
            color = com.example.bilance.ui.theme.TextOnPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = com.example.bilance.ui.theme.Poppins,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 80.dp)
        )
        
        Text(
            text = "Join thousands managing their finances",
            color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontFamily = com.example.bilance.ui.theme.Poppins,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 115.dp)
                .padding(horizontal = 32.dp)
        )

        // Modern card container
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(680.dp)
                .offset(y = 160.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = com.example.bilance.ui.theme.SurfaceElevated
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                com.example.bilance.ui.theme.SurfaceElevated,
                                com.example.bilance.ui.theme.BackgroundPrimary.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }

        // Scrollable content area
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 190.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Full name section - Modern styling
                Text(
                    text = "Full name",
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
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp)
                ) {
                    BasicTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
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
                                if (fullName.isEmpty()) {
                                    Text(
                                        text = "Enter your full name",
                                        color = com.example.bilance.ui.theme.TextMuted,
                                        fontSize = 16.sp,
                                        fontFamily = com.example.bilance.ui.theme.Poppins
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email section
                Text(
                    text = "Email",
                    color = LaunchTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

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
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
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

                // Mobile Number section
                Text(
                    text = "Mobile Number",
                    color = LaunchTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

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
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        textStyle = TextStyle(
                            color = LaunchLinkText,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (mobileNumber.isEmpty()) {
                                    Text(
                                        text = "+ 123 456 789",
                                        color = LaunchLinkText,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date of birth section
                Text(
                    text = "Date of birth",
                    color = LaunchTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

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
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        textStyle = TextStyle(
                            color = LaunchLinkText,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (dateOfBirth.isEmpty()) {
                                    Text(
                                        text = "DD / MM / YYYY",
                                        color = LaunchLinkText,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Password section
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

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Password section
                Text(
                    text = "Confirm Password",
                    color = LaunchTextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

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
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        textStyle = TextStyle(
                            color = LaunchLinkText,
                            fontSize = 14.sp
                        ),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 13.dp)
                            ) {
                                if (confirmPassword.isEmpty()) {
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

                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = if (confirmPasswordVisible) "👁️" else "👁️‍🗨️",
                            color = LaunchLinkText,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and Privacy Policy text
                Text(
                    text = "By continuing, you agree to Terms of Use and Privacy Policy.",
                    color = LaunchTextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error message display
                if (signUpError.isNotEmpty()) {
                    Text(
                        text = signUpError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Modern Create Account Button
                androidx.compose.material3.Button(
                    onClick = { 
                        when {
                            fullName.isBlank() -> signUpError = "Please enter your full name"
                            email.isBlank() -> signUpError = "Please enter your email"
                            mobileNumber.isBlank() -> signUpError = "Please enter your mobile number"
                            dateOfBirth.isBlank() -> signUpError = "Please enter your date of birth"
                            password.isBlank() -> signUpError = "Please enter a password"
                            confirmPassword.isBlank() -> signUpError = "Please confirm your password"
                            password != confirmPassword -> signUpError = "Passwords do not match"
                            password.length < 6 -> signUpError = "Password must be at least 6 characters"
                            else -> {
                                isLoading = true
                                signUpError = ""
                                
                                coroutineScope.launch {
                                    try {
                                        val existingUser = database.userDao().getUserByEmail(email.trim())
                                        if (existingUser != null) {
                                            signUpError = "Email already exists"
                                        } else {
                                            val newUser = User(
                                                fullName = fullName.trim(),
                                                email = email.trim(),
                                                mobileNumber = mobileNumber.trim(),
                                                dateOfBirth = dateOfBirth.trim(),
                                                password = password
                                            )
                                            database.userDao().registerUser(newUser)
                                            onSignUpSuccess(email.trim())
                                        }
                                    } catch (e: Exception) {
                                        signUpError = "Registration failed: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
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
                        pressedElevation = 8.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = com.example.bilance.ui.theme.TextOnPrimary,
                            strokeWidth = 2.dp,
                            modifier = androidx.compose.ui.Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = com.example.bilance.ui.theme.Poppins
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login link text
                Text(
                    text = "Already have an account? Log In",
                    color = LaunchTextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    BilanceTheme {
        SignUpScreen()
    }
} 