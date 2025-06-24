package com.example.bilance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val mobileNumber: String,
    val dateOfBirth: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
) 