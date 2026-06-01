package com.example

import com.example.schemas.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

private const val seedUserEmail = "test.user@example.com"
private const val seedUserPassword = "Password123!"
private const val seedBookingReference = "TESTBOOK1"

fun Application.configureSeed() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/airline_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    transaction(database) {
        val userId = Users.selectAll()
            .where { Users.email eq seedUserEmail }
            .singleOrNull()
            .let { existingUser ->
                if (existingUser != null) {
                    val uid = existingUser[Users.id]
                    Users.update({ Users.id eq uid }) {
                        it[name] = "Maria Olivia Virtanen"
                        it[phoneNumber] = "+33612345678"
                    }
                    uid
                } else {
                    Users.insert {
                        it[name] = "Maria Olivia Virtanen"
                        it[email] = seedUserEmail
                        it[passwordHash] = BCrypt.hashpw(seedUserPassword, BCrypt.gensalt())
                        it[phoneNumber] = "+33612345678"
                    }[Users.id]
                }
            }

        val flightId = Flights.selectAll().limit(1).single()[Flights.id]

        if (Bookings.selectAll().where { Bookings.bookingReference eq seedBookingReference }.empty()) {
            Bookings.insert {
                it[Bookings.bookingReference] = seedBookingReference
                it[Bookings.passengerId] = userId
                it[Bookings.flightId] = flightId
                it[Bookings.passengerLastName] = "Virtanen"
                it[Bookings.checkInStatus] = "NOT_STARTED"
            }
        }
    }


}
