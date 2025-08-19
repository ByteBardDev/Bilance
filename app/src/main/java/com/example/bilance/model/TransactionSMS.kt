package com.example.bilance.model

import java.util.*

data class TransactionSMS(
    val id: Int, // Display ID for UI
    val smsId: String, // Actual SMS ID from database for tracking
    val title: String,
    val message: String,
    val date: Date,
    var category: String = "",
    var amount: String = "",
    var type: String = "", // e.g., "transaction", "reminder"
    var recipient: String? = null
)