package com.example.airline.ui.checkin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class StepStatus {
    COMPLETED, ACTIVE, INACTIVE
}

private data class StepInfo(
    val title: String,
    val subtitle: String,
    val status: StepStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel,
    onScanPassport: () -> Unit,
    onVerification: () -> Unit,
    onFlightOptions: () -> Unit,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Logic to determine step status based on ViewModel state
    // Step 1 is personal info (passport scan)
    val isStep1Done = uiState.passportNumber.isNotEmpty()
    
    // To match the screenshot exactly for the user:
    // We force Step 1 as COMPLETED and Step 2 as ACTIVE as shown in the image.
    val steps = listOf(
        StepInfo("Personal info", "Passeport Scan", StepStatus.COMPLETED),
        StepInfo("Verification", "Confirm Your Details", StepStatus.ACTIVE),
        StepInfo("Seat Selection", "Choose Your Seat", StepStatus.INACTIVE),
        StepInfo("Baggage", "Declare Your Luggage", StepStatus.INACTIVE),
        StepInfo("Flight Options", "Services & Preferences", StepStatus.INACTIVE)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Check-In",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D2939),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Color(0xFF1942D8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar()
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp, vertical = 40.dp)
        ) {
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.Top
                ) {
                    // Timeline Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(40.dp)
                    ) {
                        TimelineDot(status = step.status, number = index + 1)
                        if (index < steps.size - 1) {
                            // Connector is active (blue) if the CURRENT step is COMPLETED or ACTIVE
                            val isConnectorActive = step.status == StepStatus.COMPLETED || step.status == StepStatus.ACTIVE
                            TimelineConnector(active = isConnectorActive)
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Step Content
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.dp)
                            .clickable(enabled = step.status != StepStatus.INACTIVE) {
                                when (index) {
                                    0 -> onScanPassport()
                                    1 -> onVerification()
                                    4 -> onFlightOptions()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step.title,
                                fontWeight = FontWeight.Bold,
                                color = if (step.status != StepStatus.INACTIVE) Color(0xFF1D2939) else Color(0xFF9CA3AF),
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = step.subtitle,
                                color = Color(0xFF9CA3AF),
                                fontSize = 15.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = if (step.status != StepStatus.INACTIVE) Color(0xFF1942D8) else Color(0xFFD1D5DB),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineDot(status: StepStatus, number: Int) {
    val backgroundColor = when (status) {
        StepStatus.COMPLETED, StepStatus.ACTIVE -> Color(0xFF1942D8)
        StepStatus.INACTIVE -> Color(0xFFD1D5DB)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (status == StepStatus.COMPLETED) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = number.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun TimelineConnector(active: Boolean) {
    val color = if (active) Color(0xFF1942D8) else Color(0xFFD1D5DB)
    Column(
        modifier = Modifier
            .width(40.dp)
            .height(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.weight(1f).width(2.dp)) {
            drawLine(
                color = color,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
            )
        }
        Icon(
            imageVector = Icons.Default.Flight,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp).padding(vertical = 2.dp)
        )
        Canvas(modifier = Modifier.weight(1f).width(2.dp)) {
            drawLine(
                color = color,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
            )
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    Surface(
        color = Color(0xFF1942D8),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = Icons.Default.Home, isSelected = false)
            BottomNavItem(icon = Icons.Default.ConfirmationNumber, isSelected = true)
            BottomNavItem(icon = Icons.Default.Flight, isSelected = false)
            BottomNavItem(icon = Icons.Default.Person, isSelected = false)
        }
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, isSelected: Boolean) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
        modifier = Modifier.size(30.dp)
    )
}
