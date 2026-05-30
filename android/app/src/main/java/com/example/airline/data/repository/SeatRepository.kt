package com.example.airline.data.repository

import com.example.airline.data.model.SeatModel

class SeatRepository {
    // Stub — implement network/persistence here
    suspend fun getSeats(flightId: String): List<SeatModel> = emptyList()
    suspend fun reserveSeat(flightId: String, seatId: String): Boolean = true
}
