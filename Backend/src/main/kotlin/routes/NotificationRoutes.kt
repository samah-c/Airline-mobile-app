package com.example.routes

import com.example.models.SaveFcmTokenRequest
import com.example.services.NotificationService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.notificationRoutes(notificationService: NotificationService) {
    authenticate {

        // Android envoie son token FCM au backend
        post("/api/notifications/token") {
            val request = call.receive<SaveFcmTokenRequest>()
            notificationService.saveFcmToken(request.userId, request.token)
            call.respond(HttpStatusCode.OK, "Token saved")
        }
    }
}