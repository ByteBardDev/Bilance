package com.example.bilance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.viewmodel.SMSViewModel
import com.example.bilance.viewmodel.TransactionViewModel
import com.example.bilance.viewmodel.TransactionViewModelFactory
import com.example.bilance.model.TransactionSMS
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    smsId: Int,
    viewModel: SMSViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(BilanceDatabase.getDatabase(context))
    )

    // Find the SMS by ID, if not found show error and navigate back
    val sms = viewModel.notifications.find { it.id == smsId }
    if (sms == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Transaction not found", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    val lowerMsg = sms.message.lowercase()
    val transactionType = when {
        lowerMsg.contains("debited") || lowerMsg.contains("debit") -> "Expense"
        lowerMsg.contains("credited") || lowerMsg.contains("credit") -> "Income"
        else -> "Expense"
    }

    val categoryOptions = if (transactionType == "Income") {
        listOf(
            "Uncategorized", "Salary", "Business Income", "TA/DA", "Interest", "Gift", 
            "Investment", "Bonus", "Commission", "Rental Income", "Other Income"
        )
    } else {
        listOf(
            "Uncategorized", "Food", "Transport", "Groceries", "Rent", "Entertainment", 
            "Bills", "Shopping", "Health", "Education", "Fitness", "Travel", 
            "Medicine", "Credit Card", "Insurance", "Utilities", "Others"
        )
    }

    var selectedCategory by remember { mutableStateOf(if (sms.category in categoryOptions) sms.category else "Uncategorized") }
    var amount by remember { mutableStateOf(sms.amount) }
    var recipient by remember { mutableStateOf(sms.recipient ?: "") }
    val isIncome = transactionType == "Income"

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF283A5F)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Summary Card
            TransactionSummaryCard(sms = sms, transactionType = transactionType)

            // Transaction Details Card
            TransactionDetailsCard(
                sms = sms,
                amount = amount,
                onAmountChange = { newValue ->
                    if (!isIncome) {
                        val currencySymbol = if (amount.startsWith("₹")) "₹" else ""
                        amount = currencySymbol + newValue.filter { it.isDigit() || it == '.' }
                    }
                },
                transactionType = transactionType,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                categoryOptions = categoryOptions,
                isIncome = isIncome,
                recipient = recipient,
                onRecipientChange = { recipient = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Extract amount as double, removing currency symbols
                        val amountValue = amount.replace(Regex("[₹$,\\s]"), "").toDoubleOrNull() ?: 0.0
                        
                        // Determine amount type
                        val amountType = if (isIncome) "income" else "expense"
                        
                        // Get appropriate icon name based on category
                        val iconName = when (selectedCategory.lowercase()) {
                            "food" -> "Restaurant"
                            "travel" -> "DirectionsCar"
                            "shopping" -> "ShoppingCart"
                            "groceries" -> "LocalGroceryStore"
                            "bills" -> "Receipt"
                            "salary" -> "AccountBalance"
                            "business income" -> "Business"
                            "ta/da" -> "CardTravel"
                            "interest" -> "TrendingUp"
                            "gift" -> "CardGiftcard"
                            "other income" -> "AttachMoney"
                            else -> "AttachMoney"
                        }
                        
                        // Create transaction in database
                        transactionViewModel.addTransaction(
                            title = sms.title,
                            amount = amountValue,
                            amountType = amountType,
                            category = selectedCategory,
                            iconName = iconName,
                            recipientName = if (recipient.isBlank()) null else recipient
                        )
                        
                        // Update SMS in the list
                        viewModel.updateSMS(sms.copy(recipient = if (recipient.isBlank()) null else recipient), selectedCategory, amount)
                        
                        Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                        // Refresh the transaction data before navigating back
                        transactionViewModel.loadSummaryData()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Save Transaction",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                OutlinedButton(
                    onClick = {
                        viewModel.removeSMS(sms)
                        Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionSummaryCard(sms: TransactionSMS, transactionType: String) {
    val (icon, iconColor) = when (transactionType) {
        "Income" -> Pair(Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF10B981))
        else -> Pair(Icons.AutoMirrored.Filled.TrendingDown, Color(0xFFEF4444))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon with colored background circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transactionType,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val label = if (transactionType == "Income") "From" else "To"
                val name = sms.recipient ?: "Unknown"
                Text(
                    text = "$label: $name",
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(sms.date),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Amount badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = sms.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsCard(
    sms: TransactionSMS,
    amount: String,
    onAmountChange: (String) -> Unit,
    transactionType: String,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    categoryOptions: List<String>,
    isIncome: Boolean,
    recipient: String,
    onRecipientChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Transaction Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748)
            )

            // Message
            Column {
                Text(
                    text = "Message",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A5568)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sms.message,
                    fontSize = 14.sp,
                    color = Color(0xFF2D3748),
                    lineHeight = 20.sp
                )
            }

            // Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = {
                    Text(
                        text = "Amount",
                        color = Color(0xFF4A5568)
                    )
                },
                singleLine = true,
                readOnly = isIncome,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF283A5F),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Transaction Type Field
            OutlinedTextField(
                value = transactionType,
                onValueChange = {},
                label = {
                    Text(
                        text = "Type",
                        color = Color(0xFF4A5568)
                    )
                },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF283A5F),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Recipient Field
            OutlinedTextField(
                value = recipient,
                onValueChange = onRecipientChange,
                label = {
                    Text(
                        text = if (isIncome) "From (Sender)" else "To (Recipient)",
                        color = Color(0xFF4A5568)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF283A5F),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Category Dropdown
            Column {
                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A5568)
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF283A5F),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categoryOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    onCategoryChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}