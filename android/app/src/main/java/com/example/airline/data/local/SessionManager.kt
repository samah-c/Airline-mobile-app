package com.example.airline.data.local

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "airline_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(context: Context, token: String, userId: Int) {
        prefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getToken(context: Context): String? =
        prefs(context).getString(KEY_TOKEN, null)

    fun getUserId(context: Context): Int =
        prefs(context).getInt(KEY_USER_ID, -1)

    fun clear(context: Context) =
        prefs(context).edit().clear().apply()
}