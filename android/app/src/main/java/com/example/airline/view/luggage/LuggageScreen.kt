package com.example.airline.view.luggage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airline.view.passeport.StepBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuggageScreen(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var cabinBags by remember { mutableStateOf(1) }
    var cabinWeight by remember { mutableStateOf("7") }
    var checkedBags by remember { mutableStateOf(0) }

    var sportEquipment by remember { mutableStateOf(false) }
    var pet by remember { mutableStateOf(false) }
    var babyStroller by remember { mutableStateOf(false) }
    var medicalEquipment by remember { mutableStateOf(false) }

    val checkedBagCost = if (checkedBags == 1) 35 else if (checkedBags >= 2) 60 else 0
    val totalCost = checkedBagCost

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Declare Your Luggage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color(0xFF1942D8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicator
            Text(
                text = "STEP 4 OF 5",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(4.dp))
                StepBar(isActive = false, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Flight Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Flight, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ALG ➔ CDG", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AF 1024", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text("Eco", fontWeight = FontWeight.Bold, color = Color(0xFF1942D8), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Siège 28", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bagage cabine
            SectionHeader(icon = Icons.Default.Flight, title = "Bagage cabine")
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1942D8), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Luggage, contentDescription = null, tint = Color(0xFF1942D8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bagage cabine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Max 10 Kg, 55x35x25 cm", color = Color.Gray, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Inclus", color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nombre de bagages", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                        Stepper(
                            value = cabinBags,
                            onMinus = { if (cabinBags > 0) cabinBags-- },
                            onPlus = { if (cabinBags < 2) cabinBags++ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Poids estimé", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = cabinWeight,
                            onValueChange = { cabinWeight = it },
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kg", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress bar
                    val weightFloat = cabinWeight.toFloatOrNull() ?: 0f
                    val progress = (weightFloat / 10f).coerceIn(0f, 1f)
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFFE5E7EB), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .background(Color(0xFF1942D8), RoundedCornerShape(2.dp)))
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${cabinWeight} Kg utilisés", fontSize = 10.sp, color = Color(0xFF1942D8), fontWeight = FontWeight.Bold)
                        Text("Limite : 10 Kg", fontSize = 10.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Un bagage cabine et un article personnel (sac à main) sont autorisés gratuitement.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bagage en soute
            SectionHeader(icon = Icons.Default.Flight, title = "Bagage en soute")
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧳", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bagage en soute", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Max 23 Kg, 158 cm total", color = Color.Gray, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Payant", color = Color(0xFFD97706), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nombre de bagages", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                        Stepper(
                            value = checkedBags,
                            onMinus = { if (checkedBags > 0) checkedBags-- },
                            onPlus = { if (checkedBags < 5) checkedBags++ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Des frais s'appliquent : 1 bagage = 35$, 2 bagages = 60$. Payable à l'étape suivante.", fontSize = 11.sp, color = Color(0xFF92400E))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Objets spéciaux
            SectionHeader(icon = Icons.Default.Flight, title = "Objets spéciaux")
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpecialItemBox(title = "Equipement sportif", icon = "🏂", isSelected = sportEquipment, onClick = { sportEquipment = !sportEquipment }, modifier = Modifier.weight(1f))
                SpecialItemBox(title = "Animal de compagnie", icon = "🐾", isSelected = pet, onClick = { pet = !pet }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpecialItemBox(title = "Poussette / siège bébé", icon = "👶", isSelected = babyStroller, onClick = { babyStroller = !babyStroller }, modifier = Modifier.weight(1f))
                SpecialItemBox(title = "Equipement médical", icon = "⚕️", isSelected = medicalEquipment, onClick = { medicalEquipment = !medicalEquipment }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Récapitulatif
            SectionHeader(icon = Icons.Default.Flight, title = "Récapitulatif")
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bagage cabine", color = Color.Gray, fontSize = 12.sp)
                Text("${cabinBags} x ${cabinWeight} kg", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bagage soute", color = Color.Gray, fontSize = 12.sp)
                Text(if (checkedBags > 0) "${checkedBags} bagage(s)" else "Aucun", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Objets spéciaux", color = Color.Gray, fontSize = 12.sp)
                val specialItemsCount = listOf(sportEquipment, pet, babyStroller, medicalEquipment).count { it }
                Text(if (specialItemsCount > 0) "${specialItemsCount} objet(s)" else "Aucun", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Surcoût bagages", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$totalCost $", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1942D8))
            ) {
                Text("Confirmer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun Stepper(value: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                .clickable(onClick = onMinus),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                .clickable(onClick = onPlus),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1942D8), modifier = Modifier.size(16.dp))
        Text(" - - ", color = Color(0xFF1942D8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun SpecialItemBox(title: String, icon: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF1942D8) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.White, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 14.sp)
        }
    }
}
