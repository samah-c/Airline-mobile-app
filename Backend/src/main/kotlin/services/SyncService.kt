package com.example.services

import com.example.models.BoardingPassResponse
import com.example.models.BookingSyncData
import com.example.models.SyncResponse
import com.example.schemas.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll

class SyncService(private val database: Database) {

    suspend fun syncForUser(userId: Int): SyncResponse = dbQuery {

        // Récupérer tous les bookings de l'user
        val bookings = Bookings.selectAll()
            .where { Bookings.passengerId eq userId }
            .map { booking ->
                val flight = Flights.selectAll()
                    .where { Flights.id eq booking[Bookings.flightId] }
                    .single()

                BookingSyncData(
                    id = booking[Bookings.id],
                    bookingReference = booking[Bookings.bookingReference],
                    flightNumber = flight[Flights.flightNumber],
                    origin = flight[Flights.origin],
                    destination = flight[Flights.destination],
                    departureTime = flight[Flights.departureTime],
                    checkInStatus = booking[Bookings.checkInStatus]
                )
            }

        // Récupérer tous les boarding passes de l'user
        val boardingPasses = BoardingPasses.innerJoin(CheckIns)
            .innerJoin(Bookings)
            .selectAll()
            .where { Bookings.passengerId eq userId }
            .map {
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

        SyncResponse(bookings, boardingPasses)
    }
}