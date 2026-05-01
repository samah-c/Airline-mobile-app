package com.example.schemas

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object CheckIns : Table("check_ins") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(Bookings.id)
    val status = varchar("status", 30).default("STARTED")
    val passportVerified = bool("passport_verified").default(false)
    val seatNumber = varchar("seat_number", 10).nullable()
    val cabinBags = integer("cabin_bags").default(0)
    val checkedBags = integer("checked_bags").default(0)
    val estimatedWeight = double("estimated_weight").default(0.0)
    val dietaryPreference = varchar("dietary_preference", 30).default("STANDARD")
    val needsWheelchair = bool("needs_wheelchair").default(false)
    val needsVisualAssistance = bool("needs_visual_assistance").default(false)
    val needsHearingAssistance = bool("needs_hearing_assistance").default(false)
    val travellingWithInfant = bool("travelling_with_infant").default(false)
    val travellingWithPet = bool("travelling_with_pet").default(false)

    override val primaryKey = PrimaryKey(id)
}

fun createCheckInsTable(database: Database) {
    transaction(database) {
        SchemaUtils.create(CheckIns)
    }
}