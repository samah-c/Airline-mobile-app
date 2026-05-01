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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colors ────────────────────────────────────────────────────────────────────
private val BpBg     = Color(0xFFF2F2F7)
private val BpBlue   = Color(0xFF1849D6)
private val BpLabel  = Color(0xFF9E9E9E)
private val BpDark   = Color(0xFF0D0B26)
private val BpOrange = Color(0xFFFF6B1A)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun BoardingPassScreen(onBack: () -> Unit = {}, onDownload: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(BpBg)) {
        BpHeader(onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TicketCard()
            BarcodeCard()
        }
        BpDownloadButton(onDownload)
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun BpHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text     = "‹",
            fontSize = 28.sp,
            color    = BpBlue,
            modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() }
        )
        Text(
            text       = "Flight LH007",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = BpDark,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}

// ── Ticket card ───────────────────────────────────────────────────────────────

@Composable
private fun TicketCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        // ① Airline logo + Flight / Gate
        Row(
            modifier                  = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement     = Arrangement.SpaceBetween,
            verticalAlignment         = Alignment.CenterVertically
        ) {
            Text("GOL", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = BpOrange)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                BpLabelValue("Flight", "LH007")
                BpLabelValue("Gate",   "A2")
            }
        }

        Perforation()

        // ② Route (LON → RIO) + times
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Departure city
                Column(Modifier.weight(1f)) {
                    Text("London", fontSize = 13.sp, color = BpLabel)
                    Text(
                        "LON",
                        fontSize   = 50.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = BpDark,
                        lineHeight = 56.sp
                    )
                }
                // Dashed line + airplane
                Box(
                    modifier         = Modifier.weight(1.2f).height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxWidth().height(2.dp)) {
                        val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        drawLine(BpBlue, Offset(0f, 0f), Offset(size.width * 0.36f, 0f), 1.5.dp.toPx(), pathEffect = dash)
                        drawLine(BpBlue, Offset(size.width * 0.64f, 0f), Offset(size.width, 0f), 1.5.dp.toPx(), pathEffect = dash)
                    }
                    Text("✈", fontSize = 22.sp, color = BpBlue)
                }
                // Arrival city
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Rio de Janeiro", fontSize = 13.sp, color = BpLabel, textAlign = TextAlign.End)
                    Text(
                        "RIO",
                        fontSize   = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = BpDark,
                        textAlign  = TextAlign.End,
                        lineHeight = 56.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Times row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BpTimeColumn("Boarding Time", "08:15 AM")
                BpTimeColumn("Departs",       "08:45 AM")
                BpTimeColumn("Departs",       "12:00 PM")
            }
        }

        Perforation()

        // ③ Passenger + Seat
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Bottom
        ) {
            Column {
                Text("Passenger", fontSize = 12.sp, color = BpLabel)
                Text("Jon Bon Jovi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BpBlue)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Seat", fontSize = 12.sp, color = BpLabel)
                Text("3F", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BpBlue)
            }
        }

        Perforation()
    }
}

// ── Reusable ticket sub-composables ───────────────────────────────────────────

@Composable
private fun BpLabelValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = BpLabel)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BpBlue)
    }
}

@Composable
private fun BpTimeColumn(label: String, time: String) {
    Column {
        Text(label, fontSize = 11.sp, color = BpLabel)
        Text(time, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BpBlue)
    }
}

// Dashed line with blue semicircle notches at card edges
@Composable
private fun Perforation() {
    Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
        val cy = size.height / 2f
        val nr = 12.dp.toPx()
        // Half-circles: drawn at x=0 and x=width — Canvas clips them to the card edge
        drawCircle(BpBlue, nr, Offset(0f,          cy))
        drawCircle(BpBlue, nr, Offset(size.width,  cy))
        // Dashed line between them
        drawLine(
            color       = BpBlue,
            start       = Offset(nr, cy),
            end         = Offset(size.width - nr, cy),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect  = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )
    }
}

// ── Barcode card ──────────────────────────────────────────────────────────────

@Composable
private fun BarcodeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BarcodeCanvas(code = "83GKS902KGM3017SD", modifier = Modifier.fillMaxWidth().height(80.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text          = "83GKS902KGM3017SD",
            fontSize      = 12.sp,
            letterSpacing = 2.sp,
            color         = BpDark
        )
    }
}

@Composable
private fun BarcodeCanvas(code: String, modifier: Modifier = Modifier) {
    val widths = remember(code) {
        buildList {
            code.forEach { c ->
                val v = c.code
                add(((v % 3) + 1).toFloat())         // bar width
                add(((v ushr 2 and 3) + 1).toFloat()) // space width
            }
        }
    }
    val total = widths.sum()
    Canvas(modifier) {
        if (total == 0f) return@Canvas
        val unitW = size.width / total
        var x = 0f
        widths.forEachIndexed { i, w ->
            val barW = w * unitW
            if (i % 2 == 0) {
                drawRect(Color.Black, Offset(x, 0f), Size(barW, size.height))
            }
            x += barW
        }
    }
}

// ── Download button ───────────────────────────────────────────────────────────

@Composable
private fun BpDownloadButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BpBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = BpBlue)
        ) {
            Text("Download", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, heightDp = 820)
@Composable
fun BoardingPassScreenPreview() {
    BoardingPassScreen()
}
