package com.example.airline.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.ui.checkin.CheckInUiState
import com.example.airline.ui.checkin.CheckInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationUiState(
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VerificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var hasInitialized = false

    fun initializeFrom(checkInState: CheckInUiState) {
        if (hasInitialized) return
        hasInitialized = true
        _uiState.update {
            it.copy(
                lastName = checkInState.lastName,
                firstName = checkInState.firstName,
                dob = checkInState.dob,
                gender = checkInState.gender,
                nationality = checkInState.nationality,
                passportNumber = checkInState.passportNumber,
                issueDate = checkInState.issueDate,
                expirationDate = checkInState.expirationDate,
                issuingAuthority = checkInState.issuingAuthority,
                email = checkInState.email,
                phoneNumber = checkInState.phoneNumber
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

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private fun setError(message: String?) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }

    fun verifyPassport(checkInViewModel: CheckInViewModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            setError(null)
            try {
                val currentState = _uiState.value
                checkInViewModel.updateLastName(currentState.lastName)
                checkInViewModel.updateFirstName(currentState.firstName)
                checkInViewModel.updateDob(currentState.dob)
                checkInViewModel.updateGender(currentState.gender)
                checkInViewModel.updateNationality(currentState.nationality)
                checkInViewModel.updatePassportNumber(currentState.passportNumber)
                checkInViewModel.updateIssueDate(currentState.issueDate)
                checkInViewModel.updateExpirationDate(currentState.expirationDate)
                checkInViewModel.updateIssuingAuthority(currentState.issuingAuthority)

                checkInViewModel.verifyPassportAsync()
                onSuccess()
            } catch (e: Exception) {
                setError(e.message ?: "Verification failed")
            } finally {
                setLoading(false)
            }
        }
    }
}
