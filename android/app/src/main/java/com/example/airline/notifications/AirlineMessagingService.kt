package com.example.airline.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.airline.MainActivity
import com.example.airline.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AirlineMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_CHECKIN = "checkin_channel"
        const val CHANNEL_BOARDING = "boarding_channel"
    }

    // Appelé quand un nouveau token FCM est généré
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        TokenRepository.fetchAndSaveToken(applicationContext)
    }

    // Appelé quand une notification arrive (app en foreground)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data["type"]
        val title = message.notification?.title ?: "Airline"
        val body = message.notification?.body ?: ""

        val channelId = when (type) {
            "CHECKIN_CONFIRMED" -> CHANNEL_CHECKIN
            "BOARDING_PASS_READY" -> CHANNEL_BOARDING
            else -> CHANNEL_CHECKIN
        }

        showNotification(title, body, channelId, message.data)
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        data: Map<String, String>
    ) {
        createChannels()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // Passer les données pour navigation deep link
            data.forEach { (k, v) -> putExtra(k, v) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_airplane) // adapte selon ton drawable
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            NotificationChannel(
                CHANNEL_CHECKIN,
                "Check-in Confirmations",
                NotificationManager.IMPORTANCE_HIGH
            ).also { manager.createNotificationChannel(it) }

            NotificationChannel(
                CHANNEL_BOARDING,
                "Boarding Pass",
                NotificationManager.IMPORTANCE_HIGH
            ).also { manager.createNotificationChannel(it) }
        }
    }
}