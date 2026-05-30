package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.airline.utils.MrzData
import com.example.airline.data.repository.CheckInRepository

data class CheckInUiState(
    val lastName: String = "",
    val firstName: String = "",
    val dob: String = "",
    val gender: String = "",
    val nationality: String = "",
    val passportNumber: String = "",
    val issueDate: String = "",
    val expirationDate: String = "",
    val issuingAuthority: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val selectedMeal: String = "",
    val wheelchairAssistance: Boolean = false,
    val visualImpairment: Boolean = false,
    val hearingImpairment: Boolean = false,
    val medicalEquipmentService: Boolean = false,
    val infantOnLap: Boolean = false,
    val numberOfInfants: Int = 0,
    val travellingWithPet: Boolean = false
)

class CheckInViewModel(private val repository: CheckInRepository = CheckInRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState

    fun initFromMrz(mrz: MrzData) {
        _uiState.value = _uiState.value.copy(
            passportNumber = mrz.passportNumber,
            lastName = mrz.surname,
            firstName = mrz.givenNames,
            nationality = mrz.nationality,
            dob = mrz.birthDate,
            gender = mrz.sex,
            expirationDate = mrz.expiryDate,
            issuingAuthority = mrz.issuingCountry
        )
    }

    fun updateLastName(value: String) { _uiState.value = _uiState.value.copy(lastName = value) }
    fun updateFirstName(value: String) { _uiState.value = _uiState.value.copy(firstName = value) }
    fun updateDob(value: String) { _uiState.value = _uiState.value.copy(dob = value) }
    fun updateGender(value: String) { _uiState.value = _uiState.value.copy(gender = value) }
    fun updateNationality(value: String) { _uiState.value = _uiState.value.copy(nationality = value) }
    fun updatePassportNumber(value: String) { _uiState.value = _uiState.value.copy(passportNumber = value) }
    fun updateIssueDate(value: String) { _uiState.value = _uiState.value.copy(issueDate = value) }
    fun updateExpirationDate(value: String) { _uiState.value = _uiState.value.copy(expirationDate = value) }
    fun updateIssuingAuthority(value: String) { _uiState.value = _uiState.value.copy(issuingAuthority = value) }
    fun updateEmail(value: String) { _uiState.value = _uiState.value.copy(email = value) }
    fun updatePhoneNumber(value: String) { _uiState.value = _uiState.value.copy(phoneNumber = value) }

    fun updateSelectedMeal(value: String) { _uiState.value = _uiState.value.copy(selectedMeal = value) }

    fun toggleWheelchairAssistance() { _uiState.value = _uiState.value.copy(wheelchairAssistance = !_uiState.value.wheelchairAssistance) }
    fun toggleVisualImpairment() { _uiState.value = _uiState.value.copy(visualImpairment = !_uiState.value.visualImpairment) }
    fun toggleHearingImpairment() { _uiState.value = _uiState.value.copy(hearingImpairment = !_uiState.value.hearingImpairment) }
    fun toggleMedicalEquipmentService() { _uiState.value = _uiState.value.copy(medicalEquipmentService = !_uiState.value.medicalEquipmentService) }

    fun setInfantOnLap(value: Boolean) { _uiState.value = _uiState.value.copy(infantOnLap = value) }
    fun incrementInfants() { _uiState.value = _uiState.value.copy(numberOfInfants = _uiState.value.numberOfInfants + 1) }
    fun decrementInfants() { _uiState.value = _uiState.value.copy(numberOfInfants = (_uiState.value.numberOfInfants - 1).coerceAtLeast(0)) }
    fun setTravellingWithPet(value: Boolean) { _uiState.value = _uiState.value.copy(travellingWithPet = value) }

    class Factory(private val repository: CheckInRepository = CheckInRepository()) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CheckInViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CheckInViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
