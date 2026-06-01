package com.example.airline.data.local

import android.content.Context
import android.content.SharedPreferences

object SettingsPreferences {
    private const val PREFS_NAME = "airline_settings"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_NOTIFICATIONS = "notifications"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getDarkMode(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "Français") ?: "Français"

    fun setLanguage(context: Context, language: String) =
        prefs(context).edit().putString(KEY_LANGUAGE, language).apply()

    fun getNotifications(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIFICATIONS, true)

    fun setNotifications(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
}