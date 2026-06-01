package com.example.airline.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class SaveFcmTokenRequest(val userId: Int, val token: String)

interface NotificationApi {
    @POST("/api/notifications/token")
    suspend fun saveToken(@Body request: SaveFcmTokenRequest)
}