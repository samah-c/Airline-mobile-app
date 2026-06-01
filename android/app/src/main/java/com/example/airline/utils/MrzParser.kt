package com.example.airline.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MrzParser {
    private val mrzLineRegex = Regex("[A-Z0-9<]{30,44}")
    private val displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun extractFromRawText(rawText: String): MrzData? {
        val normalizedText = rawText
            .uppercase()
            .replace("\r", "")
            .replace(Regex("[^\nA-Z0-9<]"), "")

        val lines = normalizedText
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val mrzLines = findMrzLines(lines)
        if (mrzLines.size < 2) return null

        val line1 = mrzLines[0].padEnd(44, '<')
        val line2 = mrzLines[1].padEnd(44, '<')

        return parsePassportMrz(line1, line2)
    }

    fun extractFromRawTextWithValidation(rawText: String): Pair<MrzData?, Boolean> {
        val normalizedText = rawText
            .uppercase()
            .replace("\r", "")
            .replace(Regex("[^\nA-Z0-9<]"), "")

        val lines = normalizedText
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val mrzLines = findMrzLines(lines)
        if (mrzLines.size < 2) return Pair(null, false)

        val line1 = mrzLines[0].padEnd(44, '<')
        val line2 = mrzLines[1].padEnd(44, '<')

        val parsed = parsePassportMrz(line1, line2)
        val valid = if (parsed != null) validateMrzChecks(line1, line2) else false
        return Pair(parsed, valid)
    }

    private fun findMrzLines(lines: List<String>): List<String> {
        val candidates = lines.map { it.replace(" ", "") }
            .filter { it.length >= 30 }
            .mapNotNull { line -> mrzLineRegex.find(line)?.value }

        if (candidates.size >= 2) return candidates.take(2)

        val joined = lines.joinToString(separator = "")
        val allMatches = mrzLineRegex.findAll(joined).map { it.value }.toList()
        return if (allMatches.size >= 2) allMatches.take(2) else emptyList()
    }

    private fun parsePassportMrz(line1: String, line2: String): MrzData? {
        return try {
            val documentType = line1.substring(0, 1).replace("<", " ").trim()
            val issuingCountry = line1.substring(2, 5).replace("<", " ").trim()

            val namesSection = line1.substring(5, 44)
            val nameParts = namesSection.split("<<")
            val surname = nameParts.getOrNull(0)?.replace("<", " ")?.trim() ?: ""
            val givenNames = nameParts.getOrNull(1)?.replace("<", " ")?.trim() ?: ""

            val passportNumber = line2.substring(0, 9).replace("<", " ").trim()
            val nationality = line2.substring(10, 13).replace("<", " ").trim()
            val birthDate = line2.substring(13, 19).trim()
            val sex = line2.substring(20, 21).trim()
            val expiryDate = line2.substring(21, 27).trim()
            val personalNumberRaw = line2.substring(28, 42).replace("<", " ").trim()
            val personalNumber = if (personalNumberRaw.isBlank()) null else personalNumberRaw

            MrzData(
                documentType = documentType,
                issuingCountry = issuingCountry,
                surname = surname,
                givenNames = givenNames,
                passportNumber = passportNumber,
                nationality = nationality,
                birthDate = formatMrzDate(birthDate),
                sex = sex,
                expiryDate = formatMrzDate(expiryDate),
                personalNumber = personalNumber
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun charValue(c: Char): Int {
        return when (c) {
            in '0'..'9' -> c - '0'
            in 'A'..'Z' -> c - 'A' + 10
            '<' -> 0
            else -> 0
        }
    }

    private fun computeCheckDigit(field: String): Int {
        val weights = intArrayOf(7, 3, 1)
        var sum = 0
        for (i in field.indices) {
            val v = charValue(field[i])
            sum += v * weights[i % 3]
        }
        return sum % 10
    }

    private fun formatMrzDate(value: String): String {
        if (value.length != 6) return value

        val year = value.substring(0, 2).toIntOrNull() ?: return value
        val month = value.substring(2, 4).toIntOrNull() ?: return value
        val day = value.substring(4, 6).toIntOrNull() ?: return value

        val currentYear = LocalDate.now().year
        val century = currentYear / 100 * 100
        var fullYear = century + year

        if (fullYear > currentYear + 20) fullYear -= 100
        if (fullYear < currentYear - 120) fullYear += 100

        return try {
            LocalDate.of(fullYear, month, day).format(displayDateFormatter)
        } catch (e: Exception) {
            value
        }
    }

    private fun validateMrzChecks(line1: String, line2: String): Boolean {
        try {
            val passportField = line2.substring(0, 9)
            val passportCheck = line2.substring(9, 10)[0]
            val birthField = line2.substring(13, 19)
            val birthCheck = line2.substring(19, 20)[0]
            val expiryField = line2.substring(21, 27)
            val expiryCheck = line2.substring(27, 28)[0]

            val passDigit = computeCheckDigit(passportField)
            val birthDigit = computeCheckDigit(birthField)
            val expDigit = computeCheckDigit(expiryField)

            return (passDigit.toString()[0] == passportCheck)
                    || (birthDigit.toString()[0] == birthCheck)
                    || (expDigit.toString()[0] == expiryCheck)
        } catch (e: Exception) {
            return false
        }
    }
}
