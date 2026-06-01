package com.example.routes

import com.example.models.*
import com.example.services.CheckInService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkInRoutes(checkInService: CheckInService) {
    authenticate {
        // Étape 1 — Démarrer
        post("/api/checkin/start") {
            val request = call.receive<StartCheckInRequest>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            if (!checkInService.bookingBelongsToUser(request.bookingId, tokenUserId)) {
                call.respond(HttpStatusCode.Forbidden, "Booking does not belong to the authenticated user")
                return@post
            }

            val session = checkInService.startCheckIn(request.bookingId)
            call.respond(HttpStatusCode.Created, session)
        }

        // Étape 2 — Vérifier passeport
        post("/api/checkin/passport") {
            try {
                val request = call.receive<VerifyPassportRequest>()
                val principal = call.principal<JWTPrincipal>()
                val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

                if (!checkInService.checkInBelongsToUser(request.checkInId, tokenUserId)) {
                    call.respond(HttpStatusCode.Forbidden, "Check-in session does not belong to the authenticated user")
                    return@post
                }

                val session = checkInService.verifyPassport(request)
                call.respond(HttpStatusCode.OK, session)
            } catch (e: IllegalStateException) {
                // Passport name mismatch or validation error
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Passport verification failed")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Passport verification failed")
            }
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
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            if (!checkInService.checkInBelongsToUser(request.checkInId, tokenUserId)) {
                call.respond(HttpStatusCode.Forbidden, "Check-in session does not belong to the authenticated user")
                return@post
            }

            val session = checkInService.selectSeat(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Étape 4 — Bagages
        post("/api/checkin/baggage") {
            val request = call.receive<BaggageRequest>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            if (!checkInService.checkInBelongsToUser(request.checkInId, tokenUserId)) {
                call.respond(HttpStatusCode.Forbidden, "Check-in session does not belong to the authenticated user")
                return@post
            }

            val session = checkInService.declareBaggage(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Étape 5 — Préférences
        post("/api/checkin/special-requests") {
            val request = call.receive<SpecialRequestsRequest>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            if (!checkInService.checkInBelongsToUser(request.checkInId, tokenUserId)) {
                call.respond(HttpStatusCode.Forbidden, "Check-in session does not belong to the authenticated user")
                return@post
            }

            val session = checkInService.saveSpecialRequests(request)
            call.respond(HttpStatusCode.OK, session)
        }

        // Étape finale — Confirmer l'embarquement
        post("/api/checkin/confirm") {
            val request = call.receive<ConfirmCheckInRequest>()
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("userId")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")

            if (!checkInService.checkInBelongsToUser(request.checkInId, tokenUserId)) {
                call.respond(HttpStatusCode.Forbidden, "Check-in session does not belong to the authenticated user")
                return@post
            }

            val session = checkInService.confirmCheckIn(request.checkInId)
            call.respond(HttpStatusCode.OK, session)
        }
    }
}