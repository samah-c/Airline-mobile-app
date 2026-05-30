package com.example.airline.data.model

data class SeatModel(
    val id: String,
    val seatClass: SeatClassType,
    val state: SeatStateType
)

enum class SeatClassType { FIRST, ECONOMY }
enum class SeatStateType { AVAILABLE, OCCUPIED }
