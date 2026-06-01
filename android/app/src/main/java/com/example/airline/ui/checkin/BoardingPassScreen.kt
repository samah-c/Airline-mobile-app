package com.example.airline.ui.checkin

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ── Colors ────────────────────────────────────────────────────────────────────
private val BpBg     = Color(0xFFF2F2F7)
private val BpBlue   = Color(0xFF1849D6)
private val BpLabel  = Color(0xFF9E9E9E)
private val BpDark   = Color(0xFF0D0B26)
private val BpOrange = Color(0xFFFF6B1A)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun BoardingPassScreen(
    checkInId: Int = 1,
    onBack: () -> Unit = {},
    onDownload: () -> Unit = {},
    viewModel: BoardingPassViewModel = viewModel(
        factory = BoardingPassViewModel.Factory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val bp      = uiState.boardingPass
    val context = LocalContext.current

    LaunchedEffect(checkInId) { viewModel.generateBoardingPass(checkInId) }

    // Save PDF when bytes arrive
    LaunchedEffect(uiState.pdfBytes) {
        uiState.pdfBytes?.let { bytes ->
            savePdfToDownloads(context, bytes, "boarding-pass-$checkInId.pdf")
            Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
            onDownload()
        }
    }

    Box(Modifier.fillMaxSize().background(BpBg)) {
        Column(Modifier.fillMaxSize()) {
            BpHeader(title = "Flight ${bp.flightNumber.ifEmpty { "—" }}", onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TicketCard(
                    flightNumber  = bp.flightNumber.ifEmpty  { "—" },
                    gate          = bp.gate.ifEmpty           { "—" },
                    origin        = bp.origin.ifEmpty         { "—" },
                    originCity    = bp.originCity.ifEmpty     { "—" },
                    destination   = bp.destination.ifEmpty   { "—" },
                    destCity      = bp.destinationCity.ifEmpty { "—" },
                    passengerName = bp.passengerName.ifEmpty  { "—" },
                    seat          = bp.seat.ifEmpty           { "—" },
                    boardingTime  = formatDateTime(bp.boardingTime),
                    departureTime = formatDateTime(bp.departureTime)
                )
                QRCodeCard(qrBase64 = bp.qrCode, fallbackCode = bp.barcode)
            }
            BpDownloadButton(
                isLoading = uiState.isDownloadingPdf,
                onClick   = { viewModel.downloadPdf(checkInId) }
            )
        }

        if (uiState.isLoading || uiState.isGenerating) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BpBlue)
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun BpHeader(title: String = "Flight LH007", onBack: () -> Unit) {
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
            text       = title,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = BpDark,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}

// ── Ticket card ───────────────────────────────────────────────────────────────

@Composable
private fun TicketCard(
    flightNumber: String  = "LH007",
    gate: String          = "A2",
    origin: String        = "LON",
    originCity: String    = "London",
    destination: String   = "RIO",
    destCity: String      = "Rio de Janeiro",
    passengerName: String = "—",
    seat: String          = "—",
    boardingTime: String  = "08:15 AM",
    departureTime: String = "08:45 AM"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        // ① Flight / Gate
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("GOL", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = BpOrange)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                BpLabelValue("Flight", flightNumber)
                BpLabelValue("Gate",   gate)
            }
        }

        Perforation()

        // ② Route + times
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(originCity, fontSize = 13.sp, color = BpLabel)
                    Text(origin, fontSize = 50.sp, fontWeight = FontWeight.ExtraBold, color = BpDark, lineHeight = 56.sp)
                }
                Box(Modifier.weight(1.2f).height(28.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxWidth().height(2.dp)) {
                        val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        drawLine(BpBlue, Offset(0f, 0f), Offset(size.width * 0.36f, 0f), 1.5.dp.toPx(), pathEffect = dash)
                        drawLine(BpBlue, Offset(size.width * 0.64f, 0f), Offset(size.width, 0f), 1.5.dp.toPx(), pathEffect = dash)
                    }
                    Text("✈", fontSize = 22.sp, color = BpBlue)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(destCity, fontSize = 13.sp, color = BpLabel, textAlign = TextAlign.End)
                    Text(destination, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = BpDark, textAlign = TextAlign.End, lineHeight = 56.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BpTimeColumn("Boarding Time", boardingTime)
                BpTimeColumn("Departs",       departureTime)
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
                Text(passengerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BpBlue)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Seat", fontSize = 12.sp, color = BpLabel)
                Text(seat, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BpBlue)
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
private fun QRCodeCard(qrBase64: String, fallbackCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (qrBase64.isNotEmpty()) {
            QRCodeImage(
                content  = decodeBase64(qrBase64),
                modifier = Modifier.size(180.dp)
            )
        } else {
            BarcodeCanvas(code = fallbackCode, modifier = Modifier.fillMaxWidth().height(80.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(text = fallbackCode, fontSize = 12.sp, letterSpacing = 2.sp, color = BpDark)
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
private fun BpDownloadButton(isLoading: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BpBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Button(
            onClick  = onClick,
            enabled  = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = BpBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Download PDF", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

// ── Helper functions ──────────────────────────────────────────────────────────

private fun formatDateTime(iso: String): String {
    if (iso.isEmpty()) return "—"
    return try {
        val dt = LocalDateTime.parse(iso)
        dt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm"))
    } catch (e: Exception) { iso }
}

private fun decodeBase64(base64: String): String {
    return try { String(Base64.decode(base64, Base64.DEFAULT)) } catch (e: Exception) { base64 }
}

@Composable
private fun QRCodeImage(content: String, modifier: Modifier = Modifier) {
    val bitmap = remember(content) {
        try {
            val size = 512
            val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
            val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) for (y in 0 until size) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
            bmp
        } catch (e: Exception) { null }
    }
    bitmap?.let {
        Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code", modifier = modifier)
    }
}

private fun savePdfToDownloads(context: android.content.Context, bytes: ByteArray, filename: String) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream -> stream.write(bytes) }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(it, values, null, null)
            }
        } else {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
            file.writeBytes(bytes)
        }
    } catch (e: Exception) { e.printStackTrace() }
}

@Preview(showBackground = true, heightDp = 820)
@Composable
fun BoardingPassScreenPreview() {
    BoardingPassScreen()
}
