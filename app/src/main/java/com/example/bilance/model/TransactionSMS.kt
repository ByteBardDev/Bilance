package com.example.bilance.model

import java.util.*

data class TransactionSMS(
    val id: Int,
    val title: String,
    val message: String,
    val date: Date,
    var category: String = "",
    var amount: String = "",
    var type: String = "", // e.g., "transaction", "reminder"
)