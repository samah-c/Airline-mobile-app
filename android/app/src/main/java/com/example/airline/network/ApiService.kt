package com.example.airline.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val token: String, val userId: Int)

interface ApiService {

    // ── Authentication ───────────────────────────────────────
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ── Check-in session ──────────────────────────────────────
    @POST("api/checkin/start")
    suspend fun startCheckIn(@Body request: StartCheckInRequest): Response<CheckInSession>

    @POST("api/checkin/passport")
    suspend fun verifyPassport(@Body request: VerifyPassportRequest): Response<CheckInSession>

    @POST("api/checkin/baggage")
    suspend fun declareBaggage(@Body request: BaggageRequest): Response<CheckInSession>

    @POST("api/checkin/special-requests")
    suspend fun submitSpecialRequests(@Body request: SpecialRequestsRequest): Response<CheckInSession>

    @POST("api/checkin/confirm")
    suspend fun confirmCheckIn(@Body request: ConfirmCheckInRequest): Response<CheckInSession>

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
