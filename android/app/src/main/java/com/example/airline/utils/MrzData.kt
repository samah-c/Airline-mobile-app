package com.example.airline.utils

data class MrzData(
    val documentType: String,
    val issuingCountry: String,
    val surname: String,
    val givenNames: String,
    val passportNumber: String,
    val nationality: String,
    val birthDate: String,
    val sex: String,
    val expiryDate: String,
    val personalNumber: String?
)
