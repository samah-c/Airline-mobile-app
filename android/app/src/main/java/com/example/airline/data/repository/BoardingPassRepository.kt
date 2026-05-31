package com.example.airline.data.repository

import com.example.airline.data.model.BoardingPassModel
import com.example.airline.network.GenerateBoardingPassRequest
import com.example.airline.network.RetrofitClient

class BoardingPassRepository {

    private fun mapResponse(bp: com.example.airline.network.BoardingPassResponse) = BoardingPassModel(
        flightNumber    = bp.flightNumber,
        gate            = bp.gate,
        origin          = bp.origin,
        originCity      = bp.origin,
        destination     = bp.destination,
        destinationCity = bp.destination,
        passengerName   = bp.passengerName,
        seat            = bp.seatNumber,
        seatClass       = bp.seatClass,
        boardingTime    = bp.departureTime,
        departureTime   = bp.departureTime,
        arrivalTime     = "",
        barcode         = bp.bookingReference,
        qrCode          = bp.qrCode
    )

    suspend fun getBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.getBoardingPass(checkInId)
        return if (response.isSuccessful) response.body()?.let { mapResponse(it) } else null
    }

    suspend fun generateBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.generateBoardingPass(GenerateBoardingPassRequest(checkInId))
        return if (response.isSuccessful) response.body()?.let { mapResponse(it) } else null
    }

    suspend fun downloadPdf(checkInId: Int): ByteArray? {
        val response = RetrofitClient.api.downloadBoardingPassPdf(checkInId)
        return if (response.isSuccessful) response.body()?.bytes() else null
    }
}
