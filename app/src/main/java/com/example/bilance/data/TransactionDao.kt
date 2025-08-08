package com.example.bilance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, time DESC")
    fun getAllTransactionsByUser(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND amountType = 'income' ORDER BY date DESC")
    fun getIncomeTransactions(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND amountType = 'expense' ORDER BY date DESC")
    fun getExpenseTransactions(userId: Int): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amountType = 'income'")
    suspend fun getTotalIncome(userId: Int): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amountType = 'expense'")
    suspend fun getTotalExpense(userId: Int): Double?

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(userId: Int, category: String): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllTransactionsForUser(userId: Int)
} 