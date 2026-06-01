package com.example.airline.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.ui.checkin.CheckInUiState
import com.example.airline.ui.checkin.CheckInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServicesUiState(
    val selectedMeal: String = "Standard meal",
    val wheelchairAssistance: Boolean = false,
    val visualImpairment: Boolean = false,
    val hearingImpairment: Boolean = false,
    val medicalEquipmentService: Boolean = false,
    val infantOnLap: Boolean = false,
    val numberOfInfants: Int = 0,
    val travellingWithPet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ServicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    private var hasInitialized = false

    fun initializeFrom(checkInState: CheckInUiState) {
        if (hasInitialized) return
        hasInitialized = true
        _uiState.update {
            it.copy(
                selectedMeal = checkInState.selectedMeal.ifEmpty { "Standard meal" },
                wheelchairAssistance = checkInState.wheelchairAssistance,
                visualImpairment = checkInState.visualImpairment,
                hearingImpairment = checkInState.hearingImpairment,
                medicalEquipmentService = checkInState.medicalEquipmentService,
                infantOnLap = checkInState.infantOnLap,
                numberOfInfants = checkInState.numberOfInfants,
                travellingWithPet = checkInState.travellingWithPet
            )
        }
    }

    fun updateSelectedMeal(value: String) { _uiState.update { it.copy(selectedMeal = value) } }
    fun toggleWheelchairAssistance() { _uiState.update { it.copy(wheelchairAssistance = !it.wheelchairAssistance) } }
    fun toggleVisualImpairment() { _uiState.update { it.copy(visualImpairment = !it.visualImpairment) } }
    fun toggleHearingImpairment() { _uiState.update { it.copy(hearingImpairment = !it.hearingImpairment) } }
    fun toggleMedicalEquipmentService() { _uiState.update { it.copy(medicalEquipmentService = !it.medicalEquipmentService) } }
    fun setInfantOnLap(value: Boolean) { _uiState.update { it.copy(infantOnLap = value) } }
    fun incrementInfants() { _uiState.update { it.copy(numberOfInfants = it.numberOfInfants + 1) } }
    fun decrementInfants() { _uiState.update { it.copy(numberOfInfants = (it.numberOfInfants - 1).coerceAtLeast(0)) } }
    fun setTravellingWithPet(value: Boolean) { _uiState.update { it.copy(travellingWithPet = value) } }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private fun setError(message: String?) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }

    fun submitSpecialRequests(checkInViewModel: CheckInViewModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            setError(null)
            try {
                val currentState = _uiState.value
                checkInViewModel.updateSelectedMeal(currentState.selectedMeal)
                checkInViewModel.setWheelchairAssistance(currentState.wheelchairAssistance)
                checkInViewModel.setVisualImpairment(currentState.visualImpairment)
                checkInViewModel.setHearingImpairment(currentState.hearingImpairment)
                checkInViewModel.setMedicalEquipmentService(currentState.medicalEquipmentService)
                checkInViewModel.setInfantOnLap(currentState.infantOnLap)
                checkInViewModel.setNumberOfInfants(currentState.numberOfInfants)
                checkInViewModel.setTravellingWithPet(currentState.travellingWithPet)

                checkInViewModel.submitSpecialRequestsAsync()
                onSuccess()
            } catch (e: Exception) {
                setError(e.message ?: "Submission failed")
            } finally {
                setLoading(false)
            }
        }
    }
}
