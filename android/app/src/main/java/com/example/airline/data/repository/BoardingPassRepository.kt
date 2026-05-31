package com.example.airline.data.repository

import com.example.airline.data.model.BoardingPassModel
import com.example.airline.network.GenerateBoardingPassRequest
import com.example.airline.network.RetrofitClient

class BoardingPassRepository {

    suspend fun getBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.getBoardingPass(checkInId)
        if (!response.isSuccessful) return null
        return response.body()?.let { bp ->
            BoardingPassModel(
                flightNumber    = bp.flightNumber,
                gate            = bp.gate,
                origin          = bp.origin,
                originCity      = bp.origin,
                destination     = bp.destination,
                destinationCity = bp.destination,
                passengerName   = bp.passengerName,
                seat            = bp.seatNumber,
                boardingTime    = bp.departureTime,
                departureTime   = bp.departureTime,
                arrivalTime     = "",
                barcode         = bp.bookingReference
            )
        }
    }

    suspend fun generateBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.generateBoardingPass(GenerateBoardingPassRequest(checkInId))
        if (!response.isSuccessful) return null
        return response.body()?.let { bp ->
            BoardingPassModel(
                flightNumber    = bp.flightNumber,
                gate            = bp.gate,
                origin          = bp.origin,
                originCity      = bp.origin,
                destination     = bp.destination,
                destinationCity = bp.destination,
                passengerName   = bp.passengerName,
                seat            = bp.seatNumber,
                boardingTime    = bp.departureTime,
                departureTime   = bp.departureTime,
                arrivalTime     = "",
                barcode         = bp.bookingReference
            )
        }
    }
}
