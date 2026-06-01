package com.example.airline.data.retrofit

data class FlightLookupResponse(
    val booking: BookingDto,
    val flight: FlightDto,
    val canCheckIn: Boolean
)

data class BookingDto(
    val id: Int,
    val bookingReference: String,
    val passengerId: Int,
    val flightId: Int,
    val passengerLastName: String,
    val checkInStatus: String
)

data class FlightDto(
    val id: Int,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,  // format: "2025-12-21T09:00:00"
    val arrivalTime: String,
    val availableSeats: Int
)