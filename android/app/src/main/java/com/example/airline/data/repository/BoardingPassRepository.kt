package com.example.airline.data.repository

import com.example.airline.data.model.BoardingPassModel
import com.example.airline.network.GenerateBoardingPassRequest
import com.example.airline.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response

data class PdfDownloadResult(
    val bytes: ByteArray,
    val filename: String
)

class BoardingPassRepository {

    private fun mapResponse(bp: com.example.airline.network.BoardingPassResponse) = BoardingPassModel(
        flightNumber    = bp.flightNumber,
        gate            = bp.gate,
        origin          = bp.origin,
        originCity      = bp.origin,
        destination     = bp.destination,
        destinationCity = bp.destination,
        passengerName   = bp.passengerName,
        seat            = bp.seatNumber,
        seatClass       = bp.seatClass,
        boardingTime    = bp.departureTime,
        departureTime   = bp.departureTime,
        arrivalTime     = "",
        barcode         = bp.bookingReference,
        qrCode          = bp.qrCode
    )

    suspend fun getBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.getBoardingPass(checkInId)
        return if (response.isSuccessful) response.body()?.let { mapResponse(it) } else null
    }

    suspend fun generateBoardingPass(checkInId: Int): BoardingPassModel? {
        val response = RetrofitClient.api.generateBoardingPass(GenerateBoardingPassRequest(checkInId))
        return if (response.isSuccessful) response.body()?.let { mapResponse(it) } else null
    }

    suspend fun downloadPdf(checkInId: Int): PdfDownloadResult? {
        val response: Response<ResponseBody> = RetrofitClient.api.downloadBoardingPassPdf(checkInId)

        if (response.isSuccessful && response.body() != null) {
            val bytes = response.body()!!.bytes()

            // Extraction du nom depuis Content-Disposition: attachment; filename="boarding-pass-XYZ.pdf"
            val contentDisposition = response.headers()["Content-Disposition"]
            val filename = extractFileName(contentDisposition) ?: "boarding-pass-$checkInId.pdf"

            return PdfDownloadResult(bytes, filename)
        }
        return null
    }

    // Helper pour parser le header Content-Disposition
    private fun extractFileName(contentDisposition: String?): String? {
        if (contentDisposition == null) return null
        // Regex pour capturer le nom entre guillemets ou après filename=
        val regex = """filename\*?=['"]?(?:UTF-8''|[^'"]*\*)?([^'";\n]+)["']?""".toRegex()
        return regex.find(contentDisposition)?.groupValues?.get(1)
    }
}