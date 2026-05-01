package com.example.routes

import com.example.models.FlightLookupRequest
import com.example.services.FlightService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.flightRoutes(flightService: FlightService) {

    authenticate {
        post("/api/flights/lookup") {
            val request = call.receive<FlightLookupRequest>()

            if (request.bookingReference.isBlank() || request.lastName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Booking reference and last name required")
                return@post
            }

            val result = flightService.lookupFlight(
                request.bookingReference,
                request.lastName
            )

            if (result == null) {
                call.respond(HttpStatusCode.NotFound, "Booking not found")
                return@post
            }

            call.respond(HttpStatusCode.OK, result)
        }
    }
}