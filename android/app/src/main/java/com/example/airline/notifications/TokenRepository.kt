package com.example.airline.notifications

import android.content.Context
import android.util.Log
import com.example.airline.data.network.ApiClient
import com.example.airline.data.network.NotificationApi
import com.example.airline.data.network.SaveFcmTokenRequest
import com.example.airline.data.network.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TokenRepository {

    private const val TAG = "TokenRepository"
    private const val PREFS_NAME = "airline_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    private val notificationApi = ApiClient.retrofit.create(NotificationApi::class.java)

    fun fetchAndSaveToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d(TAG, "FCM Token: $token")
            saveTokenLocally(context, token)
            sendTokenToServer(token)
        }.addOnFailureListener {
            Log.e(TAG, "Échec récupération token: ${it.message}")
        }
    }

    private fun saveTokenLocally(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FCM_TOKEN, token)
            .apply()
    }

    private fun sendTokenToServer(token: String) {
        val userId = SessionManager.getUserId() ?: run {
            Log.w(TAG, "userId null, token non envoyé au backend")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationApi.saveToken(SaveFcmTokenRequest(userId, token))
                Log.d(TAG, "Token envoyé au backend ✅")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur envoi token: ${e.message}")
            }
        }
    }

    fun getSavedToken(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FCM_TOKEN, null)
    }
}