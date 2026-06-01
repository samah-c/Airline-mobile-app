package com.example

import com.example.schemas.*
import com.example.services.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

lateinit var authService: AuthService
lateinit var flightService: FlightService
lateinit var notificationService: NotificationService
lateinit var checkInService: CheckInService
lateinit var boardingPassService: BoardingPassService
lateinit var syncService: SyncService


fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/airline_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "password"
    )

    createUsersTable(database)
    createFlightsTable(database)
    createBookingsTable(database)
    createSeatsTable(database)
    createFcmTokensTable(database)
    createCheckInsTable(database)
    createBoardingPassTable(database)

    notificationService = NotificationService(database)
    authService = AuthService(database)
    flightService = FlightService(database)
    checkInService = CheckInService(database, notificationService)
    boardingPassService = BoardingPassService(database, notificationService)
    syncService = SyncService(database)
}