package com.example.airline.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.R
import com.example.airline.ui.theme.DarkText
import com.example.airline.ui.theme.GrayText
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onNavigateToOnboarding()
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter            = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = "Airline logo",
            modifier           = Modifier.size(88.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text          = "Airline",
            fontSize      = 28.sp,
            fontWeight    = FontWeight.Bold,
            color         = DarkText,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text       = "Your travelling companion",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Normal,
            color      = GrayText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(onNavigateToOnboarding = {})
}
