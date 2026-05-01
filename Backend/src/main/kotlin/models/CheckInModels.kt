package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class StartCheckInRequest(
    val bookingId: Int
)

@Serializable
data class PassportData(
    val lastName: String,
    val firstName: String,
    val dateOfBirth: String,
    val gender: String,
    val nationality: String,
    val passportNumber: String,
    val issueDate: String,
    val expirationDate: String,
    val issuingAuthority: String
)

@Serializable
data class VerifyPassportRequest(
    val checkInId: Int,
    val passportData: PassportData
)

@Serializable
data class SeatSelectionRequest(
    val checkInId: Int,
    val seatNumber: String
)

@Serializable
data class Seat(
    val id: Int,
    val flightId: Int,
    val seatNumber: String,
    val seatClass: String,
    val isOccupied: Boolean
)

@Serializable
data class BaggageRequest(
    val checkInId: Int,
    val cabinBags: Int,
    val checkedBags: Int,
    val estimatedWeight: Double,
    val hasSpecialItems: Boolean = false
)

@Serializable
data class SpecialRequestsRequest(
    val checkInId: Int,
    val dietaryPreference: String = "STANDARD",
    val needsWheelchair: Boolean = false,
    val needsVisualAssistance: Boolean = false,
    val needsHearingAssistance: Boolean = false,
    val needsMedicalEquipment: Boolean = false,
    val travellingWithInfant: Boolean = false,
    val travellingWithPet: Boolean = false
)

@Serializable
data class CheckInSession(
    val id: Int,
    val bookingId: Int,
    val status: String,
    val passportVerified: Boolean,
    val seatNumber: String?,
    val cabinBags: Int,
    val checkedBags: Int
)