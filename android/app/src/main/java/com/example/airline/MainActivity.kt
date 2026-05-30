package com.example.airline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.ui.theme.AirlineTheme
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.confirmation.ConfirmationScreen
import com.example.airline.ui.scan.PassportScanScreen
import com.example.airline.ui.checkin.CheckInScreen
import com.example.airline.ui.scan.PassportScanViewModel
import com.example.airline.ui.services.ServicesScreen
import com.example.airline.ui.verification.VerificationScreen

// Flux de navigation : CHECKIN → SCAN → CONFIRM_DETAILS → SERVICES → FINAL_CONFIRMATION
enum class AppScreen {
    CHECKIN, SCAN, CONFIRM_DETAILS, SERVICES, FINAL_CONFIRMATION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirlineTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.CHECKIN) }

                // ViewModel partagé entre tous les écrans
                val checkInViewModel: CheckInViewModel = viewModel()
                val passportScanViewModel: PassportScanViewModel = viewModel()

                when (currentScreen) {
                    AppScreen.CHECKIN -> {
                        CheckInScreen(
                            viewModel = checkInViewModel,
                            onScanPassport = { currentScreen = AppScreen.SCAN },
                            onVerification = { currentScreen = AppScreen.CONFIRM_DETAILS },
                            onFlightOptions = { currentScreen = AppScreen.SERVICES },
                            onFinish = { finish() }
                        )
                    }
                    AppScreen.SCAN -> {
                        PassportScanScreen(
                            onBack = { currentScreen = AppScreen.CHECKIN },
                            onMrzExtracted = { mrzData ->
                                checkInViewModel.initFromMrz(mrzData)
                                currentScreen = AppScreen.CONFIRM_DETAILS
                            },
                            viewModel = passportScanViewModel
                        )
                    }
                    AppScreen.CONFIRM_DETAILS -> {
                        VerificationScreen(
                            viewModel = checkInViewModel,
                            onBack = { currentScreen = AppScreen.CHECKIN },
                            onConfirm = { currentScreen = AppScreen.SERVICES }
                        )
                    }
                    AppScreen.SERVICES -> {
                        ServicesScreen(
                            viewModel = checkInViewModel,
                            onBack = { currentScreen = AppScreen.CHECKIN },
                            onConfirm = { currentScreen = AppScreen.FINAL_CONFIRMATION },
                            onSkip = { currentScreen = AppScreen.FINAL_CONFIRMATION }
                        )
                    }
                    AppScreen.FINAL_CONFIRMATION -> {
                        ConfirmationScreen(
                            viewModel = checkInViewModel,
                            onBack = { currentScreen = AppScreen.SERVICES },
                            onConfirm = { finish() },
                            onNext = { }
                        )
                    }
                }
            }
        }
    }
}