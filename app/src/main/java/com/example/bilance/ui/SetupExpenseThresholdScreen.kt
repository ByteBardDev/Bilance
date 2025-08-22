package com.example.bilance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilance.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupExpenseThresholdScreen(onDone: () -> Unit) {
    var thresholdText by remember { mutableStateOf("") }
    val isValid = thresholdText.toDoubleOrNull() != null

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Your Monthly Expense Limit",
                fontSize = 28.sp,
                color = com.example.bilance.ui.theme.TextOnPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your planned maximum spending for the month.",
                fontSize = 16.sp,
                color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.SurfaceElevated
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Monthly Limit",
                        fontSize = 16.sp,
                        color = com.example.bilance.ui.theme.TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = com.example.bilance.ui.theme.Poppins
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = thresholdText,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) thresholdText = it },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = {
                            Text(
                                "0.00",
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                fontSize = 24.sp
                            )
                        },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.example.bilance.ui.theme.PrimaryBlue,
                                fontFamily = com.example.bilance.ui.theme.Poppins
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = com.example.bilance.ui.theme.Poppins,
                            color = com.example.bilance.ui.theme.TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = com.example.bilance.ui.theme.PrimaryBlue,
                            unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor,
                            focusedContainerColor = com.example.bilance.ui.theme.BackgroundPrimary,
                            unfocusedContainerColor = com.example.bilance.ui.theme.BackgroundPrimary
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val value = thresholdText.toDoubleOrNull() ?: 0.0
                            UserPreferences.setMonthlyExpenseThreshold(value)
                            onDone()
                        },
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.bilance.ui.theme.PrimaryBlue,
                            contentColor = com.example.bilance.ui.theme.TextOnPrimary,
                            disabledContainerColor = com.example.bilance.ui.theme.TextMuted
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = com.example.bilance.ui.theme.Poppins
                        )
                    }
                }
            }
        }
    }
}
