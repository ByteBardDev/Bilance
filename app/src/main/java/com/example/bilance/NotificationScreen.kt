@file:Suppress("PackageDirectoryMismatch")

package com.example.bilance

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    navController: NavController,
    onTransactionClick: (TransactionSMS) -> Unit = { sms ->
        navController.navigate("transactionDetail/${sms.id}")
    }
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
            title = { Text("Select Month and Year") },
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
                                    text = { Text("$year") },
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
                    Text("Close")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Back navigation can go here */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showMonthPicker = true
                    }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->

        val grouped = viewModel.notifications
            .filter {
                val cal = Calendar.getInstance().apply { time = it.date }
                cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
            }
            .groupBy {
                val now = Calendar.getInstance()
                val smsDate = Calendar.getInstance().apply { time = it.date }
                when {
                    now.get(Calendar.DAY_OF_YEAR) == smsDate.get(Calendar.DAY_OF_YEAR) -> "Today"
                    now.get(Calendar.DAY_OF_YEAR) - 1 == smsDate.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
                    else -> "This Week"
                }
            }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            grouped.forEach { (section, items) ->
                item {
                    Text(
                        text = section,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(items) { sms ->
                    NotificationCard(
                        sms = sms,
                        onAccept = {
                            navController.navigate("transactionDetail/${sms.id}")
                        },
                        onReject = {
                            viewModel.removeSMS(sms)
                            Toast.makeText(context, "Notification removed", Toast.LENGTH_SHORT).show()
                        }

                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(sms: TransactionSMS, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = sms.title, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = sms.message, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = SimpleDateFormat("HH:mm – MMM dd", Locale.getDefault()).format(sms.date),
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBEE8C0))
                ) {
                    Text("Accept")
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}
