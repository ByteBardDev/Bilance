package com.example.bilance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(private val database: BilanceDatabase) : ViewModel() {
    
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
            
            println("DEBUG: loadSummaryData() - Income: $income, Expense: $expense")
            
            _totalIncome.value = income
            _totalExpense.value = expense
            _totalBalance.value = income - expense
            
            // Calculate expense percentage (assuming goal is ₹20,000)
            val goal = 20000.0
            _expensePercentage.value = if (goal > 0) (expense / goal) * 100 else 0.0
        }
    }
    
    fun addTransaction(
        title: String,
        amount: Double,
        amountType: String,
        category: String,
        iconName: String
    ) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    title = title,
                    amount = amount,
                    amountType = amountType,
                    category = category,
                    date = Date(),
                    time = getCurrentTime(),
                    iconName = iconName,
                    userId = 1
                )
                println("DEBUG: Adding transaction: $title, amount: $amount, type: $amountType, category: $category")
                println("DEBUG: Transaction object created: $transaction")
                
                val id = database.transactionDao().insertTransaction(transaction)
                println("DEBUG: Transaction inserted with ID: $id")
                
                // The Flow should auto-update, and we'll also reload summary data
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
}
