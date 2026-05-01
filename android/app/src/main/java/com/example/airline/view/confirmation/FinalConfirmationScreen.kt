package com.example.airline.view.confirmation

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.utils.MrzData
import com.example.airline.view.luggage.SectionHeader
import com.example.airline.view.passeport.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalConfirmationScreen(
    mrzData: MrzData,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Final confirmation",
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

            // Personal Information
            SectionHeader(icon = Icons.Default.Flight, title = "Personal Information")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Last Name", value = mrzData.surname)
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "First Name", value = mrzData.givenNames)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyTextField(
                    label = "Date of birth",
                    value = formatDate(mrzData.dateOfBirth),
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray) }
                )
                ReadOnlyTextField(label = "Gender", value = mrzData.sex, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "Nationality", value = mrzData.nationality)

            Spacer(modifier = Modifier.height(24.dp))

            // Passeport Details
            SectionHeader(icon = Icons.Default.Flight, title = "Passeport Details")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Passeport Number", value = mrzData.passportNumber)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyTextField(label = "Issue Date", value = "jj/mm/aaaa", modifier = Modifier.weight(1f))
                ReadOnlyTextField(label = "Expiration Date", value = formatDate(mrzData.expiryDate), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "Issuing Authority", value = mrzData.nationality)

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Information
            SectionHeader(icon = Icons.Default.Flight, title = "Contact Information")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Email", value = "user@example.com")
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlyTextField(label = "Phone number", value = "123-456-7890", leadingIcon = { Text("🇺🇸", modifier = Modifier.padding(start = 12.dp, end = 8.dp)) })

            Spacer(modifier = Modifier.height(32.dp))

            // Boarding pass
            SectionHeader(icon = Icons.Default.Flight, title = "Boarding pass")
            Spacer(modifier = Modifier.height(16.dp))
            
            BoardingPassCard(
                passengerName = "${mrzData.givenNames} ${mrzData.surname}".trim().ifEmpty { "Jon Bon Jovi" }
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value.ifEmpty { "-" }, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun BoardingPassCard(passengerName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("GOL", color = Color(0xFFF97316), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Flight", fontSize = 10.sp, color = Color.Gray)
                        Text("LH007", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Gate", fontSize = 10.sp, color = Color.Gray)
                        Text("A2", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                    }
                }
            }

            // Dashed Line with Cutouts
            CutoutDashedLine()

            // Flight Path
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("London", fontSize = 12.sp, color = Color(0xFF1942D8), fontWeight = FontWeight.Bold)
                    Text("LON", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Flight, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(32.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("Rio de Janeiro", fontSize = 12.sp, color = Color(0xFF1942D8), fontWeight = FontWeight.Bold)
                    Text("RIO", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Times
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Boarding Time", fontSize = 10.sp, color = Color.Gray)
                    Text("08:15 AM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column {
                    Text("Departs", fontSize = 10.sp, color = Color.Gray)
                    Text("08:45 AM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Arrives", fontSize = 10.sp, color = Color.Gray)
                    Text("12:00 PM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CutoutDashedLine()
            Spacer(modifier = Modifier.height(16.dp))

            // Passenger Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Passenger", fontSize = 10.sp, color = Color.Gray)
                    Text(passengerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Seat", fontSize = 10.sp, color = Color.Gray)
                    Text("3F", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1942D8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CutoutDashedLine()
            Spacer(modifier = Modifier.height(24.dp))

            // Barcode Mock
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Simulate barcode lines
                Row(modifier = Modifier.height(60.dp), horizontalArrangement = Arrangement.Center) {
                    val widths = listOf(2.dp, 4.dp, 1.dp, 6.dp, 2.dp, 3.dp, 1.dp, 4.dp, 2.dp, 5.dp, 1.dp, 2.dp, 4.dp, 2.dp, 1.dp, 3.dp, 2.dp, 4.dp, 1.dp, 6.dp)
                    for (width in widths) {
                        Box(modifier = Modifier
                            .width(width)
                            .fillMaxHeight()
                            .background(Color.Black))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("8 3 6 K 8 9 0 2 K G M 3 Q 1 7 S 0", fontSize = 10.sp, letterSpacing = 2.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CutoutDashedLine() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left cutout
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 20.dp)
                .background(
                    color = Color(0xFF1942D8),
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                )
        )
        // Dashed line
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
        // Right cutout
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

private fun formatDate(mrzDate: String): String {
    if (mrzDate.length == 6) {
        val yy = mrzDate.substring(0, 2)
        val mm = mrzDate.substring(2, 4)
        val dd = mrzDate.substring(4, 6)
        val yyInt = yy.toIntOrNull() ?: 0
        val yearPrefix = if (yyInt > 30) "19" else "20"
        return "$dd/$mm/$yearPrefix$yy"
    }
    return mrzDate
}
