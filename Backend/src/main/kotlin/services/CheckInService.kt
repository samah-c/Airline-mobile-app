package com.example.services

import com.example.models.*
import com.example.schemas.*
import org.jetbrains.exposed.sql.*
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
        CheckIns.update({ CheckIns.id eq request.checkInId }) {
            it[passportVerified] = true
            it[status] = "PASSPORT_VERIFIED"
        }
        getCheckIn(request.checkInId)!!
    }

    // Récupérer les sièges disponibles
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

            val session = getCheckIn(request.checkInId)!!

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

        val (session, userId, flightNumber) = checkIn

        // 2. Envoyer notification (en dehors de dbQuery)
        try {
            notificationService.sendCheckInConfirmation(
                userId = userId,
                flightNumber = flightNumber,
                seat = session.seatNumber ?: "N/A"
            )
        } catch (e: Exception) {
            // Ne pas bloquer si la notification échoue
            println("Notification failed: ${e.message}")
        }

        return session
    }
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
