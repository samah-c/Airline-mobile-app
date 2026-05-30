package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.AuthResponse
import com.example.models.LoginRequest
import com.example.models.RegisterRequest
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Date

fun Route.authRoutes(authService: AuthService) {

    val jwtSecret = "secret"
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"

    post("/api/auth/register") {
        val request = call.receive<RegisterRequest>()

        // Validations
        if (request.name.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Name is required")
            return@post
        }
        if (!request.email.contains("@") || !request.email.contains(".")) {
            call.respond(HttpStatusCode.BadRequest, "Invalid email format")
            return@post
        }
        if (request.password.length < 8) {
            call.respond(HttpStatusCode.BadRequest, "Password must be at least 8 characters")
            return@post
        }
        if (request.phoneNumber.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Phone number is required")
            return@post
        }

        if (authService.emailExists(request.email)) {
            call.respond(HttpStatusCode.Conflict, "Email already exists")
            return@post
        }

        val userId = authService.register(request)
        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(HttpStatusCode.Created, AuthResponse(token, userId))
    }

    post("/api/auth/login") {
        val request = call.receive<LoginRequest>()

        val user = authService.login(request.email, request.password)

        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            return@post
        }

        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(HttpStatusCode.OK, AuthResponse(token, user.id))
    }

    post("/api/auth/forgot-password") {
        val body = call.receive<Map<String, String>>()
        val email = body["email"] ?: return@post call.respond(
            HttpStatusCode.BadRequest, "Email required"
        )

        val exists = authService.emailExists(email)
        if (!exists) {
            // On répond OK même si l'email n'existe pas (sécurité)
            call.respond(HttpStatusCode.OK, "Reset instructions sent")
            return@post
        }

        // En production → envoyer un vrai email
        // Pour l'instant on simule juste
        call.respond(HttpStatusCode.OK, mapOf("message" to "Reset instructions sent to $email"))
    }
}