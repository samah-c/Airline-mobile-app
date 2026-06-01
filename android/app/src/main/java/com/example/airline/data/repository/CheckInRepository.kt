package com.example.airline.data.repository

import com.example.airline.network.BaggageRequest
import com.example.airline.network.CheckInSession
import com.example.airline.network.PassportData
import com.example.airline.network.RetrofitClient
import com.example.airline.network.ConfirmCheckInRequest
import com.example.airline.network.SpecialRequestsRequest
import com.example.airline.network.StartCheckInRequest
import com.example.airline.network.VerifyPassportRequest

class CheckInRepository {

    suspend fun startCheckIn(bookingId: Int): CheckInSession {
        val response = RetrofitClient.api.startCheckIn(StartCheckInRequest(bookingId))
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Empty start check-in response")
        }
        throw IllegalStateException("Failed to start check-in: ${response.code()} ${response.message()}")
    }

    suspend fun verifyPassport(checkInId: Int, passportData: PassportData): CheckInSession {
        val response = RetrofitClient.api.verifyPassport(VerifyPassportRequest(checkInId, passportData))
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Empty verify passport response")
        }
        throw IllegalStateException("Failed to verify passport: ${response.code()} ${response.message()}")
    }

    suspend fun declareBaggage(
        checkInId: Int,
        cabinBags: Int,
        checkedBags: Int,
        estimatedWeight: Double,
        hasSpecialItems: Boolean
    ): CheckInSession {
        val response = RetrofitClient.api.declareBaggage(
            BaggageRequest(
                checkInId = checkInId,
                cabinBags = cabinBags,
                checkedBags = checkedBags,
                estimatedWeight = estimatedWeight,
                hasSpecialItems = hasSpecialItems
            )
        )
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Empty baggage declaration response")
        }
        throw IllegalStateException("Failed to declare baggage: ${response.code()} ${response.message()}")
    }

    suspend fun submitSpecialRequests(
        checkInId: Int,
        dietaryPreference: String,
        needsWheelchair: Boolean,
        needsVisualAssistance: Boolean,
        needsHearingAssistance: Boolean,
        needsMedicalEquipment: Boolean,
        travellingWithInfant: Boolean,
        travellingWithPet: Boolean
    ): CheckInSession {
        val response = RetrofitClient.api.submitSpecialRequests(
            SpecialRequestsRequest(
                checkInId = checkInId,
                dietaryPreference = dietaryPreference,
                needsWheelchair = needsWheelchair,
                needsVisualAssistance = needsVisualAssistance,
                needsHearingAssistance = needsHearingAssistance,
                needsMedicalEquipment = needsMedicalEquipment,
                travellingWithInfant = travellingWithInfant,
                travellingWithPet = travellingWithPet
            )
        )
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Empty special requests response")
        }
        throw IllegalStateException("Failed to submit special requests: ${response.code()} ${response.message()}")
    }

    suspend fun confirmCheckIn(checkInId: Int): CheckInSession {
        val response = RetrofitClient.api.confirmCheckIn(ConfirmCheckInRequest(checkInId))
        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Empty confirm response")
        }
        throw IllegalStateException("Failed to confirm check-in: ${response.code()} ${response.message()}")
    }
}

