package com.example.airline.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.example.airline.R
import com.example.airline.ui.theme.PrimaryBlue

// ── Data ────────────────────────────────────────────────────────────────────

private data class OnboardingPageData(
    val backgroundRes: Int,
    val headline: String
)

private val pages = listOf(
    OnboardingPageData(
        backgroundRes = R.drawable.onboarding_bg1,
        headline = "Travel smarter.\nSkip lines."
    ),
    OnboardingPageData(
        backgroundRes = R.drawable.onboarding_bg2,
        headline = "Passeport scanned.\nSeat confirmed."
    ),
    OnboardingPageData(
        backgroundRes = R.drawable.onboarding_bg3,
        headline = "Enjoy the trip.\nFly stress-free."
    )
)

// ── Main screen ──────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            PageBackground(data = pages[pageIndex])
        }

        // Fixed bottom overlay: button + dots
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GetStartedButton(onClick = onGetStarted)
            Spacer(modifier = Modifier.height(20.dp))
            DotIndicators(pagerState = pagerState, pageCount = pages.size)
        }
    }
}

// ── Page background + headline ───────────────────────────────────────────────

@Composable
private fun PageBackground(data: OnboardingPageData) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = data.backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark scrim so white text is always readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Headline — lower-left, above the button area
        Text(
            text = data.headline,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 28.dp, bottom = 160.dp),
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 44.sp
        )
    }
}

// ── "Get Started" button ─────────────────────────────────────────────────────

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = PrimaryBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Get Started",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Dot indicators ────────────────────────────────────────────────────────────

@Composable
private fun DotIndicators(pagerState: PagerState, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Dot(isActive = index == pagerState.currentPage)
        }
    }
}

@Composable
private fun Dot(isActive: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isActive) 24.dp else 8.dp,
        animationSpec = tween(durationMillis = 250),
        label = "dot_width"
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .background(
                color = if (isActive) PrimaryBlue else Color.White.copy(alpha = 0.5f),
                shape = CircleShape
            )
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(onGetStarted = {})
}
