package com.example.airline.ui.flighthistory

data class FlightHistoryUiState(
    val searchQuery: String = "",
    val flights: List<FlightItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class FlightItem(
    val id: String,
    val flightNumber: String,
    val date: String,
    val departureTime: String,
    val departureAirport: String,
    val arrivalTime: String,
    val arrivalAirport: String,
    val duration: String,
    val cabinClass: String,
    val year: Int
)