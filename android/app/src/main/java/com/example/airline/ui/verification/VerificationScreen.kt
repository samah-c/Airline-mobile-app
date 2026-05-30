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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.ui.checkin.CheckInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    viewModel: CheckInViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
                value = state.lastName,
                onValueChange = { viewModel.updateLastName(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "First Name",
                value = state.firstName,
                onValueChange = { viewModel.updateFirstName(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    label = "Date of birth",
                    value = state.dob,
                    onValueChange = { viewModel.updateDob(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa",
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Color.Gray)
                    }
                )
                CustomTextField(
                    label = "Gender",
                    value = state.gender,
                    onValueChange = { viewModel.updateGender(it) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Nationality",
                value = state.nationality,
                onValueChange = { viewModel.updateNationality(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("✈️", "Passport Details")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Passport Number",
                value = state.passportNumber,
                onValueChange = { viewModel.updatePassportNumber(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    label = "Issue Date",
                    value = state.issueDate,
                    onValueChange = { viewModel.updateIssueDate(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa"
                )
                CustomTextField(
                    label = "Expiration Date",
                    value = state.expirationDate,
                    onValueChange = { viewModel.updateExpirationDate(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = "jj/mm/aaaa"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Issuing Authority",
                value = state.issuingAuthority,
                onValueChange = { viewModel.updateIssuingAuthority(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("✈️", "Contact Information")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Email",
                value = state.email,
                onValueChange = { viewModel.updateEmail(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Phone number",
                value = state.phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                placeholder = "123-456-7890",
                leadingIcon = {
                    Text("🇩🇿", modifier = Modifier.padding(start = 12.dp, end = 8.dp), fontSize = 20.sp)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onConfirm,
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
                color = if (isActive) Color(0xFF4AC29A) else Color(0xFFE0E0E0),
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
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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
