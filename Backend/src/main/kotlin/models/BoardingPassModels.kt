package com.example.models

import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
data class GenerateBoardingPassRequest(
    val checkInId: Int
)