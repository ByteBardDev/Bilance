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
import com.example.bilance.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }

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


    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notification",
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
                actions = {
                    IconButton(onClick = {
                        showMonthPicker = true
                    }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
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

        val grouped = viewModel.notifications
            .filter { sms ->
                val cal = Calendar.getInstance().apply { time = sms.date }
                cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
            }
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
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            grouped.forEach { (section, smsItems) ->
                item {
                    Text(
                        text = section,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                    )
                }
                items(smsItems) { sms ->
                    NotificationCard(
                        sms = sms,
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

@Composable
fun NotificationCard(sms: TransactionSMS, onAccept: () -> Unit, onReject: () -> Unit, onClick: () -> Unit) {
    // Determine notification type and icon based on SMS content
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

        // Action buttons (keeping the original functionality)
        if (sms.message.lowercase().contains("transaction")) {
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
        }
    }
}
