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
fun SetupInitialBalanceScreen(onDone: () -> Unit) {
    var balanceText by remember { mutableStateOf("") }
    val isValid = balanceText.toDoubleOrNull() != null

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
        // Overlay for depth
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
            // Modern welcome text
            Text(
                text = "Set Your Initial Balance",
                fontSize = 28.sp,
                color = com.example.bilance.ui.theme.TextOnPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Enter your current account balance to get started with accurate tracking",
                fontSize = 16.sp,
                color = com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                fontFamily = com.example.bilance.ui.theme.Poppins,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Modern input card
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.SurfaceElevated
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
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
                        text = "Initial Balance",
                        fontSize = 16.sp,
                        color = com.example.bilance.ui.theme.TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = com.example.bilance.ui.theme.Poppins
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Balance input with modern styling
                    androidx.compose.material3.OutlinedTextField(
                        value = balanceText,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) balanceText = it },
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
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = com.example.bilance.ui.theme.PrimaryBlue,
                            unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor,
                            focusedContainerColor = com.example.bilance.ui.theme.BackgroundPrimary,
                            unfocusedContainerColor = com.example.bilance.ui.theme.BackgroundPrimary
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Modern continue button
                    androidx.compose.material3.Button(
                        onClick = {
                            val value = balanceText.toDoubleOrNull() ?: 0.0
                            UserPreferences.setInitialBalance(value)
                            onDone()
                        },
                        enabled = isValid,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = com.example.bilance.ui.theme.PrimaryBlue,
                            contentColor = com.example.bilance.ui.theme.TextOnPrimary,
                            disabledContainerColor = com.example.bilance.ui.theme.TextMuted
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
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





