package com.example.services

import com.example.models.RegisterRequest
import com.example.models.UserResponse
import com.example.schemas.Users
import com.example.schemas.dbQuery
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

class AuthService(private val database: Database) {

    // Vérifie si email existe déjà
    suspend fun emailExists(email: String): Boolean = dbQuery {
        Users.selectAll().where { Users.email eq email }.count() > 0
    }

    // Crée un nouvel utilisateur, retourne son ID
    suspend fun register(request: RegisterRequest): Int = dbQuery {
        val hash = BCrypt.hashpw(request.password, BCrypt.gensalt())
        Users.insert {
            it[name] = request.name
            it[email] = request.email
            it[passwordHash] = hash
            it[phoneNumber] = request.phoneNumber
        }[Users.id]
    }

    // Vérifie email + password, retourne l'user ou null
    suspend fun login(email: String, password: String): UserResponse? = dbQuery {
        val row = Users.selectAll()
            .where { Users.email eq email }
            .singleOrNull() ?: return@dbQuery null

        val hash = row[Users.passwordHash]
        if (!BCrypt.checkpw(password, hash)) return@dbQuery null

        UserResponse(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            phoneNumber = row[Users.phoneNumber]
        )
    }
}