package com.example.bilance

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.model.TransactionSMS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    smsId: Int,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val sms = viewModel.notifications.find { it.id == smsId } ?: return
    val lowerMsg = sms.message.lowercase()
    val transactionType = when {
        lowerMsg.contains("debited") || lowerMsg.contains("debit") -> "Expense"
        lowerMsg.contains("credited") || lowerMsg.contains("credit") -> "Income"
        else -> "Expense"
    }
    val categoryOptions = if (transactionType == "Income") {
        listOf("Uncategorized", "Salary", "Business Income", "TA/DA", "Interest", "Gift", "Other Income")
    } else {
        listOf("Uncategorized", "Food", "Travel", "Shopping", "Groceries", "Bills", "Others")
    }
    var selectedCategory by remember { mutableStateOf(if (sms.category in categoryOptions) sms.category else "Uncategorized") }
    var amount by remember { mutableStateOf(sms.amount) }
    val isIncome = transactionType == "Income"

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Transaction Details") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text("Transaction Details", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    Text("From: ${sms.title}", style = MaterialTheme.typography.bodyLarge)
                    Text("Message: ${sms.message}", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            // Only allow changes if not income type
                            if (!isIncome) {
                                // Preserve ₹ symbol if it exists
                                val currencySymbol = if (amount.startsWith("₹")) "₹" else ""
                                amount = currencySymbol + newValue.filter { it.isDigit() || it == '.' }
                            }
                        },
                        label = { Text("Amount") },
                        singleLine = true,
                        readOnly = isIncome,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = transactionType,
                        onValueChange = {},
                        label = { Text("Type") },
                        singleLine = true,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Select Category:", style = MaterialTheme.typography.labelLarge)
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            modifier = Modifier.menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoryOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedCategory = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateSMS(sms, selectedCategory, amount)
                                Toast.makeText(context, "Transaction saved", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Okay")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.removeSMS(sms)
                                Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}
