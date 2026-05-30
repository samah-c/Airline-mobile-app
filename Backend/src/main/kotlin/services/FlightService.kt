package com.example.services

import com.example.models.Booking
import com.example.models.Flight
import com.example.models.FlightLookupResponse
import com.example.schemas.Bookings
import com.example.schemas.Flights
import com.example.schemas.dbQuery
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class FlightService(private val database: Database) {

    suspend fun lookupFlight(
        bookingReference: String,
        lastName: String
    ): FlightLookupResponse? = dbQuery {

        // Chercher la réservation
        val bookingRow = Bookings.selectAll()
            .where {
                (Bookings.bookingReference eq bookingReference) and
                        (Bookings.passengerLastName eq lastName)
            }
            .singleOrNull() ?: return@dbQuery null

        // Chercher le vol associé
        val flightRow = Flights.selectAll()
            .where { Flights.id eq bookingRow[Bookings.flightId] }
            .singleOrNull() ?: return@dbQuery null

        val flight = Flight(
            id = flightRow[Flights.id],
            flightNumber = flightRow[Flights.flightNumber],
            origin = flightRow[Flights.origin],
            destination = flightRow[Flights.destination],
            departureTime = flightRow[Flights.departureTime],
            arrivalTime = flightRow[Flights.arrivalTime],
            availableSeats = flightRow[Flights.availableSeats]
        )

        val booking = Booking(
            id = bookingRow[Bookings.id],
            bookingReference = bookingRow[Bookings.bookingReference],
            passengerId = bookingRow[Bookings.passengerId],
            flightId = bookingRow[Bookings.flightId],
            passengerLastName = bookingRow[Bookings.passengerLastName],
            checkInStatus = bookingRow[Bookings.checkInStatus]
        )

        // Vérifier si check-in disponible (24h avant départ)
        val departure = LocalDateTime.parse(flight.departureTime)
        val now = LocalDateTime.now()
        val canCheckIn = now.isAfter(departure.minusHours(24)) &&
                now.isBefore(departure)

        FlightLookupResponse(booking, flight, canCheckIn)
    }

    suspend fun getFlightHistory(userId: Int): List<FlightLookupResponse> = dbQuery {
        Bookings.selectAll()
            .where { Bookings.passengerId eq userId }
            .mapNotNull { booking ->
                val flight = Flights.selectAll()
                    .where { Flights.id eq booking[Bookings.flightId] }
                    .singleOrNull() ?: return@mapNotNull null

                val departure = LocalDateTime.parse(booking[Bookings.flightId].let {
                    Flights.selectAll().where { Flights.id eq it }.single()[Flights.departureTime]
                })
                val now = LocalDateTime.now()
                val canCheckIn = now.isAfter(departure.minusHours(24)) &&
                        now.isBefore(departure)

                FlightLookupResponse(
                    booking = Booking(
                        id = booking[Bookings.id],
                        bookingReference = booking[Bookings.bookingReference],
                        passengerId = booking[Bookings.passengerId],
                        flightId = booking[Bookings.flightId],
                        passengerLastName = booking[Bookings.passengerLastName],
                        checkInStatus = booking[Bookings.checkInStatus]
                    ),
                    flight = Flight(
                        id = flight[Flights.id],
                        flightNumber = flight[Flights.flightNumber],
                        origin = flight[Flights.origin],
                        destination = flight[Flights.destination],
                        departureTime = flight[Flights.departureTime],
                        arrivalTime = flight[Flights.arrivalTime],
                        availableSeats = flight[Flights.availableSeats]
                    ),
                    canCheckIn = canCheckIn
                )
            }
    }
}