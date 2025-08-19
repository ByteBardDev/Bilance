package com.example.bilance.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.provider.Telephony
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.bilance.model.TransactionSMS
import java.text.SimpleDateFormat
import java.util.*

class SMSViewModel(
    private val contentResolver: ContentResolver
) : ViewModel() {

    val notifications = mutableStateListOf<TransactionSMS>()
    
    // Keep track of processed/deleted SMS IDs to avoid showing them again
    private val processedSmsIds = mutableSetOf<String>()
    private val deletedSmsIds = mutableSetOf<String>()

    init {
        loadSMS()
    }
    
    // Mark SMS as processed (added to transactions)
    fun markSmsAsProcessed(smsId: String) {
        processedSmsIds.add(smsId)
    }
    
    // Mark SMS as deleted by user
    fun markSmsAsDeleted(smsId: String) {
        deletedSmsIds.add(smsId)
    }
    
    // Check if SMS should be shown (not processed or deleted)
    private fun shouldShowSms(smsId: String): Boolean {
        return !processedSmsIds.contains(smsId) && !deletedSmsIds.contains(smsId)
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

            var displayId = 0
            while (cursor.moveToNext() && displayId < 50) {
                val smsId = cursor.getString(idIndex) // Actual SMS ID from database
                val address = cursor.getString(addressIndex)
                val body = cursor.getString(bodyIndex)
                val timestamp = cursor.getLong(dateIndex)
                val date = Date(timestamp)
                
                // Skip this SMS if it's been processed or deleted
                if (!shouldShowSms(smsId)) {
                    continue
                }

                // Debug: Log all SMS messages to understand what's being processed
                println("SMS DEBUG - Address: $address")
                println("SMS DEBUG - Body: $body")
                println("SMS DEBUG - Date: $date")

                val lower = body.lowercase()
                val hasKeywords = lower.contains("debited") || lower.contains("credited") || lower.contains("transaction") || lower.contains("sent")
                
                // Special check for HDFC format
                val isHdfcSms = lower.contains("sent") && lower.contains("rs.") && (lower.contains("hdfc") || address?.contains("HDFC", ignoreCase = true) == true)
                
                println("SMS DEBUG - Contains keywords: $hasKeywords")
                println("SMS DEBUG - Contains 'sent': ${lower.contains("sent")}")
                println("SMS DEBUG - Is HDFC SMS: $isHdfcSms")
                println("SMS DEBUG - Lowercase body: $lower")
                println("---")

                if (hasKeywords || isHdfcSms) {
                    var amount = "N/A"
                    var type = "expense"
                    var recipient: String? = null

                    // Enhanced amount extraction patterns based on actual SMS formats
                    // Common pattern: "amount of INR 500.00"
                    val amountOfPattern = Regex("""amount of (?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

                    // Standard patterns for currency amounts - prioritize INR and Rs prefixed amounts
                    val mainAmountRegex = Regex("""(?:Rs\.?|INR|Rs|₹)\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
                    val altAmountRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(?:Rs\.?|INR|rupees)""", RegexOption.IGNORE_CASE)

                    // Simple pattern to find account numbers
                    val accountNumberPattern = Regex("""(?:A/c|account|acc)\s*(?:no\.?|number)\s*[:\s]*(?:XX|x{2,})?(\d{4,6})""", RegexOption.IGNORE_CASE)
                    
                    // Simple amount pattern for fallback (without complex lookbehind)
                    val fallbackAmountRegex = Regex("""(\d{3,}(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE)

                    if (lower.contains("debited") || lower.contains("debit") || lower.contains("sent") || isHdfcSms) {
                        // Try to extract amount with specific patterns for debit/sent messages
                        val debitedPattern = Regex("""(?:debited|debit)[^\d]*(?:Rs\.?|INR|₹)?\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
                        
                        // HDFC specific patterns - multiple variations to catch different formats
                        val hdfcSentPattern1 = Regex("""sent\s+rs\.(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
                        val hdfcSentPattern2 = Regex("""sent\s+rs\s+(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
                        val hdfcSentPattern3 = Regex("""sent.*?rs\.?(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
                        
                        // General sent patterns
                        val sentPattern = Regex("""sent\s+Rs\.?(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
                        val hdfcPattern = Regex("""sent\s+rs\.?(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)

                        // Debug each pattern attempt
                        val hdfcMatch1 = hdfcSentPattern1.find(body)
                        val hdfcMatch2 = hdfcSentPattern2.find(body)
                        val hdfcMatch3 = hdfcSentPattern3.find(body)
                        val sentMatch = sentPattern.find(body)
                        val hdfcMatch = hdfcPattern.find(body)
                        val amountOfMatch = amountOfPattern.find(body)
                        val debitedMatch = debitedPattern.find(body)
                        val mainMatch = mainAmountRegex.find(body)
                        val altMatch = altAmountRegex.find(body)
                        val fallbackMatch = fallbackAmountRegex.find(body)

                        println("PATTERN DEBUG - HDFC Pattern 1 match: ${hdfcMatch1?.groupValues}")
                        println("PATTERN DEBUG - HDFC Pattern 2 match: ${hdfcMatch2?.groupValues}")
                        println("PATTERN DEBUG - HDFC Pattern 3 match: ${hdfcMatch3?.groupValues}")
                        println("PATTERN DEBUG - Sent pattern match: ${sentMatch?.groupValues}")
                        println("PATTERN DEBUG - HDFC pattern match: ${hdfcMatch?.groupValues}")
                        println("PATTERN DEBUG - Amount of pattern match: ${amountOfMatch?.groupValues}")
                        println("PATTERN DEBUG - Debited pattern match: ${debitedMatch?.groupValues}")
                        println("PATTERN DEBUG - Main amount match: ${mainMatch?.groupValues}")
                        println("PATTERN DEBUG - Alt amount match: ${altMatch?.groupValues}")
                        println("PATTERN DEBUG - Fallback amount match: ${fallbackMatch?.groupValues}")
                        println("PATTERN DEBUG - Account number pattern: ${accountNumberPattern.find(body)?.groupValues}")

                        // Get account number to exclude from amount matching
                        val accountNumber = accountNumberPattern.find(body)?.groupValues?.get(1)
                        
                        // Try patterns in sequence - prioritize currency-prefixed amounts first
                        amount = amountOfMatch?.groupValues?.get(1)
                            ?: mainMatch?.groupValues?.get(1)  // INR/Rs prefixed amounts (highest priority)
                            ?: hdfcMatch1?.groupValues?.get(1)
                            ?: hdfcMatch2?.groupValues?.get(1)
                            ?: hdfcMatch3?.groupValues?.get(1)
                            ?: sentMatch?.groupValues?.get(1)
                            ?: hdfcMatch?.groupValues?.get(1)
                            ?: debitedMatch?.groupValues?.get(1)
                            ?: altMatch?.groupValues?.get(1)
                            ?: fallbackAmountRegex.find(body)?.groupValues?.get(1) ?: "N/A"
                        
                        // Additional validation: if extracted amount matches account number, try alternative patterns
                        if (accountNumber != null && amount == accountNumber) {
                            println("DEBUG: Detected account number $accountNumber as amount, trying alternative patterns")
                            // Try to find amount that's not the account number
                            val allAmountMatches = mainAmountRegex.findAll(body).map { it.groupValues[1] }.toList()
                            amount = allAmountMatches.firstOrNull { it != accountNumber && it.length >= 3 } ?: "N/A"
                        }

                        // Debug info to help diagnose issues
                        println("DEBIT/SENT SMS: $body")
                        println("EXTRACTED AMOUNT: $amount")

                        type = "expense"
                        // Attempt to extract recipient for debit/transfer messages
                        val recipientPatterns = listOf(
                            Regex("\\bto\\s+([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE),
                            Regex("\\bsent\\s+to\\s+([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE),
                            Regex("UPI\\s*(?:txn|transaction)?\\s*to\\s*([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE),
                            Regex("IMPS\\s*to\\s*([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE),
                            Regex("NEFT\\s*to\\s*([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE)
                        )
                        recipient = recipientPatterns.firstNotNullOfOrNull { it.find(body)?.groupValues?.getOrNull(1) }
                        recipient = recipient?.replace(Regex("A/c.*$", RegexOption.IGNORE_CASE), "")?.trim()?.trimEnd('.', ',')
                    } else if (lower.contains("credited") || lower.contains("credit")) {
                        // Try to extract amount with specific patterns for credit messages
                        val creditedPattern = Regex("""(?:credited|credit)[^\d]*(?:Rs\.?|INR|₹)?\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

                        // Get account number to exclude from amount matching
                        val accountNumber = accountNumberPattern.find(body)?.groupValues?.get(1)
                        
                        // Try patterns in sequence - prioritize currency-prefixed amounts first
                        amount = amountOfPattern.find(body)?.groupValues?.get(1)
                            ?: mainAmountRegex.find(body)?.groupValues?.get(1)  // INR/Rs prefixed amounts (highest priority)
                            ?: creditedPattern.find(body)?.groupValues?.get(1)
                            ?: altAmountRegex.find(body)?.groupValues?.get(1)
                            ?: fallbackAmountRegex.find(body)?.groupValues?.get(1) ?: "N/A"
                        
                        // Additional validation: if extracted amount matches account number, try alternative patterns
                        if (accountNumber != null && amount == accountNumber) {
                            println("DEBUG: Detected account number $accountNumber as amount, trying alternative patterns")
                            // Try to find amount that's not the account number
                            val allAmountMatches = mainAmountRegex.findAll(body).map { it.groupValues[1] }.toList()
                            amount = allAmountMatches.firstOrNull { it != accountNumber && it.length >= 3 } ?: "N/A"
                        }

                        // Debug info to help diagnose issues
                        println("CREDIT SMS: $body")
                        println("EXTRACTED AMOUNT: $amount")
                        println("ACCOUNT NUMBER DETECTED: $accountNumber")

                        type = "income"
                        // Attempt to extract sender for credit messages (treated as recipient/source field)
                        val senderPatterns = listOf(
                            Regex("\\bfrom\\s+([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE),
                            Regex("\\bby\\s+([A-Za-z][A-Za-z\\s.'-]{1,40})", RegexOption.IGNORE_CASE)
                        )
                        recipient = senderPatterns.firstNotNullOfOrNull { it.find(body)?.groupValues?.getOrNull(1) }
                        recipient = recipient?.trim()?.trimEnd('.', ',')
                    }
                    
                    // Additional debugging for amount extraction result
                    println("FINAL AMOUNT EXTRACTED: $amount")
                    println("AMOUNT != N/A: ${amount != "N/A"}")
                    
                    if (amount != "N/A") {
                        val sms = TransactionSMS(
                            id = displayId++,
                            smsId = smsId,
                            title = "Transaction",
                            message = body.take(80),
                            date = date,
                            category = "Uncategorized",
                            amount = "₹$amount",
                            type = type,
                            recipient = recipient
                        )
                        notifications.add(sms)
                        println("SMS ADDED TO NOTIFICATIONS: ${sms.message}")
                    } else {
                        println("SMS REJECTED - NO AMOUNT EXTRACTED: $body")
                    }
                }
            }
        }
    }

    fun updateSMS(sms: TransactionSMS, category: String, amount: String) {
        val index = notifications.indexOfFirst { it.id == sms.id }
        if (index != -1) {
            notifications[index] = notifications[index].copy(category = category, amount = amount, recipient = sms.recipient)
        }
    }

    fun removeSMS(sms: TransactionSMS) {
        notifications.remove(sms)
        markSmsAsProcessed(sms.smsId) // Mark as processed when added to transactions
    }
    
    fun deleteSMS(sms: TransactionSMS) {
        notifications.remove(sms)
        markSmsAsDeleted(sms.smsId) // Mark as deleted when user swipes to delete
    }

    fun addTransaction(title: String, amount: String, category: String, type: String) {
        val newId = (notifications.maxByOrNull { it.id }?.id ?: 0) + 1
        val sms = TransactionSMS(
            id = newId,
            smsId = "manual_${System.currentTimeMillis()}", // Generate unique ID for manual entries
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