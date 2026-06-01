package com.example.airline.ui.confirmation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.checkin.BoardingPassViewModel
import com.example.airline.ui.confirmation.ConfirmationViewModel
import com.example.airline.ui.verification.StepBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationViewModel = viewModel(),
    checkInViewModel: CheckInViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit,
    boardingPassViewModel: BoardingPassViewModel = viewModel(
        factory = BoardingPassViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by checkInViewModel.uiState.collectAsState()
    val confirmationState by viewModel.uiState.collectAsState()
    val boardingPassState by boardingPassViewModel.uiState.collectAsState()

    LaunchedEffect(state.checkInSessionId) {
        state.checkInSessionId?.let {
            boardingPassViewModel.generateBoardingPass(it)
        }
    }

    val boardingPass = boardingPassState.boardingPass

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Final confirmation",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("STEP 5 OF 5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            confirmationState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFB91C1C),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            ConfirmSectionHeader(icon = Icons.Default.Flight, title = "Personal Information")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Last Name", value = state.lastName)
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "First Name", value = state.firstName)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyTextField(
                    label = "Date of birth",
                    value = state.dob,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray)
                    }
                )
                ReadOnlyTextField(label = "Gender", value = state.gender, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "Nationality", value = state.nationality)

            Spacer(modifier = Modifier.height(24.dp))

            ConfirmSectionHeader(icon = Icons.Default.Flight, title = "Passport Details")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Passport Number", value = state.passportNumber)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyTextField(
                    label = "Issue Date",
                    value = state.issueDate.ifEmpty { "-" },
                    modifier = Modifier.weight(1f)
                )
                ReadOnlyTextField(
                    label = "Expiration Date",
                    value = state.expirationDate,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "Issuing Authority", value = state.issuingAuthority.ifEmpty { "-" })

            Spacer(modifier = Modifier.height(24.dp))

            ConfirmSectionHeader(icon = Icons.Default.Flight, title = "Boarding pass")
            Spacer(modifier = Modifier.height(16.dp))
            BoardingPassCard(
                passengerName = "${state.firstName} ${state.lastName}".trim().ifEmpty { boardingPass.passengerName.ifEmpty { "Passenger" } },
                seatNumber = state.seatNumber ?: boardingPass.seat.ifEmpty { "N/A" },
                flightNumber = boardingPass.flightNumber.ifEmpty { "LH007" },
                gate = boardingPass.gate.ifEmpty { "A2" },
                origin = boardingPass.origin.ifEmpty { "LON" },
                destination = boardingPass.destination.ifEmpty { "RIO" },
                boardingTime = boardingPass.boardingTime.ifEmpty { boardingPass.departureTime.ifEmpty { "08:15 AM" } },
                departTime = boardingPass.departureTime.ifEmpty { "08:45 AM" },
                arrivalTime = boardingPass.arrivalTime.ifEmpty { "12:00 PM" },
                seatClass = boardingPass.seatClass.ifEmpty { "Economy" }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    viewModel.confirm {
                        checkInViewModel.confirmCheckInAsync()
                        onConfirm()
                    }
                },
                enabled = !confirmationState.isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1942D8))
            ) {
                Text("Confirmer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ConfirmSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F9FF), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFDBEAFE), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
            Text("Vérifiez et confirmez les détails.", fontSize = 12.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun ReadOnlyTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) leadingIcon()
                Text(
                    value.ifEmpty { "-" },
                    fontSize = 14.sp,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (trailingIcon != null) trailingIcon()
            }
        }
    }
}

@Composable
fun BoardingPassCard(
    passengerName: String,
    seatNumber: String = "N/A",
    flightNumber: String = "LH007",
    gate: String = "A2",
    origin: String = "LON",
    destination: String = "RIO",
    boardingTime: String = "08:15 AM",
    departTime: String = "08:45 AM",
    arrivalTime: String = "12:00 PM",
    seatClass: String = "Economy"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(flightNumber, color = Color(0xFFF97316), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Flight", fontSize = 9.sp, color = Color.Gray)
                        Text(flightNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Gate", fontSize = 9.sp, color = Color.Gray)
                        Text(gate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                    }
                }
            }

            CutoutDashedLine()

            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(origin, fontSize = 10.sp, color = Color(0xFF1942D8), fontWeight = FontWeight.Bold)
                    Text(origin.uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Flight, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(24.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(destination, fontSize = 10.sp, color = Color(0xFF1942D8), fontWeight = FontWeight.Bold)
                    Text(destination.uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Boarding Time", fontSize = 9.sp, color = Color.Gray)
                    Text(boardingTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column {
                    Text("Departs", fontSize = 9.sp, color = Color.Gray)
                    Text(departTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Arrives", fontSize = 9.sp, color = Color.Gray)
                    Text(arrivalTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            CutoutDashedLine()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Passenger", fontSize = 9.sp, color = Color.Gray)
                    Text(passengerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Seat", fontSize = 9.sp, color = Color.Gray)
                    Text(seatNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CutoutDashedLine()
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.height(60.dp), horizontalArrangement = Arrangement.Center) {
                    val widths = listOf(2.dp, 4.dp, 1.dp, 6.dp, 2.dp, 3.dp, 1.dp, 4.dp, 2.dp, 5.dp, 1.dp, 2.dp, 4.dp, 2.dp, 1.dp, 3.dp, 2.dp, 4.dp, 1.dp, 6.dp)
                    for (width in widths) {
                        Box(modifier = Modifier.width(width).fillMaxHeight().background(Color.Black))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "8 3 6 K 8 9 0 2 K G M 3 Q 1 7 S 0",
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CutoutDashedLine() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 20.dp)
                .background(
                    color = Color(0xFF1942D8),
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                )
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .padding(horizontal = 8.dp)
        ) {
            drawLine(
                color = Color(0xFF1942D8),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 20.dp)
                .background(
                    color = Color(0xFF1942D8),
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                )
        )
    }
}
