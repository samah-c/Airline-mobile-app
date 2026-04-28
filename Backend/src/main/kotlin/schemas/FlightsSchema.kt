package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Flights : Table("flights") {
    val id = integer("id").autoIncrement()
    val flightNumber = varchar("flight_number", 20)
    val origin = varchar("origin", 100)
    val destination = varchar("destination", 100)
    val departureTime = varchar("departure_time", 50)
    val arrivalTime = varchar("arrival_time", 50)
    val availableSeats = integer("available_seats")

    override val primaryKey = PrimaryKey(id)
}

fun createFlightsTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(Flights)
        // Ajouter des vols mock si la table est vide
        if (Flights.selectAll().count() == 0L) {
            Flights.insert {
                it[flightNumber] = "AF1234"
                it[origin] = "Alger"
                it[destination] = "Paris"
                it[departureTime] = "2026-05-10T10:00:00"
                it[arrivalTime] = "2026-05-10T13:00:00"
                it[availableSeats] = 150
            }
            Flights.insert {
                it[flightNumber] = "AF5678"
                it[origin] = "Alger"
                it[destination] = "Londres"
                it[departureTime] = "2026-05-11T08:00:00"
                it[arrivalTime] = "2026-05-11T11:30:00"
                it[availableSeats] = 120
            }
        }
    }
}