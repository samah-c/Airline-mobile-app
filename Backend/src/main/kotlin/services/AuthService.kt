package com.example.services

import com.example.models.RegisterRequest
import com.example.models.UserResponse
import com.example.schemas.Users
import com.example.schemas.dbQuery
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

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

    suspend fun googleSignIn(idToken: String): Pair<Int, Boolean> = dbQuery {
        // Vérifier le token Google
        val verifier = com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
            .Builder(
                com.google.api.client.http.javanet.NetHttpTransport(),
                com.google.api.client.json.gson.GsonFactory()
            )
            .setAudience(listOf("725216303962-vd329i9oeak458pjsam43f94t78rkhmo.apps.googleusercontent.com"))
            .build()

        val googleIdToken = verifier.verify(idToken)
            ?: error("Invalid Google token")

        val payload = googleIdToken.payload
        val email = payload.email
        val name = payload["name"] as? String ?: email

        // Vérifier si l'user existe déjà
        val existing = Users.selectAll()
            .where { Users.email eq email }
            .singleOrNull()

        if (existing != null) {
            // User existe → login
            Pair(existing[Users.id], false)
        } else {
            // User nouveau → register
            val userId = Users.insert {
                it[Users.name] = name
                it[Users.email] = email
                it[passwordHash] = BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt())
                it[phoneNumber] = ""
            }[Users.id]
            Pair(userId, true)
        }
    }
}