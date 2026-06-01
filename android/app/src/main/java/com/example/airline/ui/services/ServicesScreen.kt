package com.example.airline.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.ui.checkin.CheckInViewModel
import com.example.airline.ui.verification.StepBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel = viewModel(),
    checkInViewModel: CheckInViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val checkInState by checkInViewModel.uiState.collectAsState()
    val servicesState by viewModel.uiState.collectAsState()

    LaunchedEffect(checkInState) {
        viewModel.initializeFrom(checkInState)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Services & Preferences",
                        fontWeight = FontWeight.Bold,
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
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
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

            Spacer(modifier = Modifier.height(32.dp))

            Text("DIETARY PREFERENCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1942D8), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    DietaryItem(
                        title = "Standard meal", subtitle = "Regular in-flight meal", icon = "🍽️",
                        isSelected = servicesState.selectedMeal == "Standard meal",
                        onClick = { viewModel.updateSelectedMeal("Standard meal") },
                        showBorder = true
                    )
                    DietaryItem(
                        title = "Vegetarian", subtitle = "No meat or fish", icon = "🥗",
                        isSelected = servicesState.selectedMeal == "Vegetarian",
                        onClick = { viewModel.updateSelectedMeal("Vegetarian") },
                        showBorder = true
                    )
                    DietaryItem(
                        title = "Gluten-free", subtitle = "No gluten-containing foods", icon = "🌾",
                        isSelected = servicesState.selectedMeal == "Gluten-free",
                        onClick = { viewModel.updateSelectedMeal("Gluten-free") },
                        showBorder = true
                    )
                    DietaryItem(
                        title = "Halal", subtitle = "Permissible under Islamic law", icon = "☪️",
                        isSelected = servicesState.selectedMeal == "Halal",
                        onClick = { viewModel.updateSelectedMeal("Halal") },
                        showBorder = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("ASSISTANCE NEEDS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistanceItemBox(
                    title = "Wheelchair assistance", icon = "♿",
                    isSelected = servicesState.wheelchairAssistance,
                    onClick = { viewModel.toggleWheelchairAssistance() },
                    modifier = Modifier.weight(1f)
                )
                AssistanceItemBox(
                    title = "Visual impairment", icon = "👁️",
                    isSelected = servicesState.visualImpairment,
                    onClick = { viewModel.toggleVisualImpairment() },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistanceItemBox(
                    title = "Hearing impairment", icon = "🦻",
                    isSelected = servicesState.hearingImpairment,
                    onClick = { viewModel.toggleHearingImpairment() },
                    modifier = Modifier.weight(1f)
                )
                AssistanceItemBox(
                    title = "Medical equipment", icon = "⚕️",
                    isSelected = servicesState.medicalEquipmentService,
                    onClick = { viewModel.toggleMedicalEquipmentService() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("TRAVELLING COMPANIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1942D8), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("👶", fontSize = 16.sp) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Infant on lap", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Under 2 years old", color = Color.Gray, fontSize = 12.sp)
                        }
                        Switch(
                            checked = servicesState.infantOnLap,
                            onCheckedChange = { viewModel.setInfantOnLap(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4AC29A)
                            )
                        )
                    }

                    if (servicesState.infantOnLap) {
                        Divider(color = Color(0xFF1942D8))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFF6FF))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Number of infants", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            Stepper(
                                value = servicesState.numberOfInfants,
                                onMinus = { viewModel.decrementInfants() },
                                onPlus = { viewModel.incrementInfants() }
                            )
                        }
                    }

                    Divider(color = Color(0xFF1942D8))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("🐾", fontSize = 16.sp) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Travelling with pet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Cabin or cargo hold", color = Color.Gray, fontSize = 12.sp)
                        }
                        Switch(
                            checked = servicesState.travellingWithPet,
                            onCheckedChange = { viewModel.setTravellingWithPet(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4AC29A)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            servicesState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFB91C1C),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.submitSpecialRequests(checkInViewModel, onConfirm) },
                enabled = !servicesState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1942D8))
            ) {
                Text("Confirmer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DietaryItem(title: String, subtitle: String, icon: String, isSelected: Boolean, onClick: () -> Unit, showBorder: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 16.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(24.dp))
            } else {
                Box(modifier = Modifier.size(24.dp).border(1.dp, Color.LightGray, CircleShape))
            }
        }
        if (showBorder) Divider(color = Color(0xFFE5E7EB))
    }
}

@Composable
fun AssistanceItemBox(title: String, icon: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) Color(0xFF1942D8) else Color.White
    val textColor = if (isSelected) Color.White else Color.Black
    Box(
        modifier = modifier
            .border(1.dp, Color(0xFF1942D8), RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 16.sp) }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor, lineHeight = 16.sp)
        }
    }
}

@Composable
fun Stepper(value: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                .clickable(onClick = onMinus),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                .clickable(onClick = onPlus),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}
