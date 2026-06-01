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

data class StartCheckInRequest(
    val bookingId: Int
)

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

data class VerifyPassportRequest(
    val checkInId: Int,
    val passportData: PassportData
)

data class BaggageRequest(
    val checkInId: Int,
    val cabinBags: Int,
    val checkedBags: Int,
    val estimatedWeight: Double,
    val hasSpecialItems: Boolean = false
)

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

data class ConfirmCheckInRequest(
    val checkInId: Int
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
