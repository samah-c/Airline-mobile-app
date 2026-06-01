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

            // GET profil de l'user connecté
            get("/api/users/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val user = authService.getUserById(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

                call.respond(HttpStatusCode.OK, user)
            }

            // PUT modifier le profil
            put("/api/users/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val request = call.receive<UpdateProfileRequest>()

                if (request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Name is required")
                    return@put
                }

                authService.updateProfile(userId, request)

                val updated = authService.getUserById(userId)
                call.respond(HttpStatusCode.OK, updated!!)
            }
        }
    }
