package com.example

import com.example.schemas.*
import com.example.services.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

lateinit var authService: AuthService
lateinit var flightService: FlightService
lateinit var checkInService: CheckInService

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/airline_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    createUsersTable(database)
    createFlightsTable(database)
    createBookingsTable(database)
    createSeatsTable(database)
    createCheckInsTable(database)

    authService = AuthService(database)
    flightService = FlightService(database)
    checkInService = CheckInService(database)
}