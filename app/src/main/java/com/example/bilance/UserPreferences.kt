package com.example.bilance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

object UserPreferences {
    private const val PREFS_NAME = "bilance_prefs"
    private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
    private const val KEY_INITIAL_BALANCE = "initial_balance"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // These will be accessed via a composition local to avoid passing context around
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun requireContext(): Context =
        appContext ?: throw IllegalStateException("UserPreferences not initialized")

    fun setLoggedInEmail(email: String?) {
        val ctx = requireContext()
        prefs(ctx).edit().putString(KEY_LOGGED_IN_EMAIL, email).apply()
    }

    fun getLoggedInEmail(): String? {
        val ctx = requireContext()
        return prefs(ctx).getString(KEY_LOGGED_IN_EMAIL, null)
    }

    fun clearSession() {
        val ctx = requireContext()
        prefs(ctx).edit().remove(KEY_LOGGED_IN_EMAIL).apply()
    }

    fun setInitialBalance(value: Double) {
        val ctx = requireContext()
        prefs(ctx).edit().putString(KEY_INITIAL_BALANCE, value.toString()).apply()
    }

    fun getInitialBalance(): Double {
        val ctx = requireContext()
        val s = prefs(ctx).getString(KEY_INITIAL_BALANCE, null) ?: return 0.0
        return s.toDoubleOrNull() ?: 0.0
    }
}





