package com.example.airline.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    // ── Seat selection ────────────────────────────────────────
    @GET("api/checkin/seats/{flightId}")
    suspend fun getAvailableSeats(@Path("flightId") flightId: Int): Response<List<SeatResponse>>

    @POST("api/checkin/seat")
    suspend fun selectSeat(@Body request: SeatSelectionRequest): Response<CheckInSession>

    // ── Boarding pass ─────────────────────────────────────────
    @POST("api/boarding-pass/generate")
    suspend fun generateBoardingPass(@Body request: GenerateBoardingPassRequest): Response<BoardingPassResponse>

    @GET("api/boarding-pass/{checkInId}")
    suspend fun getBoardingPass(@Path("checkInId") checkInId: Int): Response<BoardingPassResponse>

    @Streaming
    @GET("api/boarding-pass/{checkInId}/pdf")
    suspend fun downloadBoardingPassPdf(@Path("checkInId") checkInId: Int): Response<ResponseBody>
}
