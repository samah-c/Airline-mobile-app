package com.example

import com.example.schemas.createUsersTable
import com.example.services.AuthService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

lateinit var authService: AuthService

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )
    createUsersTable(database)
    authService = AuthService(database)
}