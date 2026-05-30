package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val bookings: List<BookingSyncData>,
    val boardingPasses: List<BoardingPassResponse>
)

@Serializable
data class BookingSyncData(
    val id: Int,
    val bookingReference: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val checkInStatus: String
)