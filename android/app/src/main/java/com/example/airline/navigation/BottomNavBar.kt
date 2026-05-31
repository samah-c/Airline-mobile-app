package com.example.airline.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.History
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

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Home    : BottomNavItem(Routes.HOME,           Icons.Default.Home)
    object CheckIn : BottomNavItem(Routes.CHECKIN,        Icons.Default.Flight)
    object History : BottomNavItem(Routes.FLIGHT_HISTORY, Icons.Default.History)
    object Profile : BottomNavItem(Routes.PROFILE,        Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.CheckIn,
    BottomNavItem.History,
    BottomNavItem.Profile
)

val bottomNavRoutes = setOf(
    Routes.HOME,
    Routes.CHECKIN,
    Routes.FLIGHT_HISTORY,
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
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF2D55E8)
                )
            )
        }
    }
}