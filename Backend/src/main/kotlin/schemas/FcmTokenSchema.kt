package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object FcmTokens : Table("fcm_tokens") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val token = text("token")
    val updatedAt = varchar("updated_at", 50)

    override val primaryKey = PrimaryKey(id)
}

fun createFcmTokensTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(FcmTokens)
    }
}