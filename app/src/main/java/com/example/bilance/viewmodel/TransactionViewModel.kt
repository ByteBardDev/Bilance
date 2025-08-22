package com.example.bilance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bilance.UserPreferences
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(private val database: BilanceDatabase) : ViewModel() {
    // Callback for repeated transaction auto-categorization notification
    var onAutoCategorized: ((String, String) -> Unit)? = null // (sender/receiver, category)
    // State to track if threshold exceeded
    private val _thresholdExceeded = MutableStateFlow(false)
    val thresholdExceeded: StateFlow<Boolean> = _thresholdExceeded.asStateFlow()

    // Callback to add a notification entry (to be set by UI layer)
    var onThresholdExceeded: (() -> Unit)? = null
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    
    private val _expensePercentage = MutableStateFlow(0.0)
    val expensePercentage: StateFlow<Double> = _expensePercentage.asStateFlow()
    
    init {
        println("DEBUG: TransactionViewModel init called - Instance: ${this.hashCode()}")
        startTransactionCollection()
        loadSummaryData()
    }
    
    fun refreshTransactions() {
        viewModelScope.launch {
            println("DEBUG: refreshTransactions() called")
            try {
                // Manually trigger the flow to refresh by restarting the collection
                loadTransactions()
            } catch (e: Exception) {
                println("DEBUG: Error in manual refresh: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    private fun startTransactionCollection() {
        viewModelScope.launch {
            println("DEBUG: startTransactionCollection() called")
            try {
                database.transactionDao().getAllTransactionsByUser(1).collect { transactions ->
                    println("DEBUG: Flow emitted ${transactions.size} transactions from database")
                    println("DEBUG: Transaction titles: ${transactions.map { it.title }}")
                    println("DEBUG: Setting _transactions.value with ${transactions.size} transactions")
                    _transactions.value = transactions
                    println("DEBUG: _transactions.value set, current size: ${_transactions.value.size}")
                    
                    // Recalculate summary whenever transactions change
                    loadSummaryData()
                }
            } catch (e: Exception) {
                println("DEBUG: Error in transaction collection: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun loadTransactions() {
        println("DEBUG: loadTransactions() called - using existing collection")
        // The collection is already started in init, just log current state
        println("DEBUG: Current transactions count: ${_transactions.value.size}")
    }
    
    fun loadSummaryData() {
        viewModelScope.launch {
            val income = database.transactionDao().getTotalIncome(1) ?: 0.0
            val expense = database.transactionDao().getTotalExpense(1) ?: 0.0
            val initialBalance = UserPreferences.getInitialBalance()

            println("DEBUG: loadSummaryData() - Income: $income, Expense: $expense")

            _totalIncome.value = income
            _totalExpense.value = expense
            val available = initialBalance + income
            _totalBalance.value = available - expense

            // Calculate expense percentage based on initial balance + total income
            _expensePercentage.value = if (available > 0) (expense / available) * 100 else 0.0

            // Check threshold
            val threshold = UserPreferences.getMonthlyExpenseThreshold()
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            val monthExpenses = _transactions.value.filter {
                val cal = Calendar.getInstance().apply { time = it.date }
                cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear && it.amountType == "expense"
            }.sumOf { it.amount }
            val exceeded = threshold > 0 && monthExpenses > threshold
            if (exceeded && !_thresholdExceeded.value) {
                _thresholdExceeded.value = true
                onThresholdExceeded?.invoke()
            } else if (!exceeded && _thresholdExceeded.value) {
                _thresholdExceeded.value = false
            }
        }
    }
    
    fun addTransaction(
        title: String,
        amount: Double,
        amountType: String,
        category: String,
        iconName: String,
        recipientName: String? = null,
        date: Date = Date() // Default to current date if not provided
    ) {
        viewModelScope.launch {
            try {
                // Save mapping if recipientName is present and category is not Uncategorized
                if (!recipientName.isNullOrBlank() && category != "Uncategorized") {
                    UserPreferences.saveCategoryMapping(recipientName, category)
                }
                val transaction = Transaction(
                    title = title,
                    amount = amount,
                    amountType = amountType,
                    category = category,
                    date = date,
                    time = getCurrentTime(),
                    iconName = iconName,
                    recipientName = recipientName,
                    userId = 1
                )
                println("DEBUG: Adding transaction: $title, amount: $amount, type: $amountType, category: $category")
                println("DEBUG: Transaction object created: $transaction")
                val id = database.transactionDao().insertTransaction(transaction)
                println("DEBUG: Transaction inserted with ID: $id")
                loadSummaryData()
                println("DEBUG: Transaction added, waiting for Flow to update automatically")
            } catch (e: Exception) {
                println("DEBUG: Error adding transaction: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            database.transactionDao().deleteTransaction(transaction)
            loadSummaryData()
        }
    }
    
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }
    
    fun getTransactionsByMonth(): Map<String, List<Transaction>> {
        val transactions = _transactions.value
        return transactions.groupBy { transaction ->
            val calendar = Calendar.getInstance().apply { time = transaction.date }
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            getMonthName(month) + " " + year
        }
    }
    
    private fun getMonthName(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "January"
            Calendar.FEBRUARY -> "February"
            Calendar.MARCH -> "March"
            Calendar.APRIL -> "April"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "June"
            Calendar.JULY -> "July"
            Calendar.AUGUST -> "August"
            Calendar.SEPTEMBER -> "September"
            Calendar.OCTOBER -> "October"
            Calendar.NOVEMBER -> "November"
            Calendar.DECEMBER -> "December"
            else -> "Unknown"
        }
    }
    
    // Method to convert SMS transaction to database transaction
    fun addTransactionFromSMS(
        smsTransaction: com.example.bilance.model.TransactionSMS,
        finalCategory: String,
        finalAmount: String
    ) {
        viewModelScope.launch {
            try {
                // Auto-categorize if mapping exists
                var categoryToUse = finalCategory
                val recipient = smsTransaction.recipient
                if (!recipient.isNullOrBlank()) {
                    val mapping = UserPreferences.getCategoryMapping()
                    val mappedCategory = mapping[recipient]
                    if (!mappedCategory.isNullOrBlank()) {
                        categoryToUse = mappedCategory
                        // Notify UI layer for repeated transaction
                        onAutoCategorized?.invoke(recipient, mappedCategory)
                    }
                }
                // Extract numeric amount from string (remove ₹ and any formatting)
                val cleanAmount = finalAmount.replace("₹", "").replace(",", "").trim()
                val numericAmount = cleanAmount.toDoubleOrNull() ?: 0.0
                // Determine icon based on category
                val iconName = when (categoryToUse.lowercase()) {
                    "food", "food & dining" -> "food"
                    "groceries" -> "groceries"
                    "transport", "transportation" -> "transport"
                    "rent" -> "rent"
                    "salary" -> "salary"
                    "transfer" -> "transfer"
                    else -> "layers"
                }
                val transaction = Transaction(
                    title = if (smsTransaction.title.isNotBlank()) smsTransaction.title else "SMS Transaction",
                    amount = numericAmount,
                    amountType = smsTransaction.type.ifEmpty { "expense" },
                    category = categoryToUse,
                    date = smsTransaction.date,
                    time = getCurrentTime(),
                    iconName = iconName,
                    recipientName = smsTransaction.recipient,
                    userId = 1
                )
                println("DEBUG: Converting SMS transaction to database transaction: $transaction")
                val id = database.transactionDao().insertTransaction(transaction)
                println("DEBUG: SMS Transaction converted and inserted with ID: $id")
                // Save mapping if user manually categorized
                if (!recipient.isNullOrBlank() && finalCategory != "Uncategorized") {
                    UserPreferences.saveCategoryMapping(recipient, finalCategory)
                }
                loadSummaryData()
            } catch (e: Exception) {
                println("DEBUG: Error converting SMS transaction: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
