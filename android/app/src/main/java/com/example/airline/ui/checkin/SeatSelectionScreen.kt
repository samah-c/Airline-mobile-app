package com.example.airline.ui.checkin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.R

// ── Colors ────────────────────────────────────────────────────────────────────
private val PageBg              = Color(0xFF2B3EDF)
private val AirplaneOuter       = Color(0xFF7B8ED8)  // periwinkle
private val AirplaneInner       = Color.White
private val FirstClassAvailable = Color(0xFF4CD964)  // Figma: #4CD964 green
private val SeatOccupied        = Color(0xFFE9E8FC)  // Figma: #E9E8FC purple extra light
private val EconomyAvailable    = Color(0xFF1849D6)  // Figma: #1849D6 blue
private val SelectedColor       = Color(0xFFEF4444)  // Figma: coral red for selected

// ── SVG-derived layout constants (SVG viewBox = 375 wide = screen dp width) ──
private const val SVG_W        = 375f
private const val BODY_L       = 16.4f    // airplane body left edge (dp)
private const val BODY_R       = 269.6f   // airplane body right edge (dp)
private const val INNER_L      = 33f      // white seat area left (dp)
private const val INNER_R      = 248f     // white seat area right (dp)
private const val NOSE_SVG_H   = 448f     // SVG units for nose height
private const val PLANE_CENTER = 143f     // center x (dp)

// ── Models ────────────────────────────────────────────────────────────────────
private enum class SeatClass { FIRST, ECONOMY }
private enum class SeatState { AVAILABLE, OCCUPIED }
private data class SeatInfo(val id: String, val seatClass: SeatClass, val state: SeatState)

private sealed class RowItem {
    data class SeatRow(val rowNum: Int, val seats: List<SeatInfo?>) : RowItem()
    object ExitRow  : RowItem()
    object Wings    : RowItem()
    object TailFins : RowItem()
}

// ── Seat data ─────────────────────────────────────────────────────────────────
private val fcOccupied = setOf("1B", "1C", "3A", "5C", "5D")
private val ecoOccupied = setOf(
    "6A","6B","6D","6E","7B","7D","7E","7F",
    "8A","8C","8D","8F","9A","9B","9C","9E",
    "10D","10E","10F","11A","11B","11D","11E",
    "12A","12B","12D","12E","12F","13A","13C","13E","13F",
    "14A","14D","15B","15C","15E","16A","16D","16E","16F",
    "17A","17C","17D","18B","18C","18D","18E",
    "19A","19B","19D","20A","20B","20C","20F",
    "21D","21E","22A","22B","22C","22E",
    "23A","23B","23D","23E","23F","24A","24C","24D","24E",
    "25B","25C","25D","25F","26A","26B","26D",
    "27A","27C","27D","27E","27F",
    "28B","28C","28E","29A","29D","30B","30C","30D",
    "31A","31C","32A","32B","32D","32E","33A","33B","33C","33E"
)

private fun seat(id: String, cls: SeatClass, occ: Set<String>) =
    SeatInfo(id, cls, if (id in occ) SeatState.OCCUPIED else SeatState.AVAILABLE)

private fun ecoRow(r: Int) = RowItem.SeatRow(r, listOf(
    seat("${r}A", SeatClass.ECONOMY, ecoOccupied),
    seat("${r}B", SeatClass.ECONOMY, ecoOccupied),
    seat("${r}C", SeatClass.ECONOMY, ecoOccupied),
    null,
    seat("${r}D", SeatClass.ECONOMY, ecoOccupied),
    seat("${r}E", SeatClass.ECONOMY, ecoOccupied),
    seat("${r}F", SeatClass.ECONOMY, ecoOccupied)
))

private fun buildSeatMap(): List<RowItem> = buildList {
    for (r in 1..5) add(RowItem.SeatRow(r, listOf(
        seat("${r}A", SeatClass.FIRST, fcOccupied),
        seat("${r}B", SeatClass.FIRST, fcOccupied),
        null,
        seat("${r}C", SeatClass.FIRST, fcOccupied),
        seat("${r}D", SeatClass.FIRST, fcOccupied)
    )))
    add(RowItem.ExitRow)
    for (r in 6..13)  add(ecoRow(r))
    add(RowItem.ExitRow)
    add(RowItem.Wings)
    for (r in 14..18) add(ecoRow(r))
    add(RowItem.ExitRow)
    for (r in 19..28) add(ecoRow(r))
    add(RowItem.ExitRow)
    for (r in 29..33) add(ecoRow(r))
    add(RowItem.TailFins)
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun SeatSelectionScreen(
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
    viewModel: SeatSelectionViewModel = viewModel(factory = SeatSelectionViewModel.Factory())
) {
    val uiState   by viewModel.uiState.collectAsState()
    val selectedSeats = uiState.selectedSeats
    val seatMap       = remember { buildSeatMap() }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SeatHeader(onBack)
        SeatLegend()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(PageBg)
        ) {
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                item { NoseSection() }

                items(seatMap) { item ->
                    when (item) {
                        is RowItem.SeatRow -> SeatRowItem(item, selectedSeats) { id ->
                            viewModel.toggleSeat(id)
                        }
                        RowItem.ExitRow  -> ExitRowContent()
                        RowItem.Wings    -> WingsSection()
                        RowItem.TailFins -> TailFinsSection()
                    }
                }

                item { TailSection() }
            }

            // Flight info — fixed top-right, does not scroll
            FlightInfoPanel(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 10.dp)
                    .width(100.dp)
            )

            // Next button — fixed bottom-right
            NextButton(
                selectedCount = selectedSeats.size,
                onNext        = onNext,
                modifier      = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 16.dp)
            )
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun SeatHeader(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text     = "‹",
                fontSize = 30.sp,
                color    = Color(0xFF2541EE),
                modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() }
            )
            Text(
                text       = "Choose Your Seat",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A2E),
                modifier   = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text          = "STEP 3 OF 5",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Bold,
            color         = Color(0xFF6B7280),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f).height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i < 3) Color(0xFF1849D6) else Color(0xFFE5E7EB))
                )
            }
        }
    }
}

// ── Seat legend ───────────────────────────────────────────────────────────────

@Composable
private fun SeatLegend() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.width(44.dp).height(2.dp)) {
                drawLine(
                    color       = Color(0xFF9CA3AF),
                    start       = Offset(0f, 0f),
                    end         = Offset(size.width, 0f),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect  = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "✈", fontSize = 14.sp, color = Color(0xFF2541EE))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Seat Legend", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            LegendItem(FirstClassAvailable, "First Class")
            LegendItem(EconomyAvailable,    "Economy")
            LegendItem(SeatOccupied,        "Occupied")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(14.dp).background(color, RoundedCornerShape(3.dp)))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF374151))
    }
}

// ── Airplane nose — exact SVG bezier path ─────────────────────────────────────

@Composable
private fun NoseSection() {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp).background(PageBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xS = size.width / SVG_W
            val yS = size.height / NOSE_SVG_H

            val nosePath = Path().apply {
                moveTo(143f * xS, 0.5f * yS)
                cubicTo(147.02f*xS, 0.5f*yS,   151.08f*xS, 2.43f*yS,   155.16f*xS, 6.06f*yS)
                cubicTo(159.24f*xS, 9.70f*yS,   163.30f*xS, 15.01f*yS,  167.31f*xS, 21.69f*yS)
                cubicTo(175.34f*xS, 35.04f*yS,  183.13f*xS, 53.77f*yS,  190.47f*xS, 75.22f*yS)
                cubicTo(205.14f*xS, 118.13f*yS, 218.01f*xS, 171.86f*yS, 227.51f*xS, 215.11f*yS)
                cubicTo(241.81f*xS, 280.19f*yS, 259.31f*xS, 373.91f*yS, 266.41f*xS, 412.49f*yS)
                cubicTo(268.56f*xS, 424.19f*yS, 269.62f*xS, 436.03f*yS, 269.62f*xS, 447.93f*yS)
                lineTo(16.38f*xS, 447.03f*yS)
                cubicTo(16.38f*xS, 435.72f*yS,  17.34f*xS, 424.46f*yS,  19.30f*xS, 413.33f*yS)
                cubicTo(26.03f*xS, 375.25f*yS,  43.17f*xS, 280.49f*yS,  58.49f*xS, 215.11f*yS)
                cubicTo(68.74f*xS, 171.36f*yS,  81.61f*xS, 117.63f*yS,  96.10f*xS, 74.85f*yS)
                cubicTo(103.35f*xS,53.45f*yS,   110.99f*xS,34.82f*yS,   118.90f*xS,21.54f*yS)
                cubicTo(122.85f*xS,14.91f*yS,   126.86f*xS,9.63f*yS,    130.90f*xS,6.02f*yS)
                cubicTo(134.94f*xS,2.41f*yS,    138.98f*xS,0.5f*yS,     143f*xS,   0.5f*yS)
                close()
            }
            drawPath(nosePath, AirplaneOuter)

            // White inner — tiny rounded cap only at very bottom, to transition into seat rows
            val innerLx = INNER_L * xS
            val innerW  = (INNER_R - INNER_L) * xS
            val startY  = size.height * 0.88f
            drawRoundRect(
                color        = AirplaneInner,
                topLeft      = Offset(innerLx, startY),
                size         = Size(innerW, size.height - startY + 4f),
                cornerRadius = CornerRadius(24f * xS, 24f * xS)
            )
        }
    }
}

// ── Wings — swept-back curved protrusion, Emirates logo in body ───────────────

@Composable
private fun WingsSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(PageBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Mirror PlaneBodyRow's padding logic exactly:
            //   outer left  = BODY_L.dp
            //   outer right = size.width - (SVG_W - BODY_R).dp   ← screen-width-aware
            //   inner left  = INNER_L.dp
            //   inner right = size.width - (SVG_W - INNER_R).dp  ← screen-width-aware
            val bodyL  = BODY_L.dp.toPx()
            val bodyR  = size.width - (SVG_W - BODY_R).dp.toPx()
            val innerL = INNER_L.dp.toPx()
            val innerR = size.width - (SVG_W - INNER_R).dp.toPx()

            // Main body stripe
            drawRect(AirplaneOuter, Offset(bodyL, 0f), Size(bodyR - bodyL, size.height))

            // Right wing: shoots to screen edge almost instantly, then sweeps back
            val rWing = Path().apply {
                moveTo(bodyR, 0f)
                cubicTo(
                    size.width * 0.95f, size.height * 0.04f,
                    size.width,          size.height * 0.18f,
                    size.width,          size.height * 0.45f
                )
                lineTo(size.width, size.height)
                lineTo(bodyR,      size.height)
                close()
            }
            drawPath(rWing, AirplaneOuter)

            // Left wing: mirror
            val lWing = Path().apply {
                moveTo(bodyL, 0f)
                cubicTo(
                    size.width * 0.05f, size.height * 0.04f,
                    0f,                 size.height * 0.18f,
                    0f,                 size.height * 0.45f
                )
                lineTo(0f,    size.height)
                lineTo(bodyL, size.height)
                close()
            }
            drawPath(lWing, AirplaneOuter)

            // White inner — same bounds as PlaneBodyRow inner box
            drawRect(AirplaneInner, Offset(innerL, 0f), Size(innerR - innerL, size.height))
        }

        // Emirates logo — faint watermark, centred using the same padding as PlaneBodyRow inner
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = INNER_L.dp, end = (SVG_W - INNER_R).dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter            = painterResource(id = R.drawable.emirates_logo),
                contentDescription = null,
                modifier           = Modifier
                    .size(width = 86.dp, height = 44.dp)
                    .alpha(0.08f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// ── Tail fins — large swept-back protrusion before the tail ──────────────────

@Composable
private fun TailFinsSection() {
    Canvas(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(PageBg)
    ) {
        val bodyL = BODY_L.dp.toPx()
        val bodyR = size.width - (SVG_W - BODY_R).dp.toPx()

        // Body stripe — screen-width-aware to match PlaneBodyRow exactly
        drawRect(AirplaneOuter, Offset(bodyL, 0f), Size(bodyR - bodyL, size.height))

        // Right fin: large curved sweep to screen edge
        val rFin = Path().apply {
            moveTo(bodyR, 0f)
            cubicTo(
                size.width * 0.78f, size.height * 0.25f,
                size.width,         size.height * 0.60f,
                size.width,         size.height
            )
            lineTo(bodyR, size.height)
            close()
        }
        drawPath(rFin, AirplaneOuter)

        // Left fin: mirror
        val lFin = Path().apply {
            moveTo(bodyL, 0f)
            cubicTo(
                size.width * 0.22f, size.height * 0.25f,
                0f,                 size.height * 0.60f,
                0f,                 size.height
            )
            lineTo(bodyL, size.height)
            close()
        }
        drawPath(lFin, AirplaneOuter)
        // No white inner — seat column ends at the last PlaneBodyRow above
    }
}

// ── Tail — exact U-notch from SVG (offset from y=2481) ───────────────────────

@Composable
private fun TailSection() {
    Canvas(
        modifier = Modifier.fillMaxWidth().height(120.dp).background(PageBg)
    ) {
        val xS  = size.width / SVG_W
        val yS  = size.height / 49.5f  // SVG tail tip height ≈ 49.5 units

        // Tail tip with U-notch (SVG coords offset so y=2481 → 0)
        val tailPath = Path().apply {
            moveTo(174.16f*xS, 0f)
            cubicTo(170.02f*xS, 13.75f*yS, 165f*xS,    24f*yS,    162.95f*xS, 31.71f*yS)
            cubicTo(160.64f*xS, 36.20f*yS, 157.78f*xS, 40.67f*yS, 154.43f*xS, 44.00f*yS)
            cubicTo(151.07f*xS, 47.34f*yS, 147.25f*xS, 49.50f*yS, 143.00f*xS, 49.50f*yS)
            cubicTo(138.75f*xS, 49.50f*yS, 134.93f*xS, 47.34f*yS, 131.58f*xS, 44.00f*yS)
            cubicTo(128.22f*xS, 40.67f*yS, 125.36f*xS, 36.20f*yS, 123.05f*xS, 31.71f*yS)
            cubicTo(120.73f*xS, 24f*yS,    117f*xS,    13.75f*yS,  112.46f*xS, 0f)
            close()
        }
        drawPath(tailPath, AirplaneOuter)
    }
}

// ── Shared body row wrapper ───────────────────────────────────────────────────

@Composable
private fun PlaneBodyRow(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PageBg)
    ) {
        // Periwinkle outer body (left-aligned per SVG: starts at BODY_L dp, ends at BODY_R dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = BODY_L.dp,
                    end   = (SVG_W - BODY_R).dp  // 105.4dp right margin = flight info area
                )
                .background(AirplaneOuter)
        ) {
            // White inner seat area
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (INNER_L - BODY_L).dp,   // 16.6dp
                        end   = (BODY_R - INNER_R).dp    // 21.6dp
                    )
                    .background(AirplaneInner),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

// ── Seat row ──────────────────────────────────────────────────────────────────

@Composable
private fun SeatRowItem(item: RowItem.SeatRow, selectedSeats: Set<String>, onSelect: (String) -> Unit) {
    val aisleIdx   = item.seats.indexOfFirst { it == null }.takeIf { it >= 0 } ?: item.seats.size
    val leftSeats  = item.seats.take(aisleIdx).filterNotNull()
    val rightSeats = item.seats.drop(aisleIdx + 1).filterNotNull()

    PlaneBodyRow {
        val isFirst = item.seats.filterNotNull().any { it.seatClass == SeatClass.FIRST }
        val gap     = if (isFirst) 4.dp else 5.dp
        Row(
            modifier              = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                leftSeats.forEach { s -> SeatItem(s, s.id in selectedSeats) { onSelect(s.id) } }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text      = item.rowNum.toString(),
                modifier  = Modifier.width(28.dp),
                fontSize  = 11.sp,
                color     = Color(0xFF7C8DB0),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                rightSeats.forEach { s -> SeatItem(s, s.id in selectedSeats) { onSelect(s.id) } }
            }
        }
    }
}

// ── Exit row ──────────────────────────────────────────────────────────────────

@Composable
private fun ExitRowContent() {
    PlaneBodyRow {
        Row(
            modifier              = Modifier.padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(14.dp).border(1.dp, Color(0xFF9CA3AF), CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Exit row", fontSize = 11.sp, color = Color(0xFF6B7280))
        }
    }
}

// ── Seat item ─────────────────────────────────────────────────────────────────

@Composable
private fun SeatItem(seat: SeatInfo, isSelected: Boolean, onClick: () -> Unit) {
    val bg = when {
        isSelected                                                                -> SelectedColor
        seat.state == SeatState.AVAILABLE && seat.seatClass == SeatClass.FIRST   -> FirstClassAvailable
        seat.state == SeatState.AVAILABLE && seat.seatClass == SeatClass.ECONOMY -> EconomyAvailable
        else                                                                      -> SeatOccupied
    }
    val w = if (seat.seatClass == SeatClass.FIRST) 30.dp else 22.dp
    val h = if (seat.seatClass == SeatClass.FIRST) 40.dp else 32.dp
    Box(
        modifier         = Modifier
            .size(width = w, height = h)
            .background(bg, RoundedCornerShape(4.dp))
            .clickable(enabled = seat.state == SeatState.AVAILABLE) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Text(
                text       = "✓",
                color      = Color.White,
                fontSize   = if (seat.seatClass == SeatClass.FIRST) 16.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Flight info panel ─────────────────────────────────────────────────────────

@Composable
private fun FlightInfoPanel(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CGK",       fontSize = 24.sp, fontWeight = FontWeight.Bold,  color = Color.White.copy(alpha = 0.92f))
        Text("Jakarta",   fontSize = 10.sp, color = Color.White.copy(alpha = 0.55f))
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(modifier = Modifier.size(width = 60.dp, height = 22.dp)) {
            drawArc(
                color      = Color.White.copy(alpha = 0.40f),
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft    = Offset(0f, 0f),
                size       = Size(size.width, size.height * 2f),
                style      = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f), 0f))
            )
        }
        Text("1h 35m",    fontSize = 10.sp, color = Color.White.copy(alpha = 0.55f))
        Text("✈",         fontSize = 13.sp, color = Color.White.copy(alpha = 0.70f))
        Spacer(modifier = Modifier.height(4.dp))
        Text("LCY",       fontSize = 24.sp, fontWeight = FontWeight.Bold,  color = Color.White.copy(alpha = 0.92f))
        Text("Jakarta",   fontSize = 10.sp, color = Color.White.copy(alpha = 0.55f))
        Spacer(modifier = Modifier.height(10.dp))
        Text("FLIGHT NO", fontSize = 8.sp,  letterSpacing = 0.5.sp, color = Color.White.copy(alpha = 0.55f))
        Text("KB767",     fontSize = 15.sp, fontWeight = FontWeight.Bold,  color = Color.White.copy(alpha = 0.92f))
        Spacer(modifier = Modifier.height(10.dp))
        Text("18",        fontSize = 30.sp, fontWeight = FontWeight.Bold,  color = Color.White.copy(alpha = 0.92f))
        Text("SEATS",     fontSize = 8.sp,  letterSpacing = 0.5.sp, color = Color.White.copy(alpha = 0.55f))
        Text("AVAILABLE", fontSize = 8.sp,  letterSpacing = 0.5.sp, color = Color.White.copy(alpha = 0.55f))
    }
}

// ── Next button — floating bottom-right ──────────────────────────────────────

@Composable
private fun NextButton(selectedCount: Int, onNext: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(28.dp))
            .background(Color.White, RoundedCornerShape(28.dp))
            .clickable { onNext() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selectedCount > 0) {
            Box(
                modifier         = Modifier
                    .size(22.dp)
                    .background(SelectedColor, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$selectedCount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = "Next",  color = PageBg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = "→",     color = PageBg, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, heightDp = 900)
@Composable
fun SeatSelectionScreenPreview() {
    SeatSelectionScreen()
}
