@file:Suppress("PackageDirectoryMismatch")

package com.example.bilance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.bilance.model.TransactionSMS
import com.example.bilance.viewmodel.SMSViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: SMSViewModel,
    navController: NavController,
    sourceTab: String = "home",
    userEmail: String = ""
) {
    val context = LocalContext.current
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var isFilterApplied by remember { mutableStateOf(false) }

    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = { Text(text = "Select Month and Year") },
            text = {
                Column {
                    // Dropdown for year selection
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(8.dp)
                    ) {
                        Text(text = "Year: $selectedYear")
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            val years = (1990..Calendar.getInstance().get(Calendar.YEAR))
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(text = "$year") },
                                    onClick = {
                                        selectedYear = year
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Grid/List of months
                    months.forEachIndexed { index, name ->
                        Text(
                            text = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMonth = index
                                    showMonthPicker = false
                                    isFilterApplied = true
                                    Toast
                                        .makeText(context, "Filtered: ${months[selectedMonth]} $selectedYear", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text(text = "Close")
                }
            }
        )
    }


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
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Notifications",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 20.sp,
                    fontFamily = com.example.bilance.ui.theme.Poppins,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isFilterApplied) {
                        IconButton(
                            onClick = {
                                isFilterApplied = false
                                Toast.makeText(context, "Filter cleared", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(com.example.bilance.ui.theme.AccentRed.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear Filter",
                                tint = com.example.bilance.ui.theme.AccentRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showMonthPicker = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = com.example.bilance.ui.theme.TextOnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Modern card container
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = com.example.bilance.ui.theme.BackgroundPrimary
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                val filteredNotifications = if (isFilterApplied) {
                    viewModel.notifications.filter { sms ->
                        val cal = Calendar.getInstance().apply { time = sms.date }
                        cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
                    }
                } else {
                    viewModel.notifications
                }

                val grouped = filteredNotifications
                    .groupBy { sms ->
                        val now = Calendar.getInstance()
                        val smsDate = Calendar.getInstance().apply { time = sms.date }
                        when {
                            now.get(Calendar.DAY_OF_YEAR) == smsDate.get(Calendar.DAY_OF_YEAR) -> "Today"
                            now.get(Calendar.DAY_OF_YEAR) - 1 == smsDate.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
                            else -> "This Week"
                        }
                    }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    grouped.forEach { (section, smsItems) ->
                        item {
                            Text(
                                text = section,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = com.example.bilance.ui.theme.Poppins,
                                color = com.example.bilance.ui.theme.TextPrimary,
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp)
                            )
                        }
                        items(smsItems) { sms ->
                            NotificationCard(
                                sms = sms,
                                navController = navController,
                                onAccept = {
                                    navController.navigate("transactionDetail/${sms.id}")
                                },
                                onReject = {
                                    viewModel.removeSMS(sms)
                                    Toast.makeText(context, "Notification removed", Toast.LENGTH_SHORT).show()
                                },
                                onClick = {
                                    navController.navigate("transactionDetail/${sms.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    sms: TransactionSMS,
    navController: NavController,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    // State for marking as read (only for reminders)
    var isRead by remember { mutableStateOf(sms.type == "reminder_read") }
    val (icon, iconColor, notificationType) = when {
        sms.message.lowercase().contains("transaction") -> Triple(
            Icons.Default.AttachMoney,
            Color(0xFF4A90E2),
            "Transactions"
        )

        sms.message.lowercase().contains("reminder") -> Triple(
            Icons.Default.NotificationImportant,
            Color(0xFF7B68EE),
            "Reminder!"
        )

        sms.message.lowercase().contains("update") -> Triple(
            Icons.Default.Update,
            Color(0xFF50C878),
            "New Update"
        )

        sms.message.lowercase().contains("payment") -> Triple(
            Icons.Default.Payment,
            Color(0xFF2D3748),
            "Payment Record"
        )

        else -> Triple(
            Icons.Default.Notifications,
            Color(0xFF4A90E2),
            "Notification"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
                    text = notificationType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (sms.message.length > 60) sms.message.take(60) + "..." else sms.message,
                    fontSize = 12.sp,
                    color = Color(0xFF718096),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = SimpleDateFormat("HH:mm – MMM dd", Locale.getDefault()).format(sms.date),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        // Action buttons
        if (sms.isAutoCategorized) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Okay",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                OutlinedButton(
                    onClick = {
                        // Navigate to TransactionDetailsScreen for this notification
                        navController.navigate("transactionDetail/${sms.id}")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Change",
                        fontSize = 12.sp
                    )
                }
            }
        } else if (sms.message.lowercase().contains("transaction")) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Accept",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Reject",
                        fontSize = 12.sp
                    )
                }
            }
        } else if (sms.type == "reminder" || sms.type == "reminder_read") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Delete",
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = {
                        isRead = true
                        sms.type = "reminder_read"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    enabled = !isRead
                ) {
                    Text(
                        if (isRead) "Marked as Read" else "Mark as Read",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
