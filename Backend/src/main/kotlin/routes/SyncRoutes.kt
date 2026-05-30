package com.example.routes

import com.example.services.SyncService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.syncRoutes(syncService: SyncService) {
    authenticate {
        get("/api/sync/{userId}") {
            val userId = call.parameters["userId"]?.toInt()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val syncData = syncService.syncForUser(userId)
            call.respond(HttpStatusCode.OK, syncData)
        }
    }
}