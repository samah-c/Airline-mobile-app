package com.example.airline.data.network

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "airline_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun saveToken(token: String) {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(KEY_TOKEN, null)
    }

    fun saveUserId(userId: Int) {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()?.putInt(KEY_USER_ID, userId)?.apply()
    }

    fun getUserId(): Int? {
        val id = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    fun clear() {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()?.clear()?.apply()
    }
}