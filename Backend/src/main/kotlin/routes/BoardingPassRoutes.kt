package com.example.routes

import com.example.models.GenerateBoardingPassRequest
import com.example.services.BoardingPassService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.boardingPassRoutes(boardingPassService: BoardingPassService) {
    authenticate {
        // Générer le boarding pass
        post("/api/boarding-pass/generate") {
            val request = call.receive<GenerateBoardingPassRequest>()
            try {
                val boardingPass = boardingPassService.generate(request.checkInId)
                call.respond(HttpStatusCode.Created, boardingPass)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Error")
            }
        }

        // Récupérer le boarding pass
        get("/api/boarding-pass/{checkInId}") {
            val checkInId = call.parameters["checkInId"]?.toInt()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val boardingPass = boardingPassService.getByCheckIn(checkInId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Boarding pass not found")

            call.respond(HttpStatusCode.OK, boardingPass)
        }

        // Télécharger le PDF
        get("/api/boarding-pass/{checkInId}/pdf") {
            val checkInId = call.parameters["checkInId"]?.toInt()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val boardingPass = boardingPassService.getByCheckIn(checkInId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Boarding pass not found")

            val pdfBytes = boardingPassService.generatePdf(boardingPass)

            call.response.header(
                "Content-Disposition",
                "attachment; filename=\"boarding-pass-${boardingPass.bookingReference}.pdf\""
            )
            call.respondBytes(pdfBytes, contentType = io.ktor.http.ContentType.Application.Pdf)
        }
    }
}