package com.example.bilance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bilance.data.BilanceDatabase

class TransactionViewModelFactory(
    private val database: BilanceDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
