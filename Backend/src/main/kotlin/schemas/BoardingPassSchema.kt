package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BoardingPasses : Table("boarding_passes") {
    val id = integer("id").autoIncrement()
    val checkInId = integer("check_in_id").references(CheckIns.id)
    val bookingReference = varchar("booking_reference", 20)
    val passengerName = varchar("passenger_name", 100)
    val flightNumber = varchar("flight_number", 20)
    val origin = varchar("origin", 100)
    val destination = varchar("destination", 100)
    val departureTime = varchar("departure_time", 50)
    val seatNumber = varchar("seat_number", 10)
    val seatClass = varchar("seat_class", 20)
    val gate = varchar("gate", 10)
    val qrCode = text("qr_code")
    val status = varchar("status", 20).default("VALID")

    override val primaryKey = PrimaryKey(id)
}

fun createBoardingPassTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(BoardingPasses)
    }
}