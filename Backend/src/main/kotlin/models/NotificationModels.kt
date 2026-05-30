package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class SaveFcmTokenRequest(
    val userId: Int,
    val token: String
)

@Serializable
data class NotificationResult(
    val success: Boolean,
    val message: String
)