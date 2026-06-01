package com.example.utils

import java.security.MessageDigest

/**
 * Security utilities for sensitive data protection
 */
object SecurityUtils {
    
    /**
     * Hash passport number using SHA-256
     * Used for storage and uniqueness verification without exposing raw data
     */
    fun hashPassportNumber(passportNumber: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(passportNumber.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Check if a date is expired (comparing to today)
     * @param expirationDate Format: YYYY-MM-DD
     * @return true if expired, false if still valid
     */
    fun isDateExpired(expirationDate: String): Boolean {
        return try {
            val expDate = java.time.LocalDate.parse(expirationDate)
            val today = java.time.LocalDate.now()
            expDate.isBefore(today)
        } catch (e: Exception) {
            true  // If parsing fails, consider it expired for safety
        }
    }
    
    /**
     * Get current Unix timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
}
