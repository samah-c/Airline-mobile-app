package com.example.airline.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airline.ui.baggage.BaggageScreen
import com.example.airline.ui.checkin.BoardingPassOfflineScreen
import com.example.airline.ui.checkin.BoardingPassScreen
import com.example.airline.ui.checkin.CheckInScreen
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.checkin.SeatSelectionScreen
import com.example.airline.ui.confirmation.ConfirmationScreen
import com.example.airline.ui.flighthistory.FlightHistoryScreen
import com.example.airline.ui.flightlookup.FlightLookUpResultScreen
import com.example.airline.ui.forgotpassword.ForgotPasswordScreen
import com.example.airline.ui.homepage.HomeScreen
import com.example.airline.ui.login.LoginScreen
import com.example.airline.ui.onboarding.OnboardingScreen
import com.example.airline.ui.profile.ProfileScreen
import com.example.airline.ui.scan.PassportScanScreen
import com.example.airline.ui.scan.PassportScanViewModel
import com.example.airline.ui.services.ServicesScreen
import com.example.airline.ui.settings.SettingsScreen
import com.example.airline.ui.signup.SignUpScreen
import com.example.airline.ui.splash.SplashScreen
import com.example.airline.ui.verification.VerificationScreen
import com.example.airline.ui.verification.VerificationViewModel
import com.example.airline.ui.baggage.BaggageViewModel
import com.example.airline.ui.services.ServicesViewModel
import com.example.airline.ui.homepage.HomeViewModel
import com.example.airline.ui.flightlookup.FlightLookUpResultViewModel
import com.example.airline.ui.profile.ProfileViewModel
import androidx.hilt.navigation.compose.hiltViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Safe back stack helper — avoids crash when CHECKIN is no longer on the stack
// ─────────────────────────────────────────────────────────────────────────────
private fun NavHostController.getBackStackEntryOrNull(route: String) =
    try { getBackStackEntry(route) } catch (e: IllegalArgumentException) { null }

// ─────────────────────────────────────────────────────────────────────────────
// Routes
// ─────────────────────────────────────────────────────────────────────────────
object Routes {
    const val SPLASH                = "splash"
    const val ONBOARDING            = "onboarding"
    const val LOGIN                 = "login"
    const val SIGNUP                = "signup"
    const val FORGOT_PASSWORD       = "forgot_password"
    const val BOARDING_PASS_OFFLINE = "boarding_pass_offline"

    const val HOME                  = "home"
    const val PROFILE               = "profile"
    const val FLIGHT_HISTORY        = "flight_history"
    const val SETTINGS              = "settings"
    const val BOARDING_PASS         = "boarding_pass"

    // Check-in flow (correct order)
    const val CHECKIN               = "checkin"
    const val SCAN                  = "scan"
    const val CONFIRM_DETAILS       = "confirm_details"
    const val SEAT_SELECTION        = "seat_selection"
    const val BAGGAGE               = "baggage"
    const val SERVICES              = "services"
    const val FINAL_CONFIRMATION    = "final_confirmation"

    // Flight lookup with args
    const val FLIGHT_LOOKUP_RESULT  = "flight_lookup_result/{pnr}/{lastName}"
    fun flightLookupResult(pnr: String, lastName: String) =
        "flight_lookup_result/$pnr/$lastName"
}

// ─────────────────────────────────────────────────────────────────────────────
// AppNavGraph
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.SPLASH,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── Splash ────────────────────────────────────────
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // ── Onboarding ────────────────────────────────────
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
                    onNavigateBack             = { navController.popBackStack() },
                    onLoginSuccess             = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp         = { navController.navigate(Routes.SIGNUP) },
                    onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onNavigateToBaggage        = { navController.navigate(Routes.BOARDING_PASS_OFFLINE) }
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

            // Offline boarding pass — accessible without login
            composable(Routes.BOARDING_PASS_OFFLINE) {
                BoardingPassOfflineScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ── Main screens (with bottom nav) ─────────────────
            composable(Routes.HOME) {
                HomeScreen(
                    onSearchFlight    = { pnr, lastName ->
                        navController.navigate(Routes.flightLookupResult(pnr, lastName))
                    },
                    onNavigateBaggage = { navController.navigate(Routes.CHECKIN) },
                    onNavigateCheckin = { navController.navigate(Routes.CHECKIN) },
                    onNavigateProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onNavigateBack            = { navController.popBackStack() },
                    onNavigateToFlightHistory = { navController.navigate(Routes.FLIGHT_HISTORY) },
                    onNavigateToSettings      = { navController.navigate(Routes.SETTINGS) },
                    onStartTestCheckIn       = { _, _ -> /* TODO */ }
                )
            }

            composable(Routes.FLIGHT_HISTORY) {
                val context = LocalContext.current
                val userId = remember {
                    context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        .getInt("user_id", -1)
                }
                FlightHistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    userId         = userId
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack      = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                    onLogout            = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.BOARDING_PASS) {
                BoardingPassScreen(
                    onBack     = { navController.popBackStack() },
                    onDownload = { navController.navigate(Routes.BOARDING_PASS_OFFLINE) }
                )
            }

            // ── Flight Lookup Result ───────────────────────────
            composable(
                route = Routes.FLIGHT_LOOKUP_RESULT,
                arguments = listOf(
                    navArgument("pnr")      { type = NavType.StringType },
                    navArgument("lastName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                FlightLookUpResultScreen(
                    pnr               = backStackEntry.arguments?.getString("pnr") ?: "",
                    lastName          = backStackEntry.arguments?.getString("lastName") ?: "",
                    onCheckIn         = { navController.navigate(Routes.CHECKIN) },
                    onNavigateHome    = { navController.navigate(Routes.HOME) },
                    onNavigateBaggage = { navController.navigate(Routes.CHECKIN) },
                    onNavigateProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            // ── Check-In flow ─────────────────────────────────
            composable(Routes.CHECKIN) {
                val checkInViewModel: CheckInViewModel = viewModel(
                    viewModelStoreOwner = it
                )
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
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                val passportScanViewModel: PassportScanViewModel = viewModel()

                PassportScanScreen(
                    viewModel      = passportScanViewModel,
                    onBack         = { navController.popBackStack() },
                    onMrzExtracted = { mrzData ->
                        checkInViewModel.initFromMrz(mrzData)
                        navController.navigate(Routes.CONFIRM_DETAILS)
                    }
                )
            }

            composable(Routes.CONFIRM_DETAILS) {
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                val verificationViewModel: VerificationViewModel = viewModel()
                VerificationScreen(
                    viewModel        = verificationViewModel,
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = { navController.navigate(Routes.SEAT_SELECTION) }
                )
            }

            composable(Routes.SEAT_SELECTION) {
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                val checkInId = checkInViewModel.uiState.value.checkInSessionId ?: 0

                SeatSelectionScreen(
                    checkInId = checkInId,
                    onBack    = { navController.popBackStack() },
                    onNext    = { navController.navigate(Routes.BAGGAGE) }
                )
            }

            composable(Routes.BAGGAGE) {
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                val baggageViewModel: BaggageViewModel = viewModel()
                BaggageScreen(
                    viewModel        = baggageViewModel,
                    checkInViewModel = checkInViewModel,
                    onNavigateBack   = { navController.popBackStack() },
                    onConfirm        = { navController.navigate(Routes.SERVICES) }
                )
            }

            composable(Routes.SERVICES) {
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                val servicesViewModel: ServicesViewModel = viewModel()
                ServicesScreen(
                    viewModel        = servicesViewModel,
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = { navController.navigate(Routes.FINAL_CONFIRMATION) }
                )
            }

            composable(Routes.FINAL_CONFIRMATION) {
                val checkInEntry = remember(currentEntry) {
                    navController.getBackStackEntryOrNull(Routes.CHECKIN)
                } ?: return@composable
                val checkInViewModel: CheckInViewModel = viewModel(checkInEntry)
                ConfirmationScreen(
                    checkInViewModel = checkInViewModel,
                    onBack           = { navController.popBackStack() },
                    onConfirm        = {
                        navController.navigate(Routes.BOARDING_PASS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onNext           = {
                        navController.navigate(Routes.BOARDING_PASS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}