package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Bookings : Table("bookings") {
    val id = integer("id").autoIncrement()
    val bookingReference = varchar("booking_reference", 20).uniqueIndex()
    val passengerId = integer("passenger_id").references(Users.id)
    val flightId = integer("flight_id").references(Flights.id)
    val passengerLastName = varchar("passenger_last_name", 100)
    val checkInStatus = varchar("check_in_status", 20).default("NOT_STARTED")

    override val primaryKey = PrimaryKey(id)
}

fun createBookingsTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(Bookings)

    }
}