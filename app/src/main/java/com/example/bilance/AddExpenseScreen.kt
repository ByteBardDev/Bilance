// File: AddExpenseScreen.kt
package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilance.viewmodel.TransactionViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    categoryName: String,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("expense") }
    var expanded by remember { mutableStateOf(false) }

    val isFormValid = title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0

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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Add Transaction",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 20.sp,
                    fontFamily = com.example.bilance.ui.theme.Poppins,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = {
                        if (isFormValid) {
                            println("DEBUG: AddExpenseScreen - Adding transaction using ViewModel: ${viewModel.hashCode()}")
                            viewModel.addTransaction(
                                title = title,
                                amount = amount.toDouble(),
                                amountType = transactionType,
                                category = categoryName,
                                iconName = "ShoppingCart"
                            )
                            navController.popBackStack()
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFormValid) com.example.bilance.ui.theme.AccentGreen.copy(alpha = 0.15f)
                            else com.example.bilance.ui.theme.TextMuted.copy(alpha = 0.1f)
                        )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Save Transaction",
                        tint = if (isFormValid) com.example.bilance.ui.theme.AccentGreen else com.example.bilance.ui.theme.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Modern card container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.BackgroundPrimary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                // Form Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Category Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = com.example.bilance.ui.theme.SurfaceElevated
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Category",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = categoryName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.PrimaryBlue
                            )
                        }
                    }

                    // Transaction Type Selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = com.example.bilance.ui.theme.SurfaceElevated
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Transaction Type",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = if (transactionType == "expense") "Expense" else "Income",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Dropdown",
                                            tint = com.example.bilance.ui.theme.PrimaryBlue
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = com.example.bilance.ui.theme.PrimaryBlue,
                                        unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Expense") },
                                        onClick = {
                                            transactionType = "expense"
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Income") },
                                        onClick = {
                                            transactionType = "income"
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Title Input
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = com.example.bilance.ui.theme.SurfaceElevated
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Title",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Enter transaction title") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = com.example.bilance.ui.theme.PrimaryBlue,
                                    unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor
                                )
                            )
                        }
                    }

                    // Amount Input
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = com.example.bilance.ui.theme.SurfaceElevated
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Amount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    // Allow only numeric input with decimal
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        amount = newValue
                                    }
                                },
                                placeholder = { Text("0.00") },
                                leadingIcon = {
                                    Text(
                                        text = "₹",
                                        fontSize = 18.sp,
                                        color = com.example.bilance.ui.theme.PrimaryBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = com.example.bilance.ui.theme.Poppins
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = com.example.bilance.ui.theme.PrimaryBlue,
                                    unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor
                                )
                            )
                        }
                    }

                    // Validation Message
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        if (amount.toDoubleOrNull() == null || amount.toDoubleOrNull()!! <= 0) {
                            Text(
                                text = "Please enter a valid amount greater than 0",
                                fontSize = 12.sp,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.AccentRed,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    // Preview Card
                    if (isFormValid) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (transactionType == "expense")
                                    com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.1f)
                                else
                                    com.example.bilance.ui.theme.AccentGreen.copy(alpha = 0.1f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Preview",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = com.example.bilance.ui.theme.Poppins,
                                    color = com.example.bilance.ui.theme.TextSecondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = com.example.bilance.ui.theme.Poppins,
                                            color = com.example.bilance.ui.theme.TextPrimary
                                        )
                                        Text(
                                            text = categoryName,
                                            fontSize = 12.sp,
                                            fontFamily = com.example.bilance.ui.theme.Poppins,
                                            color = com.example.bilance.ui.theme.TextSecondary
                                        )
                                    }

                                    Text(
                                        text = "₹${String.format(Locale.US, "%.2f", amount.toDouble())}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = com.example.bilance.ui.theme.Poppins,
                                        color = if (transactionType == "expense")
                                            com.example.bilance.ui.theme.AccentRed
                                        else
                                            com.example.bilance.ui.theme.AccentGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}