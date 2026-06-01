package com.example.airline.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// ─────────────────────────────────────────────────────────────────────────────
// Bottom nav items — 4 tabs matching the app's main sections
// FIX: removed FLIGHT_HISTORY (it's a sub-page of Profile, not a top-level tab)
//      replaced with BOARDING_PASS so users can reach their boarding pass quickly
// ─────────────────────────────────────────────────────────────────────────────
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home         : BottomNavItem(Routes.HOME,         Icons.Default.Home,                "Home")
    object CheckIn      : BottomNavItem(Routes.CHECKIN,      Icons.Default.AirplanemodeActive,  "Check-in")
    object BoardingPass : BottomNavItem(Routes.BOARDING_PASS, Icons.Default.ConfirmationNumber, "Pass")
    object Profile      : BottomNavItem(Routes.PROFILE,      Icons.Default.Person,              "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.CheckIn,
    BottomNavItem.BoardingPass,
    BottomNavItem.Profile
)

// These are the routes where the bottom bar is visible
val bottomNavRoutes = setOf(
    Routes.HOME,
    Routes.CHECKIN,
    Routes.BOARDING_PASS,
    Routes.PROFILE
)

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController
        .currentBackStackEntryAsState().value
        ?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF1942D8),
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Always pop back to Home so the back stack stays clean
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF2D55E8)
                )
            )
        }
    }
}