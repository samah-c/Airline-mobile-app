package com.example.airline.data.model

data class BoardingPassModel(
    val flightNumber: String = "",
    val gate: String = "",
    val origin: String = "",
    val originCity: String = "",
    val destination: String = "",
    val destinationCity: String = "",
    val passengerName: String = "",
    val seat: String = "",
    val boardingTime: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val barcode: String = ""
)
