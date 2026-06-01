package com.example.airline.ui.flightlookup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BrandBlue = Color(0xFF2B4EFF)
private val StatusYellowBg = Color(0xFFFFF3CD)
private val StatusYellowText = Color(0xFF856404)
private val DepartureRed = Color(0xFFE53935)
private val ArrivalGreen = Color(0xFF43A047)
@Composable
fun FlightLookUpResultScreen(
    pnr: String,                        // ← receive from nav args
    lastName: String,                   // ← receive from nav args
    onCheckIn: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateBaggage: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    viewModel: FlightLookUpResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Trigger fetch once when the screen first appears
    LaunchedEffect(pnr, lastName) {
        viewModel.loadFlight(pnr, lastName)
    }

    Scaffold(
        topBar = { FlightLookUpResultTopBar() },
        containerColor = Color.White
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = Color.Gray,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadFlight(pnr, lastName) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) {
                            Text("Réessayer", color = Color.White)
                        }
                    }
                }
            }

            else -> {
                // Your existing Column content, unchanged
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(status = uiState.checkInStatus)
                    Text(
                        text = uiState.flightNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                    FlightInfoCard(uiState = uiState)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onCheckIn,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Text("Check-In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Top Bar ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlightLookUpResultTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Flight LookUp",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// ── Status Badge ─────────────────────────────────────────────────────────────
@Composable
private fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .background(
                color = StatusYellowBg,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            color = StatusYellowText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Flight Info Card ──────────────────────────────────────────────────────────
@Composable
private fun FlightInfoCard(uiState: FlightLookUpResultUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Departure
            FlightEndpoint(
                date = uiState.departureDate,
                time = uiState.departureTime,
                timeColor = DepartureRed,
                airport = uiState.departureAirport,
                alignment = Alignment.Start
            )

            // Center — duration + plane icon
            FlightMiddle(duration = uiState.duration, cabinClass = uiState.cabinClass)

            // Arrival
            FlightEndpoint(
                date = uiState.arrivalDate,
                time = uiState.arrivalTime,
                timeColor = ArrivalGreen,
                airport = uiState.arrivalAirport,
                alignment = Alignment.End
            )
        }
    }
}

@Composable
private fun FlightEndpoint(
    date: String,
    time: String,
    timeColor: Color,
    airport: String,
    alignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = alignment) {
        Text(text = date, fontSize = 13.sp, color = Color.Gray)
        Text(
            text = time,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = timeColor
        )
        Text(
            text = airport,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
private fun FlightMiddle(duration: String, cabinClass: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = duration, fontSize = 12.sp, color = Color.Gray)

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Left dash
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(22.dp)
            )
            // Right arrow dash
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }

        Text(text = cabinClass, fontSize = 12.sp, color = Color.Gray)
    }
}


