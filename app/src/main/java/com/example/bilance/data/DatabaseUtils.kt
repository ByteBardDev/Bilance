package com.example.bilance.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseUtils {
    suspend fun createDemoUser(database: BilanceDatabase) {
        withContext(Dispatchers.IO) {
            val userDao = database.userDao()
            
            // Check if demo user already exists
            val existingUser = userDao.getUserByEmail("demo@bilance.com")
            if (existingUser == null) {
                // Create demo user
                val demoUser = User(
                    fullName = "Demo User",
                    email = "demo@bilance.com",
                    mobileNumber = "+1 234 567 890",
                    dateOfBirth = "01/01/1990",
                    password = "demo123"
                )
                userDao.insertUser(demoUser)
            }
        }
    }
} 