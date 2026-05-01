package com.example.airline.view.passeport

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
import com.example.airline.utils.MrzData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDetailsScreen(
    mrzData: MrzData,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var lastName by remember { mutableStateOf(mrzData.surname) }
    var firstName by remember { mutableStateOf(mrzData.givenNames) }
    var dob by remember { mutableStateOf(formatDate(mrzData.dateOfBirth)) }
    var gender by remember { mutableStateOf(mrzData.sex) }
    var nationality by remember { mutableStateOf(mrzData.nationality) }

    var passportNumber by remember { mutableStateOf(mrzData.passportNumber) }
    var issueDate by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf(formatDate(mrzData.expiryDate)) }
    var issuingAuthority by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Confirm your details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color(0xFF1942D8)
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

            // Step Indicator
            Text(
                text = "STEP 2 OF 5",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Personal Information
            SectionTitle("✈️", "Personal Information")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Last Name", value = lastName, onValueChange = { lastName = it })
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "First Name", value = firstName, onValueChange = { firstName = it })
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    label = "Date of birth",
                    value = dob,
                    onValueChange = { dob = it },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Color.Gray)
                    }
                )
                CustomTextField(label = "Gender", value = gender, onValueChange = { gender = it }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Nationality", value = nationality, onValueChange = { nationality = it })

            Spacer(modifier = Modifier.height(32.dp))

            // Passeport Details
            SectionTitle("✈️", "Passeport Details")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Passeport Number", value = passportNumber, onValueChange = { passportNumber = it })
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(label = "Issue Date", value = issueDate, onValueChange = { issueDate = it }, modifier = Modifier.weight(1f), placeholder = "jj/mm/aaaa")
                CustomTextField(label = "Expiration Date", value = expirationDate, onValueChange = { expirationDate = it }, modifier = Modifier.weight(1f), placeholder = "jj/mm/aaaa")
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Issuing Authority", value = issuingAuthority, onValueChange = { issuingAuthority = it })

            Spacer(modifier = Modifier.height(32.dp))

            // Contact Information
            SectionTitle("✈️", "Contact Information")
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Email", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.height(16.dp))
            // Simple Phone Number Field (In a real app, use a proper phone input component)
            CustomTextField(label = "Phone number", value = phoneNumber, onValueChange = { phoneNumber = it }, placeholder = "123-456-7890", leadingIcon = {
                Text("🇺🇸", modifier = Modifier.padding(start = 12.dp, end = 8.dp), fontSize = 20.sp)
            })

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
        Text(
            text = "$icon - - -",
            fontSize = 16.sp,
            color = Color(0xFF1942D8),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
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
        label = { Text(label, color = Color.Gray, fontSize = 12.sp) },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, color = Color.LightGray) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFD1D5DB),
            focusedBorderColor = Color(0xFF1942D8),
            unfocusedContainerColor = Color(0xFFF9FAFB),
            focusedContainerColor = Color(0xFFF9FAFB),
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        ),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        singleLine = true
    )
}

private fun formatDate(mrzDate: String): String {
    // Convert YYMMDD to jj/mm/aaaa as much as possible, or keep as is
    if (mrzDate.length == 6) {
        val yy = mrzDate.substring(0, 2)
        val mm = mrzDate.substring(2, 4)
        val dd = mrzDate.substring(4, 6)
        
        // Simple heuristic for 19xx vs 20xx
        val yyInt = yy.toIntOrNull() ?: 0
        val yearPrefix = if (yyInt > 30) "19" else "20"
        return "$dd/$mm/$yearPrefix$yy"
    }
    return mrzDate
}
