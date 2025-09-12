package com.example.remindermatemock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.remindermatemock.screen.HomeScreen
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderMateMockTheme {
                    AppNavigation()
            }
        }
    }
}
