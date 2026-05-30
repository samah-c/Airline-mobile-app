package com.example.services

import com.example.models.BoardingPassResponse
import com.example.schemas.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import java.util.Base64
import java.util.UUID

class BoardingPassService(
    private val database: Database,
    private val notificationService: NotificationService
) {

    suspend fun generate(checkInId: Int): BoardingPassResponse {

        // 1. Tout ce qui est DB dans dbQuery
        val (boardingPass, userId, boardingPassId) = dbQuery {

            val checkIn = CheckIns.selectAll()
                .where { CheckIns.id eq checkInId }
                .singleOrNull() ?: error("CheckIn not found")

            if (checkIn[CheckIns.status] != "COMPLETED") {
                error("Check-in not completed yet")
            }

            val booking = Bookings.selectAll()
                .where { Bookings.id eq checkIn[CheckIns.bookingId] }
                .singleOrNull() ?: error("Booking not found")

            val flight = Flights.selectAll()
                .where { Flights.id eq booking[Bookings.flightId] }
                .singleOrNull() ?: error("Flight not found")

            val user = Users.selectAll()
                .where { Users.id eq booking[Bookings.passengerId] }
                .singleOrNull() ?: error("User not found")

            val seatNumber = checkIn[CheckIns.seatNumber] ?: "N/A"
            val seatClass = if (seatNumber.firstOrNull()?.digitToIntOrNull() ?: 3 <= 2)
                "FIRST" else "ECONOMY"

            val qrData = """
                BOARDING PASS
                Passenger: ${user[Users.name]}
                Flight: ${flight[Flights.flightNumber]}
                From: ${flight[Flights.origin]}
                To: ${flight[Flights.destination]}
                Departure: ${flight[Flights.departureTime]}
                Seat: $seatNumber
                Ref: ${booking[Bookings.bookingReference]}
                ID: ${UUID.randomUUID()}
            """.trimIndent()

            val qrCode = Base64.getEncoder().encodeToString(qrData.toByteArray())
            val gates = listOf("A1", "A2", "B3", "B4", "C5", "C6", "D7", "D8")
            val gate = gates.random()

            val existing = BoardingPasses.selectAll()
                .where { BoardingPasses.checkInId eq checkInId }
                .singleOrNull()

            val bpId = if (existing != null) {
                existing[BoardingPasses.id]
            } else {
                BoardingPasses.insert {
                    it[BoardingPasses.checkInId] = checkInId
                    it[bookingReference] = booking[Bookings.bookingReference]
                    it[passengerName] = user[Users.name]
                    it[flightNumber] = flight[Flights.flightNumber]
                    it[origin] = flight[Flights.origin]
                    it[destination] = flight[Flights.destination]
                    it[departureTime] = flight[Flights.departureTime]
                    it[BoardingPasses.seatNumber] = seatNumber
                    it[BoardingPasses.seatClass] = seatClass
                    it[BoardingPasses.gate] = gate
                    it[BoardingPasses.qrCode] = qrCode
                }[BoardingPasses.id]
            }

            Triple(
                BoardingPassResponse(
                    id = bpId,
                    bookingReference = booking[Bookings.bookingReference],
                    passengerName = user[Users.name],
                    flightNumber = flight[Flights.flightNumber],
                    origin = flight[Flights.origin],
                    destination = flight[Flights.destination],
                    departureTime = flight[Flights.departureTime],
                    seatNumber = seatNumber,
                    seatClass = seatClass,
                    gate = gate,
                    qrCode = qrCode,
                    status = "VALID"
                ),
                booking[Bookings.passengerId],
                bpId
            )
        }

        // 2. Notification en dehors de dbQuery ✅
        try {
            notificationService.sendBoardingPassReady(
                userId = userId,
                boardingPassId = boardingPassId
            )
        } catch (e: Exception) {
            println("Notification failed: ${e.message}")
        }

        return boardingPass
    }

    suspend fun getByCheckIn(checkInId: Int): BoardingPassResponse? = dbQuery {
        BoardingPasses.selectAll()
            .where { BoardingPasses.checkInId eq checkInId }
            .singleOrNull()?.let {
                BoardingPassResponse(
                    id = it[BoardingPasses.id],
                    bookingReference = it[BoardingPasses.bookingReference],
                    passengerName = it[BoardingPasses.passengerName],
                    flightNumber = it[BoardingPasses.flightNumber],
                    origin = it[BoardingPasses.origin],
                    destination = it[BoardingPasses.destination],
                    departureTime = it[BoardingPasses.departureTime],
                    seatNumber = it[BoardingPasses.seatNumber],
                    seatClass = it[BoardingPasses.seatClass],
                    gate = it[BoardingPasses.gate],
                    qrCode = it[BoardingPasses.qrCode],
                    status = it[BoardingPasses.status]
                )
            }
    }
}