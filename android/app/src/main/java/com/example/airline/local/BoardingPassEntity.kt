package com.example.airline.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boarding_passes")
data class BoardingPassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: String,
    val gate: String,
    val origin: String,
    val destination: String,
    val passengerName: String,
    val seat: String,
    val seatClass: String,
    val departureTime: String,
    val barcode: String,
    val qrCode: String,
    val savedAt: Long = System.currentTimeMillis()
)
