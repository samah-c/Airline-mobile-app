package com.example.airline.ui.flighthistory

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.R
import com.example.airline.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightHistoryScreen(
    onNavigateBack: () -> Unit,
    userId: Int,                                          // ✅ pass it in from the nav graph
    viewModel: FlightHistoryViewModel = viewModel(
        factory = FlightHistoryViewModel.factory(userId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    // ✅ Nouveau code avec derivedStateOf
    val filteredFlights by remember(uiState.flights, uiState.searchQuery) {
        derivedStateOf {
            val query = uiState.searchQuery.trim().lowercase()
            if (query.isEmpty()) {
                uiState.flights
            } else {
                uiState.flights.filter {
                    it.flightNumber.lowercase().contains(query) ||
                            it.departureAirport.lowercase().contains(query) ||
                            it.arrivalAirport.lowercase().contains(query)
                }
            }
        }
    }
    val groupedFlights = filteredFlights.groupBy { it.year }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Flight History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2541EE)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF1A1A2E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Input your flight number", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_airplane),
                            contentDescription = null,
                            tint = Color(0xFF1849D6),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    )
                )
            }

            // Flight List
            // Flight List
            if (uiState.isLoading) {
                // Loader pendant le chargement
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val groupedFlights = filteredFlights.groupBy { it.year }

                    // ✅ Vérifie que la map n'est pas vide avant d'accéder à .first()
                    val years = groupedFlights.keys.sortedDescending()

                    years.forEachIndexed { index, year ->
                        val flights = groupedFlights[year] ?: emptyList()

                        // Séparateur d'année (sauf pour la première)
                        if (index > 0) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        text = year.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6B7280),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .fillMaxWidth(0.4f)
                                            .height(1.dp)
                                            .background(Color(0xFFE0E0E0))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .fillMaxWidth(0.4f)
                                            .height(1.dp)
                                            .background(Color(0xFFE0E0E0))
                                    )
                                }
                            }
                        }

                        items(flights) { flight ->
                            FlightCard(flight)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Message "No flights found"
                    if (filteredFlights.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_airplane),
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No flights found",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlightCard(flight: FlightItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Flight header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_airplane),
                    contentDescription = null,
                    tint = Color(0xFF1849D6),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = flight.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Flight ${flight.flightNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flight details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = flight.departureTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE94235)
                    )
                    Text(
                        text = flight.departureAirport,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }

                // Duration and plane icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = flight.duration,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_airplane),
                        contentDescription = null,
                        tint = Color(0xFF1849D6),
                        modifier = Modifier
                            .size(24.dp)
                            .rotateZ(90f)
                    )
                    Text(
                        text = flight.cabinClass,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Arrival
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = flight.arrivalTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CD964)
                    )
                    Text(
                        text = flight.arrivalAirport,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }
            }
        }
    }
}

// Extension function for rotating icons
fun Modifier.rotateZ(degrees: Float): Modifier = this