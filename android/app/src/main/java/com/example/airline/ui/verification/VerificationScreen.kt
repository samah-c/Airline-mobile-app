package com.example.airline.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.ui.checkin.CheckInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    viewModel: VerificationViewModel = viewModel(),
    checkInViewModel: CheckInViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val checkInState by checkInViewModel.uiState.collectAsState()
    val verificationState by viewModel.uiState.collectAsState()

    LaunchedEffect(checkInState) {
        viewModel.initializeFrom(checkInState)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Confirm your details",
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

            Text(
                text = "STEP 2 OF 5",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle("✈️", "Personal Information")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Last Name",
                value = verificationState.lastName,
                onValueChange = viewModel::updateLastName
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "First Name",
                value = verificationState.firstName,
                onValueChange = viewModel::updateFirstName
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    label = "Date of birth",
                    value = verificationState.dob,
                    onValueChange = viewModel::updateDob,
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa",
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Color.Gray)
                    }
                )
                CustomTextField(
                    label = "Gender",
                    value = verificationState.gender,
                    onValueChange = viewModel::updateGender,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Nationality",
                value = verificationState.nationality,
                onValueChange = viewModel::updateNationality
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("✈️", "Passport Details")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Passport Number",
                value = verificationState.passportNumber,
                onValueChange = viewModel::updatePassportNumber
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    label = "Issue Date",
                    value = verificationState.issueDate,
                    onValueChange = viewModel::updateIssueDate,
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa"
                )
                CustomTextField(
                    label = "Expiration Date",
                    value = verificationState.expirationDate,
                    onValueChange = viewModel::updateExpirationDate,
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa",
                    readOnly = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Issuing Authority",
                value = verificationState.issuingAuthority,
                onValueChange = viewModel::updateIssuingAuthority
            )

            Spacer(modifier = Modifier.height(32.dp))

            Spacer(modifier = Modifier.height(48.dp))

            verificationState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFB91C1C),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.verifyPassport(checkInViewModel, onConfirm) },
                enabled = !verificationState.isLoading,
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
fun StepBar(isActive: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(4.dp)
            .background(
                color = if (isActive) Color(0xFF1942D8) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(2.dp)
            )
    )
}

@Composable
fun SectionTitle(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE0F2FE), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.weight(1f))
        Divider(
            color = Color(0xFFE5E7EB),
            thickness = 1.dp,
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label, color = Color(0xFF6B7280), fontSize = 12.sp) },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, color = Color(0xFF9CA3AF) ) } } else null,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFB), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedBorderColor = Color(0xFF1942D8),
            unfocusedContainerColor = Color(0xFFF8FAFB),
            focusedContainerColor = Color(0xFFF8FAFB),
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        ),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        singleLine = true
    )
}
