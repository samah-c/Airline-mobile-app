package com.example.airline.data.repository

import com.example.airline.data.model.BoardingPassModel

class BoardingPassRepository {
    // Stub — implement network/persistence here
    suspend fun getBoardingPass(flightId: String): BoardingPassModel = BoardingPassModel()
    suspend fun downloadBoardingPass(flightId: String): Boolean = true
}
