package com.example.airline.ui.flighthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FlightHistoryUiState())
    val uiState: StateFlow<FlightHistoryUiState> = _uiState.asStateFlow()

    init {
        loadFlightHistory()
    }

    fun loadFlightHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Données mockées - chargement immédiat sans delay pour le test
            val mockFlights = listOf(
                FlightItem(
                    id = "1",
                    flightNumber = "LHO44",
                    date = "Thu, Jan 20, 2026",
                    departureTime = "09:50",
                    departureAirport = "BOM",
                    arrivalTime = "15:38",
                    arrivalAirport = "USA",
                    duration = "14h20m",
                    cabinClass = "Economy",
                    year = 2026
                ),
                FlightItem(
                    id = "2",
                    flightNumber = "NR007",
                    date = "Mon, Dec 21, 2025",
                    departureTime = "09:00",
                    departureAirport = "ALG",
                    arrivalTime = "10:10",
                    arrivalAirport = "CDG",
                    duration = "1h10m",
                    cabinClass = "Economy",
                    year = 2025
                ),
                FlightItem(
                    id = "3",
                    flightNumber = "MV33",
                    date = "Sun, Oct 04, 2025",
                    departureTime = "07:40",
                    departureAirport = "ALG",
                    arrivalTime = "10:00",
                    arrivalAirport = "IST",
                    duration = "1h50m",
                    cabinClass = "Economy",
                    year = 2025
                )
            )

            // Petit delay pour simuler le chargement réseau
            delay(300)

            _uiState.update {
                it.copy(
                    flights = mockFlights,
                    isLoading = false
                )
            }
            println("DEBUG: Loading ${mockFlights.size} flights")
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun filterFlights(): List<FlightItem> {
        val query = _uiState.value.searchQuery.trim().lowercase()
        return if (query.isEmpty()) {
            _uiState.value.flights
        } else {
            _uiState.value.flights.filter {
                it.flightNumber.lowercase().contains(query) ||
                        it.departureAirport.lowercase().contains(query) ||
                        it.arrivalAirport.lowercase().contains(query)
            }
        }
    }
}