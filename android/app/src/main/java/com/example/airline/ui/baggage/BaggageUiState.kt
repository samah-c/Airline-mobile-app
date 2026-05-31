package com.example.airline.ui.baggage

data class BaggageUiState(
    // Bagage Cabine
    val cabinBagCount: Int = 1,
    val cabinWeight: Int = 7,
    val isCabinExpanded: Boolean = true,

    // Bagage Soute
    val holdBagCount: Int = 0,
    val isHoldExpanded: Boolean = true,

    // Objets spéciaux
    val sportsCount: Int = 0,
    val petCount: Int = 0,
    val suitcaseCount: Int = 0,
    val medicalCount: Int = 0,

    // Prix total (calculé dynamiquement)
    val totalPrice: Int = 0
) {
    // Calcul simple : 15$ par bagage en soute (exemple)
    val displayPrice: String
        get() = "$${totalPrice} $"
}