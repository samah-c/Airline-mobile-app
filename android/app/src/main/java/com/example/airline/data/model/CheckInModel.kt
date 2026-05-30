package com.example.airline.data.model

import com.example.airline.data.model.PassengerModel
import com.example.airline.data.model.PassportModel

data class CheckInModel(
    val passenger: PassengerModel = PassengerModel(),
    val passport: PassportModel = PassportModel(),
    val selectedMeal: String = "",
    val wheelchairAssistance: Boolean = false,
    val visualImpairment: Boolean = false,
    val hearingImpairment: Boolean = false,
    val medicalEquipmentService: Boolean = false,
    val infantOnLap: Boolean = false,
    val numberOfInfants: Int = 0,
    val travellingWithPet: Boolean = false
)
