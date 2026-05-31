package com.example.airline.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.baggage.BaggageScreen
import com.example.airline.ui.checkin.CheckInScreen
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.confirmation.ConfirmationScreen
import com.example.airline.ui.flighthistory.FlightHistoryScreen
import com.example.airline.ui.forgotpassword.ForgotPasswordScreen
import com.example.airline.ui.homepage.HomeScreen
import com.example.airline.ui.login.LoginScreen
import com.example.airline.ui.profile.ProfileScreen
import com.example.airline.ui.scan.PassportScanScreen
import com.example.airline.ui.scan.PassportScanViewModel
import com.example.airline.ui.services.ServicesScreen
import com.example.airline.ui.settings.SettingsScreen
import com.example.airline.ui.signup.SignUpScreen
import com.example.airline.ui.verification.VerificationScreen
import com.example.airline.ui.flightlookup.FlightLookUpResultScreen

object Routes {
    const val LOGIN             = "login"
    const val SIGNUP            = "signup"
    const val FORGOT_PASSWORD   = "forgot_password"
    const val HOME              = "home"
    const val PROFILE           = "profile"
    const val FLIGHT_HISTORY    = "flight_history"
    const val SETTINGS          = "settings"
    const val BAGGAGE           = "baggage"
    const val CHECKIN           = "checkin"
    const val SCAN              = "scan"
    const val CONFIRM_DETAILS   = "confirm_details"
    const val SERVICES          = "services"
    const val FINAL_CONFIRMATION = "final_confirmation"
    const val FLIGHT_LOOKUP_RESULT = "flight_lookup_result/{pnr}/{lastName}"

}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // CheckInViewModel partagé entre toutes les étapes du check-in
    val checkInViewModel: CheckInViewModel = viewModel()
    val passportScanViewModel: PassportScanViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // ── Auth ──────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGNUP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onNavigateToBaggage = {
                    navController.navigate(Routes.BAGGAGE)
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // ── Home (placeholder) ────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onSearchFlight = { pnr, lastName ->
                    navController.navigate("flight_lookup_result/$pnr/$lastName")
                },
                onNavigateBaggage  = { navController.navigate(Routes.BAGGAGE) },
                onNavigateCheckin  = { navController.navigate(Routes.CHECKIN) },
                onNavigateProfile  = { navController.navigate(Routes.PROFILE) }
            )
        }

        // ── Profil & Settings ─────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFlightHistory = {
                    navController.navigate(Routes.FLIGHT_HISTORY)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.FLIGHT_HISTORY) {
            FlightHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Baggage ───────────────────────────────────────────
        composable(Routes.BAGGAGE) {
            BaggageScreen(
                onNavigateBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.HOME)
                }
            )
        }

        // ── Flux Check-In (ViewModel partagé) ─────────────────
        composable(Routes.CHECKIN) {
            CheckInScreen(
                viewModel = checkInViewModel,
                onScanPassport = {
                    navController.navigate(Routes.SCAN)
                },
                onVerification = {
                    navController.navigate(Routes.CONFIRM_DETAILS)
                },
                onFlightOptions = {
                    navController.navigate(Routes.SERVICES)
                },
                onFinish = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SCAN) {
            PassportScanScreen(
                viewModel = passportScanViewModel,
                onBack = { navController.popBackStack() },
                onMrzExtracted = { mrzData ->
                    checkInViewModel.initFromMrz(mrzData)
                    navController.navigate(Routes.CONFIRM_DETAILS)
                }
            )
        }

        composable(Routes.CONFIRM_DETAILS) {
            VerificationScreen(
                viewModel = checkInViewModel,
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.SERVICES)
                }
            )
        }

        composable(Routes.SERVICES) {
            ServicesScreen(
                viewModel = checkInViewModel,
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.FINAL_CONFIRMATION)
                },
                onSkip = {
                    navController.navigate(Routes.FINAL_CONFIRMATION)
                }
            )
        }

        composable(Routes.FINAL_CONFIRMATION) {
            ConfirmationScreen(
                viewModel = checkInViewModel,
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.CHECKIN) { inclusive = true }
                    }
                },
                onNext = { }
            )
        }

        composable(Routes.FLIGHT_LOOKUP_RESULT) { backStackEntry ->
            val pnr = backStackEntry.arguments?.getString("pnr") ?: ""
            val lastName = backStackEntry.arguments?.getString("lastName") ?: ""
            FlightLookUpResultScreen(
                onCheckIn      = { navController.navigate(Routes.CHECKIN) },
                onNavigateHome = { navController.navigate(Routes.HOME) },
                onNavigateBaggage = { navController.navigate(Routes.BAGGAGE) },
                onNavigateProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
    }
}