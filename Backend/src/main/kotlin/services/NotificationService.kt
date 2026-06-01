package com.example.services

import com.example.schemas.FcmTokens
import com.example.schemas.dbQuery
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import java.io.FileInputStream
import java.time.LocalDateTime

class NotificationService(private val database: Database) {

    init {
        // Initialiser Firebase une seule fois (skip si le fichier credentials est absent)
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                val serviceAccount = FileInputStream("airline-checkin-8710e-firebase-adminsdk-fbsvc-f517d46ffd.json")
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                FirebaseApp.initializeApp(options)
            } catch (e: Exception) {
                println("⚠️  Firebase not initialized (credentials file missing): ${e.message}")
            }
        }
    }

    // Sauvegarder le token FCM de l'appareil
    suspend fun saveFcmToken(userId: Int, token: String) = dbQuery {
        val existing = FcmTokens.selectAll()
            .where { FcmTokens.userId eq userId }
            .singleOrNull()

        if (existing != null) {
            FcmTokens.update({ FcmTokens.userId eq userId }) {
                it[FcmTokens.token] = token
                it[updatedAt] = LocalDateTime.now().toString()
            }
        } else {
            FcmTokens.insert {
                it[FcmTokens.userId] = userId
                it[FcmTokens.token] = token
                it[updatedAt] = LocalDateTime.now().toString()
            }
        }
    }

    // Envoyer notification après check-in complété
    suspend fun sendCheckInConfirmation(userId: Int, flightNumber: String, seat: String) {
        val token = getFcmToken(userId) ?: return

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("✅ Check-in confirmé !")
                    .setBody("Vol $flightNumber — Siège $seat. Bon voyage !")
                    .build()
            )
            .putData("type", "CHECKIN_CONFIRMED")
            .putData("flightNumber", flightNumber)
            .putData("seat", seat)
            .setToken(token)
            .build()

        FirebaseMessaging.getInstance().send(message)
    }

    // Envoyer notification boarding pass prêt
    suspend fun sendBoardingPassReady(userId: Int, boardingPassId: Int) {
        val token = getFcmToken(userId) ?: return

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("🎫 Boarding Pass prêt !")
                    .setBody("Votre carte d'embarquement est disponible.")
                    .build()
            )
            .putData("type", "BOARDING_PASS_READY")
            .putData("boardingPassId", boardingPassId.toString())
            .setToken(token)
            .build()

        FirebaseMessaging.getInstance().send(message)
    }

    private suspend fun getFcmToken(userId: Int): String? = dbQuery {
        FcmTokens.selectAll()
            .where { FcmTokens.userId eq userId }
            .singleOrNull()
            ?.get(FcmTokens.token)
    }
}