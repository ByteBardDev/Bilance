// File: TransactionViewModel.kt
package com.example.bilance.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.provider.Telephony
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.bilance.model.TransactionSMS
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel(
    private val contentResolver: ContentResolver
) : ViewModel() {

    val notifications = mutableStateListOf<TransactionSMS>()

    init {
        loadSMS()
    }

    private fun loadSMS() {
        val smsUri: Uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )

        val cursor = contentResolver.query(
            smsUri, projection, null, null, Telephony.Sms.DEFAULT_SORT_ORDER
        )

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        cursor?.use {
            val idIndex = cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
            val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val addressIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val dateIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)

            var id = 0
            while (cursor.moveToNext() && id < 50) {
                val address = cursor.getString(addressIndex)
                val body = cursor.getString(bodyIndex)
                val timestamp = cursor.getLong(dateIndex)
                val date = Date(timestamp)

                val lower = body.lowercase()
                if (lower.contains("debited") || lower.contains("credited") || lower.contains("transaction")) {
                    var amount = "N/A"
                    var type = "expense"

                    // Enhanced amount extraction patterns based on actual SMS formats
                    // Common pattern: "amount of INR 500.00"
                    val amountOfPattern = Regex("""amount of (?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

                    // Standard patterns for currency amounts
                    val mainAmountRegex = Regex("""(?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
                    val altAmountRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(?:Rs\.?|INR|rupees)""", RegexOption.IGNORE_CASE)

                    // Only use this as last resort to avoid matching irrelevant numbers
                    val simpleAmountRegex = Regex("""(\d+(?:[.,]\d+)?)""")

                    if (lower.contains("debited") || lower.contains("debit")) {
                        // Try to extract amount with specific patterns for debit messages
                        val debitedPattern = Regex("""(?:debited|debit)[^\d]*(?:Rs\.?|INR|₹)?\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

                        // Try patterns in sequence from most specific to least specific
                        amount = amountOfPattern.find(body)?.groupValues?.get(1)
                            ?: debitedPattern.find(body)?.groupValues?.get(1)
                                    ?: mainAmountRegex.find(body)?.groupValues?.get(1)
                                    ?: altAmountRegex.find(body)?.groupValues?.get(1)
                                    ?: simpleAmountRegex.find(body)?.groupValues?.get(1) ?: "N/A"

                        // Debug info to help diagnose issues
                        println("DEBIT SMS: $body")
                        println("EXTRACTED AMOUNT: $amount")

                        type = "expense"
                    } else if (lower.contains("credited") || lower.contains("credit")) {
                        // Try to extract amount with specific patterns for credit messages
                        val creditedPattern = Regex("""(?:credited|credit)[^\d]*(?:Rs\.?|INR|₹)?\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

                        // Try patterns in sequence from most specific to least specific
                        amount = amountOfPattern.find(body)?.groupValues?.get(1)
                            ?: creditedPattern.find(body)?.groupValues?.get(1)
                                    ?: mainAmountRegex.find(body)?.groupValues?.get(1)
                                    ?: altAmountRegex.find(body)?.groupValues?.get(1)
                                    ?: simpleAmountRegex.find(body)?.groupValues?.get(1) ?: "N/A"

                        // Debug info to help diagnose issues
                        println("CREDIT SMS: $body")
                        println("EXTRACTED AMOUNT: $amount")

                        type = "income"
                    }
                    if (amount != "N/A") {
                        val sms = TransactionSMS(
                            id = id++,
                            title = "Transaction",
                            message = body.take(80),
                            date = date,
                            category = "Uncategorized",
                            amount = "₹$amount",
                            type = type
                        )
                        notifications.add(sms)
                    }
                }
            }
        }
    }

    fun updateSMS(sms: TransactionSMS, category: String, amount: String) {
        val index = notifications.indexOfFirst { it.id == sms.id }
        if (index != -1) {
            notifications[index] = notifications[index].copy(category = category, amount = amount)
        }
    }

    fun removeSMS(sms: TransactionSMS) {
        notifications.remove(sms)
    }

    fun addTransaction(title: String, amount: String, category: String, type: String) {
        val newId = (notifications.maxByOrNull { it.id }?.id ?: 0) + 1
        val sms = TransactionSMS(
            id = newId,
            title = title,
            message = "Manual transaction entry",
            date = Date(),
            category = category,
            amount = amount,
            type = type
        )
        notifications.add(0, sms) // Add to the beginning of the list
    }
}
