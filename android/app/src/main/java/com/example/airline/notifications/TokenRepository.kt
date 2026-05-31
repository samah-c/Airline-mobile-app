package com.example.airline.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object TokenRepository {

    private const val TAG = "TokenRepository"
    private const val PREFS_NAME = "airline_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    // Récupère et sauvegarde le token localement
    fun fetchAndSaveToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d(TAG, "FCM Token: $token") // visible dans Logcat
            saveTokenLocally(context, token)
            // TODO: envoyer au backend quand Retrofit sera intégré
            // sendTokenToServer(token)
        }.addOnFailureListener {
            Log.e(TAG, "Échec récupération token: ${it.message}")
        }
    }

    // Sauvegarde dans SharedPreferences
    private fun saveTokenLocally(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FCM_TOKEN, token)
            .apply()
    }

    // Lire le token sauvegardé (utile plus tard pour l'envoyer au backend)
    fun getSavedToken(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FCM_TOKEN, null)
    }
}