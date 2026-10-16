package com.example.gestpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestpro.ui.navigation.AppNavigation
import com.example.gestpro.ui.theme.GestProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestProTheme {
                AppNavigation()
            }
        }
    }
}