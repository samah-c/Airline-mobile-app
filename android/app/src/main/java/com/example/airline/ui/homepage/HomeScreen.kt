package com.example.airline.ui.homepage

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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.airline.R
// ── Brand colors (keep in sync with Theme.kt) ──────────────────────────────
private val BrandBlue   = Color(0xFF2B4EFF)
private val BrandPink   = Color(0xFFE91E8C)
private val CardGradientStart = Color(0xFFE91E8C)
private val CardGradientEnd   = Color(0xFFFF6B6B)

@Composable
fun HomeScreen(
    onSearchFlight: (pnr: String, lastName: String) -> Unit = { _, _ -> },
    onNavigateBaggage: () -> Unit = {},
    onNavigateCheckin: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            HomeTopBar()

            // ── Greeting ───────────────────────────────────────────────────
            GreetingSection(userName = uiState.userName)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Flight LookUp ──────────────────────────────────────────────
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

            // ── Check-In promo card ────────────────────────────────────────
            CheckInPromoCard(
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        }
    }
}

// ── Top bar ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
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
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// ── Greeting ─────────────────────────────────────────────────────────────────
@Composable
private fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar placeholder
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
                Text(
                    text = "Hi, $userName !",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Welcome back Traveler",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Acces your flight information and check-in",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

// ── Flight Look-Up ───────────────────────────────────────────────────────────
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
        Text(
            text = "Flight LookUp",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PNR field
        Text(
            text = "Booking Reference Number (PNR)",
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = pnr,
            onValueChange = onPnrChange,
            placeholder = {
                Text("Input your booking reference", color = Color.LightGray, fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandBlue
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Last name field
        Text(
            text = "Last Name",
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = {
                Text("Input your last name", color = Color.LightGray, fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandBlue
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Search button
        Button(
            onClick = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
        ) {
            Text(
                text = "Search",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
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
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(CardGradientStart, CardGradientEnd)
                )
            )
    ) {
        // Text at the top
        Text(
            text = "Check-In For\nyour Flight!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )

        // Image fills the remaining space below the text
        Image(
            painter = painterResource(id = R.drawable.homecheckin),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}