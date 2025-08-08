package com.example.bilance.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconUtils {
    fun getIconFromName(iconName: String): ImageVector {
        return when (iconName) {
            "AttachMoney" -> Icons.Default.AttachMoney
            "ShoppingCart" -> Icons.Default.ShoppingCart
            "Home" -> Icons.Default.Home
            "DirectionsCar" -> Icons.Default.DirectionsCar
            "Restaurant" -> Icons.Default.Restaurant
            "LocalGroceryStore" -> Icons.Default.LocalGroceryStore
            "AccountBalance" -> Icons.Default.AccountBalance
            "Payment" -> Icons.Default.Payment
            "CreditCard" -> Icons.Default.CreditCard
            "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
            "TrendingUp" -> Icons.AutoMirrored.Filled.TrendingUp
            "TrendingDown" -> Icons.AutoMirrored.Filled.TrendingDown
            "Notifications" -> Icons.Default.Notifications
            "Check" -> Icons.Default.Check
            "Receipt" -> Icons.Default.Receipt
            "Business" -> Icons.Default.Business
            "CardTravel" -> Icons.Default.CardTravel
            "CardGiftcard" -> Icons.Default.CardGiftcard
            else -> Icons.Default.AccountBalance // Default icon
        }
    }
} 