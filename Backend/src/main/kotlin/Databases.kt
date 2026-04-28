package com.example

import com.example.schemas.createBookingsTable
import com.example.schemas.createFlightsTable
import com.example.schemas.createUsersTable
import com.example.services.AuthService
import com.example.services.FlightService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

lateinit var authService: AuthService
lateinit var flightService: FlightService

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

    authService = AuthService(database)
    flightService = FlightService(database)
}