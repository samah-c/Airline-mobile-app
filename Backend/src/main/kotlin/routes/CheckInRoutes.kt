package com.example.routes

import com.example.models.*
import com.example.services.CheckInService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkInRoutes(checkInService: CheckInService) {
    // TODO: restore authenticate { } after login is wired to real backend

        // Étape 1 — Démarrer
        post("/api/checkin/start") {
            val request = call.receive<StartCheckInRequest>()
            val session = checkInService.startCheckIn(request.bookingId)
            call.respond(HttpStatusCode.Created, session)
        }

        // Étape 2 — Vérifier passeport
        post("/api/checkin/passport") {
            val request = call.receive<VerifyPassportRequest>()
            val session = checkInService.verifyPassport(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Récupérer les sièges disponibles
        get("/api/checkin/seats/{flightId}") {
            val flightId = call.parameters["flightId"]?.toInt()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid flight ID")
            val seats = checkInService.getAvailableSeats(flightId)
            call.respond(HttpStatusCode.OK, seats)
        }

        // Étape 3 — Choisir siège
        post("/api/checkin/seat") {
            val request = call.receive<SeatSelectionRequest>()
            val session = checkInService.selectSeat(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Étape 4 — Bagages
        post("/api/checkin/baggage") {
            val request = call.receive<BaggageRequest>()
            val session = checkInService.declareBaggage(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Étape 5 — Préférences
        post("/api/checkin/special-requests") {
            val request = call.receive<SpecialRequestsRequest>()
            val session = checkInService.saveSpecialRequests(request)
            call.respond(HttpStatusCode.OK, session)
        }
}