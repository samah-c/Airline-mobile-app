package com.example.airline.network

data class SeatResponse(
    val id: Int,
    val flightId: Int,
    val seatNumber: String,
    val seatClass: String,
    val isOccupied: Boolean
)

data class SeatSelectionRequest(
    val checkInId: Int,
    val seatNumber: String
)

data class CheckInSession(
    val id: Int,
    val bookingId: Int,
    val status: String,
    val passportVerified: Boolean,
    val seatNumber: String?,
    val cabinBags: Int,
    val checkedBags: Int
)

data class BoardingPassResponse(
    val id: Int,
    val bookingReference: String,
    val passengerName: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val seatNumber: String,
    val seatClass: String,
    val gate: String,
    val qrCode: String,
    val status: String
)

data class GenerateBoardingPassRequest(
    val checkInId: Int
)
