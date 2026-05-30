package com.example.airline.data.model

data class PassportModel(
    val passportNumber: String = "",
    val nationality: String = "",
    val issueDate: String = "",
    val expiryDate: String = "",
    val issuingAuthority: String = ""
)
