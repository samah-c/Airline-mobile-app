package com.example.airline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.airline.navigation.AppNavGraph
import com.example.airline.ui.theme.AirlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirlineTheme(dynamicColor = false) {
                AppNavGraph()
            }
        }
    }
}
