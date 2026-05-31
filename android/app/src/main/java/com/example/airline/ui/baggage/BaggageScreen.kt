package com.example.airline.ui.baggage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.common.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.R

// ── Colors ─────────────────────────────
private val PageBg              = Color.White    // Fond principal
private val CardBg              = Color.White            // Cartes blanches
private val TextPrimary         = Color(0xFF1A1A2E)      // Texte principal noir
private val TextSecondary       = Color(0xFF6B7280)      // Texte secondaire gris

private val BorderBlue          = Color(0xFF1849D6)

private val TextOnBlue          = Color.White.copy(alpha = 0.92f)
private val BadgeIncluded       = Color(0xFF4CD964)      // Vert "Inclus"
private val BadgePaid           = Color(0xFFFF9800)      // Orange "Payant"
private val BorderLight         = Color(0xFFE0E0E0)      // Bordures grises
private val InfoBoxBlue         = Color(0xFFE3F2FD)      // Fond info bleu clair
private val InfoBoxOrange       = Color(0xFFFFF8E1)      // Fond info orange
private val SummaryGreen        = Color(0xFFE8F5E9)      // Fond récap vert
private val PriceGreen          = Color(0xFF4CAF50)      // Prix vert
private val CounterBtnDisabled  = Color(0xFF9CA3AF)      // Bouton - grisé
private val CounterBtnActive    = Color(0xFF1849D6)      // Bouton + bleu

// ── Constants pour layout précis ──────────────────────────────────────────────
private const val CARD_CORNER   = 12f   // Coins arrondis des cartes
private const val BADGE_CORNER  = 4f    // Coins des badges
private const val COUNTER_SIZE  = 32f   // Taille boutons +/-
private const val SECTION_PAD_V = 16f   // Padding vertical sections

// ── Models ────────────────────────────────────────────────────────────────────
private enum class BagType { CABIN, HOLD, SPECIAL }
private enum class BagState { INCLUDED, PAID, NONE }

private data class BaggageItem(
    val id: String,
    val type: BagType,
    val state: BagState,
    val count: Int = 0,
    val maxWeight: Int = 10,
    val currentWeight: Int = 7,
    val dimensions: String = "55 x 35 x 25 cm",
    val price: Int = 0
)

private sealed class BaggageSection {
    data class CounterSection(val item: BaggageItem) : BaggageSection()
    data class InfoBox(val text: String, val bgColor: Color, val textColor: Color) : BaggageSection()
    object SpecialItems : BaggageSection()
    object Summary : BaggageSection()
}

// ── Données initiales ─────────────────────────────────────────────────────────
private fun initialBaggageState() = listOf(
    BaggageItem("cabin", BagType.CABIN, BagState.INCLUDED, count = 1, maxWeight = 10, currentWeight = 7, dimensions = "55 x 35 x 25 cm" ),
    BaggageItem("hold", BagType.HOLD, BagState.PAID, count = 0,dimensions = "158 cm linéaires (L+H+P)" , price = 65)
)

// ── Screen Principal ──────────────────────────────────────────────────────────

@Composable
fun BaggageScreen(
    onNavigateBack: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    var baggageItems by remember { mutableStateOf(initialBaggageState()) }
    val scrollState = rememberScrollState()
    var selectedSpecialItems by remember { mutableStateOf(emptySet<String>()) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header avec stepper
        BaggageHeader(onNavigateBack)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(PageBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 88.dp) // Espace pour le bouton fixe
            ) {
                // Flight info header
                FlightInfoHeader()

                // Sections scrollables
                baggageItems.forEach { item ->
                    CounterBaggageSection(item) { updated ->
                        baggageItems = baggageItems.map { if (it.id == updated.id) updated else it }
                    }
                }

                SpecialItemsSection(
                    selectedItems = selectedSpecialItems,
                    onToggle = { item ->
                        selectedSpecialItems = if (item in selectedSpecialItems) {
                            selectedSpecialItems - item
                        } else {
                            selectedSpecialItems + item
                        }
                    }
                )
                SummarySection(baggageItems)

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bouton Confirmer fixe en bas à droite (comme NextButton)
            ConfirmButton(
                onClick = onConfirm,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

// ── Header avec Stepper (identique à SeatSelectionScreen) ─────────────────────

@Composable
private fun BaggageHeader(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Ligne 1: Retour + Titre
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "‹",
                fontSize = 30.sp,
                color = Color(0xFF2541EE),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )
            Text(
                text = "Declare Your Luggage",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ligne 2: STEP X OF Y
        Text(
            text = "STEP 4 OF 5",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Stepper visuel (5 étapes, 4 complétées)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i < 4) Color(0xFF1849D6) else Color(0xFFE5E7EB))
                )
            }
        }
    }
}

// ── Flight Info Header (comme dans SeatSelectionScreen) ───────────────────────


@Composable
private fun FlightInfoHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône avion bleue
            Icon(
                painter = painterResource(id = R.drawable.ic_airplane),
                contentDescription = null,
                tint = Color(0xFF1849D6),
                modifier = Modifier.size(16.dp)
            )

            // Aéroport départ
            Text(
                text = "ALG",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Flèche
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )

            // Aéroport arrivée
            Text(
                text = "CDG",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Séparateur vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // Code vol
            Text(
                text = "AF 1024",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            // Type (Eco) en bleu
            Text(
                text = "Eco",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1849D6)
            )

            // Séparateur vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // Siège
            Text(
                text = "Siège 2B",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }
    }
}

// ── Wrapper pour les sections (comme PlaneBodyRow) ────────────────────────────

@Composable
private fun BaggageCard(
    hasBlueBorder: Boolean = false,
    content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PageBg)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CARD_CORNER.dp))
                .background(CardBg)
                .border(1.dp, color = if (hasBlueBorder) BorderBlue else BorderLight, RoundedCornerShape(CARD_CORNER.dp))
        ) {
            content()
        }
    }
}

// ── Section avec compteur (+/-) ───────────────────────────────────────────────

@Composable
private fun CounterBaggageSection(
    item: BaggageItem,
    onUpdate: (BaggageItem) -> Unit
) {
    BaggageCard (hasBlueBorder = item.type == BagType.CABIN ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Titre + Badge + Compteur
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (item.type) {
                                BagType.CABIN -> "Bagage cabine"
                                BagType.HOLD -> "Bagage en soute"
                                else -> ""
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Badge Inclus/Payant
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (item.state == BagState.INCLUDED) BadgeIncluded.copy(alpha = 0.2f) else BadgePaid.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(BADGE_CORNER.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (item.state == BagState.INCLUDED) "Inclus" else "Payant",
                                color = if (item.state == BagState.INCLUDED) BadgeIncluded else BadgePaid,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Text(
                        text = "Max. ${item.maxWeight}kg par passager",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Taille max: ${item.dimensions}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Compteur +/-
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (item.count > 0) {
                                onUpdate(item.copy(count = item.count - 1))
                            }
                        },
                        modifier = Modifier.size(COUNTER_SIZE.dp),
                        enabled = item.count > 0
                    ) {
                        Text(
                            text = "−",
                            color = if (item.count > 0) TextPrimary else CounterBtnDisabled,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${item.count}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = { onUpdate(item.copy(count = item.count + 1)) },
                        modifier = Modifier.size(COUNTER_SIZE.dp)
                    ) {
                        Text(
                            text = "+",
                            color = CounterBtnActive,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Slider de poids (uniquement pour cabine)
            if (item.type == BagType.CABIN) {
                Spacer(modifier = Modifier.height(12.dp))
                WeightSlider(
                    currentWeight = item.currentWeight,
                    maxWeight = item.maxWeight,
                    onWeightChange = { newWeight ->
                        onUpdate(item.copy(currentWeight = newWeight))
                    }
                )
            }

            // Info box conditionnelle
            if (item.type == BagType.CABIN) {
                InfoBox(
                    text = "Un bagage cabine et un article personnel inclus dans votre tarif",
                    bgColor = InfoBoxBlue,
                    textColor = Color(0xFF1849D6)
                )
            } else if (item.type == BagType.HOLD && item.count > 0) {
                InfoBox(
                    text = "Ce frais supplémentaire : 1 bagage • 23kg\n+ bagages ${item.price} $ Payable à l'étape suivante",
                    bgColor = InfoBoxOrange,
                    textColor = BadgePaid
                )
            }
        }
    }
}

// ── Slider de poids avec barre de progression ─────────────────────────────────

@Composable
private fun WeightSlider(
    currentWeight: Int,
    maxWeight: Int,
    onWeightChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Poids estimé", fontSize = 14.sp, color = TextSecondary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (currentWeight > 0) onWeightChange(currentWeight - 1) }) {
                    Text("−", color = CounterBtnDisabled, fontSize = 18.sp)
                }
                Text(
                    "$currentWeight kg",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = TextPrimary
                )
                IconButton(onClick = { if (currentWeight < 8) onWeightChange(currentWeight + 1) }) {
                    Text("+", color = CounterBtnActive, fontSize = 18.sp)
                }
            }
        }

        // Barre de progression
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((currentWeight.toFloat() / maxWeight).coerceIn(0f, 1f))
                    .height(4.dp)
                    .background(Color(0xFF1849D6), RoundedCornerShape(2.dp))
            )
        }
        Text(
            "$currentWeight kg cabine",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// ── Info Box (bleue ou orange) ────────────────────────────────────────────────

@Composable
private fun InfoBox(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(bottomStart = CARD_CORNER.dp, bottomEnd = CARD_CORNER.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Section Objets spéciaux ───────────────────────────────────────────────────

@Composable
private fun SpecialItemsSection(
    selectedItems: Set<String>,
    onToggle: (String) -> Unit
) {
    BaggageCard(hasBlueBorder = false) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecialItemRow(
                    item1 = "Équipement sportif",
                    item2 = "Animal de compagnie",
                    selectedItems = selectedItems,
                    onToggle = onToggle
                )
                SpecialItemRow(
                    item1 = "Poussette / siège bébé",
                    item2 = "Équipement médical",
                    selectedItems = selectedItems,
                    onToggle = onToggle
                )
            }
        }
    }
}

@Composable
private fun SpecialItemRow(
    item1: String,
    item2: String,
    selectedItems: Set<String>,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SpecialItemChip(
            text = item1,
            isSelected = item1 in selectedItems,
            onClick = { onToggle(item1) },
            modifier = Modifier.weight(1f)
        )
        SpecialItemChip(
            text = item2,
            isSelected = item2 in selectedItems,
            onClick = { onToggle(item2) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SpecialItemChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) BorderBlue else BorderLight,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) BorderBlue.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) BorderBlue else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ── Section Récapitulatif ─────────────────────────────────────────────────────

@Composable
private fun SummarySection(items: List<BaggageItem>) {
    BaggageCard (hasBlueBorder = false) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.5.dp, TextSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Récapitulatif", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Liste des bagages
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryRow("Bagage cabine", "1 x 7 kg")
                SummaryRow("Bagage soute", "Aucun")
                SummaryRow("Objets spéciaux", "Aucun")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Box verte avec prix
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SummaryGreen, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Surcoût bagages", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Poids total: 7 kg", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("0 $", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PriceGreen)
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

// ── Bouton Confirmer (fixe en bas à droite, comme NextButton) ─────────────────

@Composable
private fun ConfirmButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .shadow(6.dp, RoundedCornerShape(28.dp))
            .background(Color(0xFF2B3EDF), RoundedCornerShape(28.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(28.dp))  // ✅ Bordure subtile
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Confirmer",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, heightDp = 900)
@Composable
fun BaggageScreenPreview() {
    BaggageScreen()
}