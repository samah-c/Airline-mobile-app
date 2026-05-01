package com.example.airline.utils

data class MrzData(
    val passportNumber: String,
    val surname: String,
    val givenNames: String,
    val nationality: String,
    val dateOfBirth: String,    // Format YYMMDD
    val sex: String,            // M/F/<
    val expiryDate: String      // Format YYMMDD
)

object MrzParser {
    fun extractFromRawText(rawText: String): MrzData? {
        // Nettoyage de base pour toutes les lignes
        val lines = rawText.uppercase()
            .replace("«", "<<")
            .split('\n', '\r')
            .map { line ->
                // Ne garder QUE les caractères autorisés
                line.replace(" ", "<").filter { c -> c in 'A'..'Z' || c in '0'..'9' || c == '<' }
            }
            .filter { it.length > 20 } // Ignorer les toutes petites lignes

        // Signature TD2/TD3 (Passeports) : Nationalité(3) + DOB(6) + Check(1) + Sex(1) + Expiry(6)
        val td2Td3Signature = Regex("([A-Z<]{3})([0-9<]{6})[0-9<]([MF<])([0-9<]{6})")
        
        // Signature TD1 (Cartes ID) : DOB(6) + Check(1) + Sex(1) + Expiry(6) + Check(1) + Nationalité(3)
        val td1Signature = Regex("([0-9<]{6})[0-9<]([MF<])([0-9<]{6})[0-9<]?([A-Z<]{3})")

        val td2Td3Index = lines.indexOfFirst { td2Td3Signature.containsMatchIn(it) }
        val td1Index = lines.indexOfFirst { td1Signature.containsMatchIn(it) }

        return try {
            if (td2Td3Index != -1) {
                // On a trouvé la ligne 2 d'un passeport ! La ligne 1 est forcément juste au-dessus.
                val line2 = lines[td2Td3Index]
                val line1 = if (td2Td3Index > 0) lines[td2Td3Index - 1] else return null
                parseTd2OrTd3(line1, line2)
            } else if (td1Index != -1) {
                // On a trouvé la ligne 2 d'une carte d'identité ! Ligne 1 au-dessus, Ligne 3 en-dessous.
                val line2 = lines[td1Index]
                val line1 = if (td1Index > 0) lines[td1Index - 1] else return null
                val line3 = if (td1Index < lines.size - 1) lines[td1Index + 1] else return null
                parseTd1(line1, line2, line3)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun String.safeSubstring(startIndex: Int, endIndex: Int): String {
        if (startIndex >= this.length) return ""
        val end = if (endIndex > this.length) this.length else endIndex
        return this.substring(startIndex, end)
    }

    private fun parseTd1(line1: String, line2: String, line3: String): MrzData {
        val l1 = line1.padEnd(30, '<')
        val l2 = line2.padEnd(30, '<')
        val l3 = line3.padEnd(30, '<')

        val passportNumber = l1.safeSubstring(5, 14).replace("<", "")
        
        val regex = Regex("([0-9<]{6})[0-9<]([MF<])([0-9<]{6})[0-9<]?([A-Z<]{3})")
        val match = regex.find(l2)
        
        var dob = ""
        var sex = ""
        var expiry = ""
        var nationality = ""

        if (match != null) {
            dob = match.groupValues[1].replace("<", "")
            sex = match.groupValues[2]
            expiry = match.groupValues[3].replace("<", "")
            nationality = match.groupValues[4].replace("<", "")
        } else {
            dob = l2.safeSubstring(0, 6).replace("<", "")
            sex = l2.safeSubstring(7, 8).replace("<", "")
            expiry = l2.safeSubstring(8, 14).replace("<", "")
            nationality = l2.safeSubstring(15, 18).replace("<", "")
        }

        val namesPart = l3.split("<<")
        val surname = namesPart[0].replace("<", " ").trim()
        val givenNames = if (namesPart.size > 1) namesPart[1].replace("<", " ").trim() else ""

        return MrzData(
            passportNumber = passportNumber,
            surname = surname,
            givenNames = givenNames,
            nationality = nationality,
            dateOfBirth = dob,
            sex = if (sex == "M") "Masculin" else if (sex == "F") "Féminin" else "Autre",
            expiryDate = expiry
        )
    }

    private fun parseTd2OrTd3(line1: String, line2: String): MrzData {
        // --- LIGNE 2 (Extraction intelligente anti-décalage) ---
        var passportNumber = ""
        var nationality = ""
        var dob = ""
        var sex = ""
        var expiry = ""

        val regex = Regex("([A-Z<]{3})([0-9<]{6})[0-9<]([MF<])([0-9<]{6})")
        val match = regex.find(line2)

        if (match != null) {
            nationality = match.groupValues[1].replace("<", "")
            dob = match.groupValues[2].replace("<", "")
            sex = match.groupValues[3]
            expiry = match.groupValues[4].replace("<", "")

            val beforeMatch = line2.substring(0, match.range.first)
            val passportStr = if (beforeMatch.isNotEmpty()) beforeMatch.substring(0, beforeMatch.length - 1) else ""
            passportNumber = passportStr.replace("<", "").take(9)
        } else {
            passportNumber = line2.safeSubstring(0, 9).replace("<", "")
            nationality = line2.safeSubstring(10, 13).replace("<", "")
            dob = line2.safeSubstring(13, 19).replace("<", "")
            sex = line2.safeSubstring(20, 21).replace("<", "")
            expiry = line2.safeSubstring(21, 27).replace("<", "")
        }

        // --- LIGNE 1 ---
        val namesPart = line1.split("<<")
        var surname = ""
        
        val firstPart = namesPart[0]
        val natIndex = if (nationality.length == 3) firstPart.indexOf(nationality) else -1
        
        if (natIndex != -1) {
            val surnameStart = natIndex + nationality.length
            if (surnameStart < firstPart.length) {
                surname = firstPart.substring(surnameStart).replace("<", " ").trim()
            }
        } else {
            surname = if (firstPart.length > 5) firstPart.substring(5).replace("<", " ").trim() else firstPart.replace("<", " ").trim()
        }

        val givenNames = if (namesPart.size > 1) namesPart[1].replace("<", " ").trim() else ""

        return MrzData(
            passportNumber = passportNumber,
            surname = surname,
            givenNames = givenNames,
            nationality = nationality,
            dateOfBirth = dob,
            sex = if (sex == "M") "Masculin" else if (sex == "F") "Féminin" else "Autre",
            expiryDate = expiry
        )
    }
}