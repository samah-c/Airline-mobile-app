package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.CheckInRepository
import com.example.airline.network.CheckInSession
import com.example.airline.network.PassportData
import com.example.airline.utils.MrzData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val bookingId: Int = 2,
    val checkInSessionId: Int? = null,
    val isPassportVerified: Boolean = false,
    val selectedMeal: String = "",
    val wheelchairAssistance: Boolean = false,
    val visualImpairment: Boolean = false,
    val hearingImpairment: Boolean = false,
    val medicalEquipmentService: Boolean = false,
    val infantOnLap: Boolean = false,
    val numberOfInfants: Int = 0,
    val travellingWithPet: Boolean = false,
    val seatNumber: String? = null,
    val cabinBags: Int = 0,
    val checkedBags: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CheckInViewModel(private val repository: CheckInRepository = CheckInRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private fun updateLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }

    private fun updateSession(session: CheckInSession) {
        _uiState.update {
            it.copy(
                checkInSessionId = session.id,
                isPassportVerified = session.passportVerified,
                seatNumber = session.seatNumber ?: it.seatNumber,
                cabinBags = session.cabinBags,
                checkedBags = session.checkedBags,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private suspend fun createSessionIfNeeded(): Int {
        val existing = _uiState.value.checkInSessionId
        if (existing != null) return existing

        val session = repository.startCheckIn(_uiState.value.bookingId)
        updateSession(session)
        return session.id
    }

    fun startCheckIn(bookingId: Int = _uiState.value.bookingId, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            updateLoading(true)
            try {
                _uiState.update { it.copy(bookingId = bookingId) }
                val session = repository.startCheckIn(bookingId)
                updateSession(session)
                onSuccess?.invoke()
            } catch (e: Exception) {
                val message = e.message ?: "Unable to start check-in"
                setError(message)
                onError?.invoke(message)
            }
        }
    }

    suspend fun verifyPassportAsync() {
        updateLoading(true)
        try {
            val checkInId = createSessionIfNeeded()
            val passportData = PassportData(
                lastName = _uiState.value.lastName,
                firstName = _uiState.value.firstName,
                dateOfBirth = _uiState.value.dob,
                gender = _uiState.value.gender,
                nationality = _uiState.value.nationality,
                passportNumber = _uiState.value.passportNumber,
                issueDate = _uiState.value.issueDate,
                expirationDate = _uiState.value.expirationDate,
                issuingAuthority = _uiState.value.issuingAuthority
            )
            val session = repository.verifyPassport(checkInId, passportData)
            updateSession(session)
        } catch (e: Exception) {
            val message = e.message ?: "Unable to verify passport"
            setError(message)
            throw e
        } finally {
            updateLoading(false)
        }
    }

    fun verifyPassport(onSuccess: () -> Unit, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                verifyPassportAsync()
                onSuccess()
            } catch (e: Exception) {
                onError?.invoke(e.message ?: "Unable to verify passport")
            }
        }
    }

    fun declareBaggage(
        cabinBags: Int,
        checkedBags: Int,
        estimatedWeight: Double,
        hasSpecialItems: Boolean,
        onSuccess: () -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            updateLoading(true)
            try {
                val checkInId = createSessionIfNeeded()
                val session = repository.declareBaggage(checkInId, cabinBags, checkedBags, estimatedWeight, hasSpecialItems)
                updateSession(session)
                onSuccess()
            } catch (e: Exception) {
                val message = e.message ?: "Unable to declare baggage"
                setError(message)
                onError?.invoke(message)
            }
        }
    }

    suspend fun submitSpecialRequestsAsync() {
        updateLoading(true)
        try {
            val checkInId = createSessionIfNeeded()
            val session = repository.submitSpecialRequests(
                checkInId = checkInId,
                dietaryPreference = _uiState.value.selectedMeal.ifEmpty { "STANDARD" },
                needsWheelchair = _uiState.value.wheelchairAssistance,
                needsVisualAssistance = _uiState.value.visualImpairment,
                needsHearingAssistance = _uiState.value.hearingImpairment,
                needsMedicalEquipment = _uiState.value.medicalEquipmentService,
                travellingWithInfant = _uiState.value.infantOnLap,
                travellingWithPet = _uiState.value.travellingWithPet
            )
            updateSession(session)
        } catch (e: Exception) {
            val message = e.message ?: "Unable to save service preferences"
            setError(message)
            throw e
        } finally {
            updateLoading(false)
        }
    }

    fun submitSpecialRequests(onSuccess: () -> Unit, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                submitSpecialRequestsAsync()
                onSuccess()
            } catch (e: Exception) {
                onError?.invoke(e.message ?: "Unable to save service preferences")
            }
        }
    }

    suspend fun confirmCheckInAsync() {
        updateLoading(true)
        try {
            val checkInId = createSessionIfNeeded()
            val session = repository.confirmCheckIn(checkInId)
            updateSession(session)
        } catch (e: Exception) {
            throw e
        } finally {
            updateLoading(false)
        }
    }

    fun confirmCheckIn(onSuccess: () -> Unit, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                confirmCheckInAsync()
                onSuccess()
            } catch (e: Exception) {
                setError(e.message ?: "Unable to confirm check-in")
                onError?.invoke(e.message ?: "Unable to confirm check-in")
            }
        }
    }

    fun initFromMrz(mrz: MrzData) {
        _uiState.update {
            it.copy(
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
    }

    fun updateLastName(value: String) { _uiState.update { it.copy(lastName = value) } }
    fun updateFirstName(value: String) { _uiState.update { it.copy(firstName = value) } }
    fun updateDob(value: String) { _uiState.update { it.copy(dob = value) } }
    fun updateGender(value: String) { _uiState.update { it.copy(gender = value) } }
    fun updateNationality(value: String) { _uiState.update { it.copy(nationality = value) } }
    fun updatePassportNumber(value: String) { _uiState.update { it.copy(passportNumber = value) } }
    fun updateIssueDate(value: String) { _uiState.update { it.copy(issueDate = value) } }
    fun updateExpirationDate(value: String) { _uiState.update { it.copy(expirationDate = value) } }
    fun updateIssuingAuthority(value: String) { _uiState.update { it.copy(issuingAuthority = value) } }
    fun updateEmail(value: String) { _uiState.update { it.copy(email = value) } }
    fun updatePhoneNumber(value: String) { _uiState.update { it.copy(phoneNumber = value) } }

    fun updateSelectedMeal(value: String) { _uiState.update { it.copy(selectedMeal = value) } }

    fun toggleWheelchairAssistance() {
        _uiState.update { it.copy(wheelchairAssistance = !it.wheelchairAssistance) }
    }

    fun toggleVisualImpairment() {
        _uiState.update { it.copy(visualImpairment = !it.visualImpairment) }
    }

    fun toggleHearingImpairment() {
        _uiState.update { it.copy(hearingImpairment = !it.hearingImpairment) }
    }

    fun toggleMedicalEquipmentService() {
        _uiState.update { it.copy(medicalEquipmentService = !it.medicalEquipmentService) }
    }

    fun setWheelchairAssistance(value: Boolean) { _uiState.update { it.copy(wheelchairAssistance = value) } }
    fun setVisualImpairment(value: Boolean) { _uiState.update { it.copy(visualImpairment = value) } }
    fun setHearingImpairment(value: Boolean) { _uiState.update { it.copy(hearingImpairment = value) } }
    fun setMedicalEquipmentService(value: Boolean) { _uiState.update { it.copy(medicalEquipmentService = value) } }

    fun setInfantOnLap(value: Boolean) { _uiState.update { it.copy(infantOnLap = value) } }
    fun setNumberOfInfants(value: Int) { _uiState.update { it.copy(numberOfInfants = value.coerceAtLeast(0)) } }
    fun incrementInfants() { _uiState.update { it.copy(numberOfInfants = it.numberOfInfants + 1) } }
    fun decrementInfants() { _uiState.update { it.copy(numberOfInfants = (it.numberOfInfants - 1).coerceAtLeast(0)) } }
    fun setTravellingWithPet(value: Boolean) { _uiState.update { it.copy(travellingWithPet = value) } }

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
