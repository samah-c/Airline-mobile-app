package com.example.airline.data.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FlightApiService {

    @GET("api/flights/history/{userId}")
    suspend fun getFlightHistory(
        @Path("userId") userId: Int
    ): Response<List<FlightLookupResponse>>

    @POST("api/flights/lookup")
    suspend fun lookupFlight(
        @Body request: FlightLookupRequest
    ): Response<FlightLookupResponse>


}