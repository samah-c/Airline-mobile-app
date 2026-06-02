package com.example.airline.ui.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.airline.R

private val BrandBlue         = Color(0xFF2B4EFF)
private val CardGradientStart = Color(0xFFE91E8C)
private val CardGradientEnd   = Color(0xFFFF6B6B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchFlight: (pnr: String, lastName: String) -> Unit = { _, _ -> },
    onNavigateBaggage: () -> Unit = {},
    onNavigateCheckin: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // No Scaffold — just a plain Column so the top bar truly sticks to the top
    // regardless of any outer Scaffold padding.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Pinned top bar ─────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text = "Home",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // ── Scrollable content below ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            GreetingSection(userName = uiState.userName)

            Spacer(modifier = Modifier.height(24.dp))

            FlightLookUpSection(
                pnr = uiState.pnr,
                lastName = uiState.lastName,
                onPnrChange = viewModel::onPnrChange,
                onLastNameChange = viewModel::onLastNameChange,
                onSearch = {
                    viewModel.validateAndSearch { pnr, lastName ->
                        onSearchFlight(pnr, lastName)
                    }
                },
                errorMessage = uiState.errorMessage
            )

            Spacer(modifier = Modifier.height(24.dp))

            CheckInPromoCard(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Greeting ──────────────────────────────────────────────────────────────────
@Composable
private fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFCDD2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFE91E8C),
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Hi, $userName !", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = "Welcome back Traveler", fontSize = 13.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Acces your flight information and check-in", fontSize = 13.sp, color = Color.Gray)
    }
}

// ── Flight Look-Up ────────────────────────────────────────────────────────────
@Composable
private fun FlightLookUpSection(
    pnr: String,
    lastName: String,
    errorMessage: String?,
    onPnrChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Flight LookUp", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Booking Reference Number (PNR)", fontSize = 12.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = pnr,
            onValueChange = onPnrChange,
            placeholder = { Text("Input your booking reference", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandBlue
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Last Name", fontSize = 12.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = { Text("Input your last name", color = Color.LightGray, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandBlue
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
        ) {
            Text(text = "Search", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, fontSize = 13.sp)
        }
    }
}

// ── Check-In Promo Card ───────────────────────────────────────────────────────
@Composable
private fun CheckInPromoCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.linearGradient(colors = listOf(CardGradientStart, CardGradientEnd)))
    ) {
        Text(
            text = "Check-In For\nyour Flight!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.homecheckin),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}