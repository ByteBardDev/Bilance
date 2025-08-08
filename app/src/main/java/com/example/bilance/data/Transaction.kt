package com.example.bilance.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val amountType: String, // "income" or "expense"
    val category: String,
    val date: Date,
    val time: String,
    val iconName: String, // Store icon name as string
    val userId: Int = 1, // Default user ID
    val createdAt: Long = System.currentTimeMillis()
) 