package com.example.bilance.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import java.util.*

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
    
    suspend fun createSampleTransactions(database: BilanceDatabase) {
        withContext(Dispatchers.IO) {
            val transactionDao = database.transactionDao()
            
            // Check if transactions already exist
            val existingTransactions = transactionDao.getAllTransactionsByUser(1).first()
            if (existingTransactions.isEmpty()) {
                val calendar = Calendar.getInstance()
                
                // Sample transactions with more realistic Indian amounts
                val sampleTransactions = listOf(
                    Transaction(
                        title = "Salary",
                        amount = 45000.0,
                        amountType = "income",
                        category = "Monthly",
                        date = calendar.apply { 
                            set(2024, Calendar.APRIL, 30)
                        }.time,
                        time = "18:27",
                        iconName = "AttachMoney",
                        userId = 1
                    ),
                    Transaction(
                        title = "Groceries",
                        amount = 2500.0,
                        amountType = "expense",
                        category = "Pantry",
                        date = calendar.apply { 
                            set(2024, Calendar.APRIL, 24)
                        }.time,
                        time = "17:00",
                        iconName = "ShoppingCart",
                        userId = 1
                    ),
                    Transaction(
                        title = "Rent",
                        amount = 15000.0,
                        amountType = "expense",
                        category = "Rent",
                        date = calendar.apply { 
                            set(2024, Calendar.APRIL, 15)
                        }.time,
                        time = "8:30",
                        iconName = "Home",
                        userId = 1
                    ),
                    Transaction(
                        title = "Fuel",
                        amount = 1200.0,
                        amountType = "expense",
                        category = "Fuel",
                        date = calendar.apply { 
                            set(2024, Calendar.APRIL, 8)
                        }.time,
                        time = "9:30",
                        iconName = "DirectionsCar",
                        userId = 1
                    ),
                    Transaction(
                        title = "Restaurant",
                        amount = 800.0,
                        amountType = "expense",
                        category = "Dinner",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 31)
                        }.time,
                        time = "19:30",
                        iconName = "Restaurant",
                        userId = 1
                    ),
                    Transaction(
                        title = "Freelance Project",
                        amount = 8000.0,
                        amountType = "income",
                        category = "Business Income",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 25)
                        }.time,
                        time = "14:15",
                        iconName = "Business",
                        userId = 1
                    ),
                    Transaction(
                        title = "Movie Tickets",
                        amount = 600.0,
                        amountType = "expense",
                        category = "Entertainment",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 20)
                        }.time,
                        time = "20:00",
                        iconName = "Movie",
                        userId = 1
                    ),
                    Transaction(
                        title = "Internet Bill",
                        amount = 999.0,
                        amountType = "expense",
                        category = "Bills",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 15)
                        }.time,
                        time = "10:30",
                        iconName = "Receipt",
                        userId = 1
                    ),
                    Transaction(
                        title = "Gift",
                        amount = 1500.0,
                        amountType = "expense",
                        category = "Gift",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 10)
                        }.time,
                        time = "16:45",
                        iconName = "CardGiftcard",
                        userId = 1
                    ),
                    Transaction(
                        title = "Interest Income",
                        amount = 500.0,
                        amountType = "income",
                        category = "Interest",
                        date = calendar.apply { 
                            set(2024, Calendar.MARCH, 5)
                        }.time,
                        time = "12:00",
                        iconName = "TrendingUp",
                        userId = 1
                    )
                )
                
                sampleTransactions.forEach { transaction ->
                    transactionDao.insertTransaction(transaction)
                }
            }
        }
    }
} 