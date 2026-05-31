package com.example.airline.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.baggage.BaggageScreen
import com.example.airline.ui.flighthistory.FlightHistoryScreen
import com.example.airline.ui.forgotpassword.ForgotPasswordScreen
import com.example.airline.ui.login.LoginScreen
//import com.example.airline.ui.onboarding.OnboardingScreen
import com.example.airline.ui.profile.ProfileScreen
import com.example.airline.ui.settings.SettingsScreen
import com.example.airline.ui.signup.SignUpScreen
//import com.example.airline.ui.splash.SplashScreen

object Routes {
    const val SPLASH         = "splash"
    const val ONBOARDING     = "onboarding"
    const val LOGIN          = "login"
    const val SIGNUP         = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME           = "home"
    const val BAGGAGE = "baggage"
    const val PROFILE = "profile"
    const val FLIGHT_HISTORY = "flight_history"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
       /* composable(Routes.SPLASH) {
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
        }*/

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Remplace HOME par ton écran principal plus tard
                    navController.navigate(Routes.PROFILE) {
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

        // Placeholder pour l'écran principal (Home/Dashboard)
        composable(Routes.HOME) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = " Home Screen — Connecté !", fontSize = 24.sp)
            }
        }

        composable(Routes.BAGGAGE) {
            BaggageScreen(
                onNavigateBack = { navController.popBackStack() },
                onConfirm = {
                    // Rediriger vers Payment ou Booking Confirmation
                    navController.navigate(Routes.HOME)
                }
            )
        }
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
                    // Retour à l'écran de login avec cleanup
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}