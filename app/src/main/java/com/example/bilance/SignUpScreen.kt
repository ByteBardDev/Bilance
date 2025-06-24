package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
    onSignUpSuccess: () -> Unit = {},
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


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Original dimensions and position
    val originalWidth = 430.dp
    val originalHeight = 745.dp
    val originalOffsetY = 187.dp
    val cornerRadius = 40.dp

    // Calculate the scale factor based on screen width
    // This makes the panel always take the full width
    val scaleFactor = screenWidth / originalWidth
    val newHeight = originalHeight * scaleFactor
    val newOffsetY = originalOffsetY * scaleFactor
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

        // Create Account title - Figma: x=95, y=100, width=240, height=22
        Text(
            text = "Create Account",
            color = LaunchTextLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(240.dp)
                .offset(x = 95.dp, y = 100.dp)
        )

        // Base shape (white form area) - Figma: x=0, y=187, width=430, height=745
        Box(
            modifier = Modifier
                .fillMaxWidth() // Occupy full width
                .height(newHeight)
                .offset(y = newOffsetY) // Apply scaled offset
                .clip(RoundedCornerShape(topStart = cornerRadius * scaleFactor, topEnd = cornerRadius * scaleFactor))
                .background(LaunchBackgroundLight)
        )

        // Full name label - Figma: x=55, y=214, width=76, height=23
        Text(
            text = "Full name",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(76.dp)
                .offset(x = 55.dp, y = 214.dp)
        )

        // Full name input field - Figma: x=37, y=240, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 240.dp)
                .background(
                    Color(0x4D6DB6FE), // 30% opacity blue background
                    RoundedCornerShape(18.dp)
                )
        ) {
            BasicTextField(
                value = fullName,
                onValueChange = { fullName = it },
                textStyle = TextStyle(
                    color = LaunchLinkText,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 35.dp, y = 12.dp)
                    ) {
                        if (fullName.isEmpty()) {
                            Text(
                                text = "example@example.com",
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

        // Email label - Figma: x=55, y=297, width=42, height=23
        Text(
            text = "Email",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(42.dp)
                .offset(x = 55.dp, y = 297.dp)
        )

        // Email input field - Figma: x=37, y=323, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 323.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background
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
                            .offset(x = 35.dp, y = 12.dp)
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

        // Mobile Number label - Figma: x=55, y=380, width=116, height=23
        Text(
            text = "Mobile Number",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(116.dp)
                .offset(x = 55.dp, y = 380.dp)
        )

        // Mobile Number input field - Figma: x=37, y=406, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 406.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background
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
                            .offset(x = 36.dp, y = 12.dp)
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

        // Date of birth label - Figma: x=55, y=464, width=95, height=23
        Text(
            text = "Date of birth",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(95.dp)
                .offset(x = 55.dp, y = 464.dp)
        )

        // Date of birth input field - Figma: x=37, y=490, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 490.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background
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
                            .offset(x = 36.dp, y = 12.dp)
                    ) {
                        if (dateOfBirth.isEmpty()) {
                            Text(
                                text = "DD / MM /YYY",
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

        // Password label - Figma: x=55, y=547, width=73, height=23
        Text(
            text = "Password",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(73.dp)
                .offset(x = 55.dp, y = 547.dp)
        )

        // Password input field - Figma: x=37, y=573, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 573.dp)
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
                            .offset(x = 36.dp, y = 13.dp)
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
            
            // Eye icon - Figma: x=354, y=590 (relative to password field: x=317, y=17)
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

        // Confirm Password label - Figma: x=55, y=630, width=139, height=23
        Text(
            text = "Confirm Password",
            color = LaunchTextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(139.dp)
                .offset(x = 55.dp, y = 630.dp)
        )

        // Confirm Password input field - Figma: x=37, y=656, width=357, height=41
        Box(
            modifier = Modifier
                .width(357.dp)
                .height(41.dp)
                .offset(x = 37.dp, y = 656.dp)
                .background(
                    Color(0xFFC9E9F6), // Light blue background
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
                            .offset(x = 36.dp, y = 13.dp)
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
            
            // Eye icon - Figma: x=352, y=672 (relative to confirm password field: x=315, y=16)
            IconButton(
                onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                modifier = Modifier
                    .offset(x = 315.dp, y = 8.dp)
            ) {
                Text(
                    text = if (confirmPasswordVisible) "👁️" else "👁️‍🗨️",
                    color = LaunchLinkText,
                    fontSize = 16.sp
                )
            }
        }

        // Terms and Privacy Policy text - Figma: x=79, y=725, width=273, height=28
        Text(
            text = "By continuing, you agree to Terms of Use and Privacy Policy.",
            color = LaunchTextDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(273.dp)
                .offset(x = 79.dp, y = 725.dp)
        )

        // Error message display
        if (signUpError.isNotEmpty()) {
            Text(
                text = signUpError,
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(273.dp)
                    .offset(x = 79.dp, y = 750.dp)
            )
        }

        // Create Account Button - Figma: x=116, y=766, width=207, height=45
        Button(
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
                                    onSignUpSuccess()
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
            colors = ButtonDefaults.buttonColors(
                containerColor = LaunchButtonBlue,
                contentColor = LaunchTextLight
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .width(207.dp)
                .height(45.dp)
                .offset(x = 116.dp, y = 766.dp)
        ) {
            Text(
                text = if (isLoading) "Creating Account..." else "Create Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Login link text - Figma: x=79, y=822, width=273, height=28
        Text(
            text = "Already have an account? Log In",
            color = LaunchTextDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(273.dp)
                .offset(x = 79.dp, y = 822.dp)
                .clickable { onLoginClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    BilanceTheme {
        SignUpScreen()
    }
} 