package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Flight(
    val id: Int,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val availableSeats: Int
)

@Serializable
data class Booking(
    val id: Int,
    val bookingReference: String,
    val passengerId: Int,
    val flightId: Int,
    val passengerLastName: String,
    val checkInStatus: String
)

@Serializable
data class FlightLookupRequest(
    val bookingReference: String,
    val lastName: String
)

@Serializable
data class FlightLookupResponse(
    val booking: Booking,
    val flight: Flight,
    val canCheckIn: Boolean
)