package com.example.airline.data.repository

import com.example.airline.data.model.SeatModel
import com.example.airline.data.model.SeatClassType
import com.example.airline.data.model.SeatStateType
import com.example.airline.network.RetrofitClient
import com.example.airline.network.SeatSelectionRequest

class SeatRepository {

    suspend fun getSeats(flightId: Int): List<SeatModel> {
        val response = RetrofitClient.api.getAvailableSeats(flightId)
        if (!response.isSuccessful) return emptyList()
        return response.body()?.map { seat ->
            SeatModel(
                id    = seat.seatNumber,
                seatClass = if (seat.seatClass.uppercase() == "FIRST") SeatClassType.FIRST else SeatClassType.ECONOMY,
                state = if (seat.isOccupied) SeatStateType.OCCUPIED else SeatStateType.AVAILABLE
            )
        } ?: emptyList()
    }

    suspend fun selectSeat(checkInId: Int, seatNumber: String): Boolean {
        val response = RetrofitClient.api.selectSeat(SeatSelectionRequest(checkInId, seatNumber))
        return response.isSuccessful
    }
}
