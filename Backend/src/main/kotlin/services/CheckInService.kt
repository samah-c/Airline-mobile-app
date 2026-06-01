package com.example.services

import com.example.models.*
import com.example.schemas.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Database

class CheckInService(
    private val database: Database,
    private val notificationService: NotificationService
) {

    // Étape 1 — Démarrer le check-in
    suspend fun startCheckIn(bookingId: Int): CheckInSession = dbQuery {
        val id = CheckIns.insert {
            it[CheckIns.bookingId] = bookingId
            it[status] = "STARTED"
        }[CheckIns.id]

        CheckInSession(id, bookingId, "STARTED", false, null, 0, 0)
    }

    // Étape 2 — Vérifier le passeport
    suspend fun verifyPassport(request: VerifyPassportRequest): CheckInSession = dbQuery {
        val checkInId = request.checkInId
        val passportData = request.passportData

        val checkIn = CheckIns.selectAll()
            .where { CheckIns.id eq checkInId }
            .firstOrNull()
            ?: throw IllegalArgumentException("Check-in session not found")

        val booking = Bookings.selectAll()
            .where { Bookings.id eq checkIn[CheckIns.bookingId] }
            .firstOrNull()
            ?: throw IllegalArgumentException("Booking not found")

        val userId = booking[Bookings.passengerId]
        val user = Users.selectAll()
            .where { Users.id eq userId }
            .firstOrNull()
            ?: throw IllegalArgumentException("User not found")

        val expirationDate = passportData.expirationDate.trim()
        val expiry = try {
            parsePassportDate(expirationDate)
        } catch (e: Exception) {
            throw IllegalStateException("Invalid passport expiration date format: $expirationDate")
        }

        if (!expiry.isAfter(java.time.LocalDate.now())) {
            throw IllegalStateException("Passport is expired on $expirationDate")
        }

        val bookingLastName = booking[Bookings.passengerLastName].trim().lowercase()
        val passportLastName = passportData.lastName.trim().lowercase()
        if (passportLastName != bookingLastName) {
            throw IllegalStateException(
                "Passport last name does not match booking record. Expected: '$bookingLastName', Got: '$passportLastName'"
            )
        }

        CheckIns.update({ CheckIns.id eq checkInId }) {
            it[passportVerified] = true
            it[status] = "PASSPORT_VERIFIED"
        }

        getCheckIn(checkInId)!!
    }


    // Récupérer les sièges disponibles
    private fun parsePassportDate(value: String): java.time.LocalDate {
        return try {
            java.time.LocalDate.parse(value)
        } catch (e: java.time.format.DateTimeParseException) {
            try {
                java.time.LocalDate.parse(value, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (ex: java.time.format.DateTimeParseException) {
                parseMrzShortDate(value)
            }
        }
    }

    private fun parseMrzShortDate(value: String): java.time.LocalDate {
        if (value.length != 6) throw IllegalArgumentException("Unsupported date format: $value")

        val year = value.substring(0, 2).toIntOrNull() ?: throw IllegalArgumentException("Invalid MRZ date: $value")
        val month = value.substring(2, 4).toIntOrNull() ?: throw IllegalArgumentException("Invalid MRZ date: $value")
        val day = value.substring(4, 6).toIntOrNull() ?: throw IllegalArgumentException("Invalid MRZ date: $value")

        val currentYear = java.time.LocalDate.now().year
        val century = currentYear / 100 * 100
        var fullYear = century + year
        if (fullYear > currentYear + 20) fullYear -= 100
        if (fullYear < currentYear - 120) fullYear += 100

        return java.time.LocalDate.of(fullYear, month, day)
    }

    suspend fun getAvailableSeats(flightId: Int): List<Seat> = dbQuery {
        Seats.selectAll()
            .where { Seats.flightId eq flightId }
            .map {
                Seat(
                    id = it[Seats.id],
                    flightId = it[Seats.flightId],
                    seatNumber = it[Seats.seatNumber],
                    seatClass = it[Seats.seatClass],
                    isOccupied = it[Seats.isOccupied]
                )
            }
    }

    // Étape 3 — Choisir le siège
    suspend fun selectSeat(request: SeatSelectionRequest): CheckInSession = dbQuery {
        // Marquer le siège comme occupé
        Seats.update({ Seats.seatNumber eq request.seatNumber }) {
            it[isOccupied] = true
        }
        // Sauvegarder dans le check-in
        CheckIns.update({ CheckIns.id eq request.checkInId }) {
            it[seatNumber] = request.seatNumber
            it[status] = "SEAT_SELECTED"
        }
        getCheckIn(request.checkInId)!!
    }

    // Étape 4 — Déclarer les bagages
    suspend fun declareBaggage(request: BaggageRequest): CheckInSession = dbQuery {
        CheckIns.update({ CheckIns.id eq request.checkInId }) {
            it[cabinBags] = request.cabinBags
            it[checkedBags] = request.checkedBags
            it[estimatedWeight] = request.estimatedWeight
            it[status] = "BAGGAGE_DECLARED"
        }
        getCheckIn(request.checkInId)!!
    }

    // Étape 5 — Préférences spéciales
    suspend fun saveSpecialRequests(request: SpecialRequestsRequest): CheckInSession {

        // 1. Sauvegarder en DB (dans dbQuery)
        val checkIn = dbQuery {
            CheckIns.update({ CheckIns.id eq request.checkInId }) {
                it[dietaryPreference] = request.dietaryPreference
                it[needsWheelchair] = request.needsWheelchair
                it[needsVisualAssistance] = request.needsVisualAssistance
                it[needsHearingAssistance] = request.needsHearingAssistance
                it[travellingWithInfant] = request.travellingWithInfant
                it[travellingWithPet] = request.travellingWithPet
                it[status] = "COMPLETED"
            }

            val checkInRow = CheckIns.selectAll()
                .where { CheckIns.id eq request.checkInId }
                .single()
            val session = CheckInSession(
                id = checkInRow[CheckIns.id],
                bookingId = checkInRow[CheckIns.bookingId],
                status = checkInRow[CheckIns.status],
                passportVerified = checkInRow[CheckIns.passportVerified],
                seatNumber = checkInRow[CheckIns.seatNumber],
                cabinBags = checkInRow[CheckIns.cabinBags],
                checkedBags = checkInRow[CheckIns.checkedBags]
            )

            // Marquer le booking comme completed
            Bookings.update({ Bookings.id eq session.bookingId }) {
                it[checkInStatus] = "COMPLETED"
            }

            // Récupérer infos pour la notification
            val booking = Bookings.selectAll()
                .where { Bookings.id eq session.bookingId }
                .single()

            val flight = Flights.selectAll()
                .where { Flights.id eq booking[Bookings.flightId] }
                .single()

            Triple(
                session,
                booking[Bookings.passengerId],
                flight[Flights.flightNumber]
            )
        }

        val (session, _, _) = checkIn
        return session
    }

    suspend fun confirmCheckIn(checkInId: Int): CheckInSession {
        val (session, userId, flightNumber) = dbQuery {
            val checkInRow = CheckIns.selectAll()
                .where { CheckIns.id eq checkInId }
                .singleOrNull()
                ?: throw IllegalArgumentException("Check-in session not found")

            val session = CheckInSession(
                id = checkInRow[CheckIns.id],
                bookingId = checkInRow[CheckIns.bookingId],
                status = checkInRow[CheckIns.status],
                passportVerified = checkInRow[CheckIns.passportVerified],
                seatNumber = checkInRow[CheckIns.seatNumber],
                cabinBags = checkInRow[CheckIns.cabinBags],
                checkedBags = checkInRow[CheckIns.checkedBags]
            )

            val booking = Bookings.selectAll()
                .where { Bookings.id eq session.bookingId }
                .single()
            val flight = Flights.selectAll()
                .where { Flights.id eq booking[Bookings.flightId] }
                .single()

            Triple(
                session,
                booking[Bookings.passengerId],
                flight[Flights.flightNumber]
            )
        }

        try {
            notificationService.sendCheckInConfirmation(
                userId = userId,
                flightNumber = flightNumber,
                seat = session.seatNumber ?: "N/A"
            )
        } catch (e: Exception) {
            println("Notification failed: ${e.message}")
        }

        return session
    }

    suspend fun bookingBelongsToUser(bookingId: Int, userId: Int): Boolean = dbQuery {
        Bookings.selectAll()
            .where { (Bookings.id eq bookingId) and (Bookings.passengerId eq userId) }
            .count() > 0
    }

    suspend fun checkInBelongsToUser(checkInId: Int, userId: Int): Boolean = dbQuery {
        (CheckIns innerJoin Bookings)
            .selectAll()
            .where { (CheckIns.id eq checkInId) and (Bookings.passengerId eq userId) }
            .count() > 0
    }

    private suspend fun getCheckIn(id: Int): CheckInSession? = dbQuery {
        CheckIns.selectAll()
            .where { CheckIns.id eq id }
            .map {
                CheckInSession(
                    id = it[CheckIns.id],
                    bookingId = it[CheckIns.bookingId],
                    status = it[CheckIns.status],
                    passportVerified = it[CheckIns.passportVerified],
                    seatNumber = it[CheckIns.seatNumber],
                    cabinBags = it[CheckIns.cabinBags],
                    checkedBags = it[CheckIns.checkedBags]
                )
            }.singleOrNull()
    }
}
