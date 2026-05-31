package com.example.airline.ui.flightlookup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class FlightLookUpResultUiState(
    val checkInStatus: String = "Not Checked In",
    val flightNumber: String = "Flight LH007",
    val departureDate: String = "Jan 20",
    val departureTime: String = "09:50",
    val departureAirport: String = "BOM",
    val arrivalDate: String = "Jan 21",
    val arrivalTime: String = "15:38",
    val arrivalAirport: String = "USA",
    val duration: String = "14h20m",
    val cabinClass: String = "Economy"
)

@HiltViewModel
class FlightLookUpResultViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FlightLookUpResultUiState())
    val uiState: StateFlow<FlightLookUpResultUiState> = _uiState.asStateFlow()

    fun loadFlight(pnr: String, lastName: String) {
        // TODO: fetch real flight data using pnr + lastName from your repository
        // _uiState.update { ... }
    }
}