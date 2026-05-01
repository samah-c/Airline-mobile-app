package com.example.airline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.airline.ui.theme.AirlineTheme
import com.example.airline.utils.MrzData
import com.example.airline.view.confirmation.FinalConfirmationScreen
import com.example.airline.view.luggage.LuggageScreen
import com.example.airline.view.passeport.ConfirmDetailsScreen
import com.example.airline.view.passeport.PassportScanScreen
import com.example.airline.view.services.ServicesPreferencesScreen

enum class AppScreen {
    SCAN, CONFIRM_DETAILS, LUGGAGE, SERVICES, FINAL_CONFIRMATION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirlineTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.SCAN) }
                var mrzData by remember { mutableStateOf<MrzData?>(null) }

                when (currentScreen) {
                    AppScreen.SCAN -> {
                        PassportScanScreen(
                            onBack = { finish() },
                            onMrzExtracted = { data ->
                                mrzData = data
                                currentScreen = AppScreen.CONFIRM_DETAILS
                            }
                        )
                    }
                    AppScreen.CONFIRM_DETAILS -> {
                        ConfirmDetailsScreen(
                            mrzData = mrzData!!,
                            onBack = { currentScreen = AppScreen.SCAN },
                            onConfirm = { currentScreen = AppScreen.LUGGAGE }
                        )
                    }
                    AppScreen.LUGGAGE -> {
                        LuggageScreen(
                            onBack = { currentScreen = AppScreen.CONFIRM_DETAILS },
                            onConfirm = { currentScreen = AppScreen.SERVICES }
                        )
                    }
                    AppScreen.SERVICES -> {
                        ServicesPreferencesScreen(
                            onBack = { currentScreen = AppScreen.LUGGAGE },
                            onConfirm = { currentScreen = AppScreen.FINAL_CONFIRMATION },
                            onSkip = { currentScreen = AppScreen.FINAL_CONFIRMATION }
                        )
                    }
                    AppScreen.FINAL_CONFIRMATION -> {
                        FinalConfirmationScreen(
                            mrzData = mrzData!!,
                            onBack = { currentScreen = AppScreen.SERVICES },
                            onConfirm = {
                                // Final submission
                                finish()
                            },
                            onNext = {
                                // Action for next
                            }
                        )
                    }
                }
            }
        }
    }
}