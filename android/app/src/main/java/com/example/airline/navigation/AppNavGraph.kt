package com.example.airline.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.baggage.BaggageScreen
import com.example.airline.ui.checkin.BoardingPassOfflineScreen
import com.example.airline.ui.checkin.BoardingPassScreen
import com.example.airline.ui.checkin.CheckInScreen
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.checkin.SeatSelectionScreen
import com.example.airline.ui.confirmation.ConfirmationScreen
import com.example.airline.ui.flighthistory.FlightHistoryScreen
import com.example.airline.ui.forgotpassword.ForgotPasswordScreen
import com.example.airline.ui.login.LoginScreen
import com.example.airline.ui.onboarding.OnboardingScreen
import com.example.airline.ui.profile.ProfileScreen
import com.example.airline.network.RetrofitClient
import com.example.airline.ui.scan.PassportScanScreen
import com.example.airline.ui.scan.PassportScanViewModel
import com.example.airline.ui.services.ServicesScreen
import com.example.airline.ui.settings.SettingsScreen
import com.example.airline.ui.signup.SignUpScreen
import com.example.airline.ui.splash.SplashScreen
import com.example.airline.ui.verification.VerificationScreen

object Routes {
    const val SPLASH                = "splash"
    const val ONBOARDING            = "onboarding"
    const val LOGIN                 = "login"
    const val SIGNUP                = "signup"
    const val FORGOT_PASSWORD       = "forgot_password"
    const val HOME                  = "home"
    const val PROFILE               = "profile"
    const val FLIGHT_HISTORY        = "flight_history"
    const val SETTINGS              = "settings"
    const val BAGGAGE               = "baggage"
    const val CHECKIN               = "checkin"
    const val SCAN                  = "scan"
    const val CONFIRM_DETAILS       = "confirm_details"
    const val SERVICES              = "services"
    const val FINAL_CONFIRMATION    = "final_confirmation"
    const val SEAT_SELECTION        = "seat_selection"
    const val BOARDING_PASS         = "boarding_pass"
    const val BOARDING_PASS_OFFLINE = "boarding_pass_offline"
}


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val checkInViewModel: CheckInViewModel = viewModel()
    val checkInState by checkInViewModel.uiState.collectAsState()
    val passportScanViewModel: PassportScanViewModel = viewModel()

    val currentEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = currentEntry?.destination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.SPLASH,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── Splash & Onboarding ───────────────────────────
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onGetStarted = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            // ── Auth ──────────────────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp        = { navController.navigate(Routes.SIGNUP) },
                    onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onNavigateToBaggage       = { navController.navigate(Routes.BAGGAGE) }
                )
            }

            composable(Routes.SIGNUP) {
                SignUpScreen(
                    onNavigateBack  = { navController.popBackStack() },
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
                    onNavigateBack    = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main (with bottom nav) ────────────────────────
            composable(Routes.HOME) {
                ProfileScreen(
                    onNavigateBack          = { navController.popBackStack() },
                    onNavigateToFlightHistory = { navController.navigate(Routes.FLIGHT_HISTORY) },
                    onNavigateToSettings    = { navController.navigate(Routes.SETTINGS) },
                    onStartTestCheckIn = { bookingId, token ->
                        if (token.isNotBlank()) RetrofitClient.setToken(token)
                        checkInViewModel.startCheckIn(bookingId, onSuccess = {
                            navController.navigate(Routes.CHECKIN)
                        }, onError = { message -> println("Test check-in failed: $message") })
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onNavigateBack          = { navController.popBackStack() },
                    onNavigateToFlightHistory = { navController.navigate(Routes.FLIGHT_HISTORY) },
                    onNavigateToSettings    = { navController.navigate(Routes.SETTINGS) },
                    onStartTestCheckIn = { bookingId, token ->
                        if (token.isNotBlank()) RetrofitClient.setToken(token)
                        checkInViewModel.startCheckIn(bookingId, onSuccess = {
                            navController.navigate(Routes.CHECKIN)
                        }, onError = { message -> println("Test check-in failed: $message") })
                    }
                )
            }

            composable(Routes.FLIGHT_HISTORY) {
                FlightHistoryScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack    = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                    onLogout          = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ── Baggage ───────────────────────────────────────
            composable(Routes.BAGGAGE) {
                BaggageScreen(
                    checkInViewModel = checkInViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onConfirm      = { navController.navigate(Routes.SERVICES) }
                )
            }

            // ── Check-In flow ─────────────────────────────────
            composable(Routes.CHECKIN) {
                CheckInScreen(
                    viewModel       = checkInViewModel,
                    onScanPassport  = { navController.navigate(Routes.SCAN) },
                    onVerification  = { navController.navigate(Routes.CONFIRM_DETAILS) },
                    onSeatSelection = { navController.navigate(Routes.SEAT_SELECTION) },
                    onBaggage       = { navController.navigate(Routes.BAGGAGE) },
                    onFlightOptions = { navController.navigate(Routes.SERVICES) },
                    onFinish        = { navController.popBackStack() }
                )
            }

            composable(Routes.SCAN) {
                PassportScanScreen(
                    viewModel       = passportScanViewModel,
                    onBack          = { navController.popBackStack() },
                    onMrzExtracted  = { mrzData ->
                        checkInViewModel.initFromMrz(mrzData)
                        navController.navigate(Routes.CONFIRM_DETAILS)
                    }
                )
            }

            composable(Routes.CONFIRM_DETAILS) {
                VerificationScreen(
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = { navController.navigate(Routes.SEAT_SELECTION) }
                )
            }

            composable(Routes.SERVICES) {
                ServicesScreen(
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = { navController.navigate(Routes.FINAL_CONFIRMATION) }
                )
            }

            composable(Routes.FINAL_CONFIRMATION) {
                ConfirmationScreen(
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = {
                        navController.navigate(Routes.BOARDING_PASS) {
                            popUpTo(Routes.CHECKIN) { inclusive = true }
                        }
                    },
                    onNext = { navController.popBackStack(Routes.CHECKIN, false) }
                )
            }

            // ── Feriel's screens ──────────────────────────────
            composable(Routes.SEAT_SELECTION) {
                SeatSelectionScreen(
                    checkInId = checkInState.checkInSessionId ?: 0,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.BAGGAGE) }
                )
            }

            composable(Routes.BOARDING_PASS) {
                BoardingPassScreen(
                    onBack     = { navController.popBackStack() },
                    onDownload = { navController.navigate(Routes.BOARDING_PASS_OFFLINE) }
                )
            }

            composable(Routes.BOARDING_PASS_OFFLINE) {
                BoardingPassOfflineScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
