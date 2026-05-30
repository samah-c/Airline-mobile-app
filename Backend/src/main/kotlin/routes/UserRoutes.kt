package com.example.routes

import com.example.models.UpdateProfileRequest
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(authService: AuthService) {
    authenticate {

        // Récupérer le profil
        get("/api/users/{userId}") {
            val userId = call.parameters["userId"]?.toInt()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
            if (tokenUserId != userId) {
                call.respond(HttpStatusCode.Forbidden, "Access denied")
                return@get
            }

            val user = authService.getUserById(userId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.respond(HttpStatusCode.OK, user)
        }

        // Modifier le profil
        put("/api/users/{userId}") {
            val userId = call.parameters["userId"]?.toInt()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
            if (tokenUserId != userId) {
                call.respond(HttpStatusCode.Forbidden, "Access denied")
                return@put
            }

            val request = call.receive<UpdateProfileRequest>()

            if (request.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Name is required")
                return@put
            }

            authService.updateProfile(userId, request)
            call.respond(HttpStatusCode.OK, "Profile updated")
        }
    }
}