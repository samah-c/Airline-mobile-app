package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Seats : Table("seats") {
    val id = integer("id").autoIncrement()
    val flightId = integer("flight_id").references(Flights.id)
    val seatNumber = varchar("seat_number", 10)
    val seatClass = varchar("seat_class", 20) // FIRST, ECONOMY
    val isOccupied = bool("is_occupied").default(false)

    override val primaryKey = PrimaryKey(id)
}

fun createSeatsTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(Seats)
        if (Seats.selectAll().count() == 0L) {
            // Générer les sièges pour le vol 1
            val rows = listOf("A", "B", "C", "D")
            for (rowNum in 1..5) {
                for (col in rows) {
                    Seats.insert {
                        it[flightId] = 1
                        it[seatNumber] = "$rowNum$col"
                        it[seatClass] = if (rowNum <= 2) "FIRST" else "ECONOMY"
                        it[isOccupied] = false
                    }
                }
            }
        }
    }
}