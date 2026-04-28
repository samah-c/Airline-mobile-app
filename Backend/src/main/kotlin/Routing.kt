package com.example

import com.example.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Airline Check-In API")
        }
        authRoutes(authService)
    }
}