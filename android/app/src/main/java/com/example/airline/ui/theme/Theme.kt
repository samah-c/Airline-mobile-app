package com.example.airline.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Couleurs personnalisées (ton style) ──────────────────────────────────────
private val Purple40 = Color(0xFF6650a4)
private val PurpleGrey40 = Color(0xFF625b71)
private val Pink40 = Color(0xFF7D5260)
private val Purple80 = Color(0xFFD0BCFF)
private val PurpleGrey80 = Color(0xFFCCC2DC)
private val Pink80 = Color(0xFFEFB8C8)

// ── Light Color Scheme (tes couleurs personnalisées) ─────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1942D8),           // Ton bleu de marque
    secondary = Color(0xFF0F172A),         // Texte secondaire sombre
    tertiary = Pink40,

    // Couleurs de texte/contraste
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),

    // Fonds
    background = Color(0xFFF9FAFB),
    surface = Color.White
)

// ── Dark Color Scheme (version sombre cohérente) ─────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90B4F6),           // Bleu plus clair pour le dark mode
    secondary = Color(0xFFE2E8F0),
    tertiary = Pink80,

    onPrimary = Color(0xFF0F172A),
    onSecondary = Color(0xFF0F172A),
    onTertiary = Color.White,
    onBackground = Color(0xFFF9FAFB),
    onSurface = Color(0xFFF9FAFB),

    background = Color(0xFF111827),
    surface = Color(0xFF1F2937)
)

// ── Theme principal ──────────────────────────────────────────────────────────
@Composable
fun AirlineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // ← Désactivé pour garder tes couleurs personnalisées
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Couleurs dynamiques Material You (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Thème sombre personnalisé
        darkTheme -> DarkColorScheme
        // Thème clair personnalisé (défaut)
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}