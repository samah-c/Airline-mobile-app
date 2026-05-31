package com.example.airline.ui.checkin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OffBg    = Color(0xFFF2F2F7)
private val OffBlue  = Color(0xFF1849D6)
private val OffLabel = Color(0xFF9E9E9E)
private val OffDark  = Color(0xFF0D0B26)

data class SavedBoardingPass(
    val date: String,
    val time: String,
    val originCode: String,
    val originCity: String,
    val destCode: String,
    val destCity: String
)

@Composable
fun BoardingPassOfflineScreen(onBack: () -> Unit = {}) {
    val savedPasses = listOf(
        SavedBoardingPass("Feb 18, 2022", "08:15 AM", "LON", "London", "RIO", "Rio de Janeiro"),
        SavedBoardingPass("Feb 18, 2022", "08:15 AM", "LON", "London", "RIO", "Rio de Janeiro")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffBg)
    ) {
        // ── Header ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OffBg)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = OffBlue,
                modifier           = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
                    .size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Offline icon ──────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE8EEFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.WifiOff,
                    contentDescription = null,
                    tint               = OffBlue,
                    modifier           = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text       = "Offline Mode",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = OffDark
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text      = "Your boarding passes are saved locally and\naccessible without an internet connection",
                fontSize  = 14.sp,
                color     = OffLabel,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Section label ─────────────────────────────────
            Text(
                text     = "Saved boarding passes",
                fontSize = 13.sp,
                color    = OffLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // ── Boarding pass cards ───────────────────────────
            savedPasses.forEach { pass ->
                SavedPassCard(pass)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(12.dp))

            // ── Sync info card ────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Info,
                    contentDescription = null,
                    tint               = OffLabel,
                    modifier           = Modifier.size(20.dp)
                )
                Text(
                    text      = "Synchronization happens automatically when the connection is restored. No action required.",
                    fontSize  = 13.sp,
                    color     = OffLabel,
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SavedPassCard(pass: SavedBoardingPass) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        // Date + time row
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pass.date, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OffDark)
            Text(text = pass.time, fontSize = 13.sp, color = OffDark)
        }

        // Perforation line
        Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            val cy = size.height / 2f
            val nr = 10.dp.toPx()
            drawCircle(OffBlue, nr, Offset(0f, cy))
            drawCircle(OffBlue, nr, Offset(size.width, cy))
            drawLine(
                color       = OffBlue,
                start       = Offset(nr, cy),
                end         = Offset(size.width - nr, cy),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect  = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )
        }

        // Route row
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Departure
            Column {
                Text(pass.originCity, fontSize = 12.sp, color = OffBlue)
                Text(pass.originCode, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = OffDark, lineHeight = 46.sp)
            }

            // Dashed line + airplane
            Box(Modifier.weight(1f).height(28.dp).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxWidth().height(2.dp)) {
                    val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    drawLine(OffBlue, Offset(0f, 0f), Offset(size.width * 0.38f, 0f), 1.5.dp.toPx(), pathEffect = dash)
                    drawLine(OffBlue, Offset(size.width * 0.62f, 0f), Offset(size.width, 0f), 1.5.dp.toPx(), pathEffect = dash)
                }
                Text("✈", fontSize = 20.sp, color = OffBlue)
            }

            // Arrival
            Column(horizontalAlignment = Alignment.End) {
                Text(pass.destCity, fontSize = 12.sp, color = OffBlue, textAlign = TextAlign.End)
                Text(pass.destCode, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = OffDark, lineHeight = 46.sp)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 820)
@Composable
fun BoardingPassOfflineScreenPreview() {
    BoardingPassOfflineScreen()
}
