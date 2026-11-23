package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.firebase.configureAppCheck
import com.example.notifications.DailyReminderManager
import com.example.ui.RafeeqApp
import com.example.ui.theme.RafeeqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)
        DailyReminderManager.ensureNotificationChannel(this)

        // STEP 2: Enable edge-to-edge and launch UI
        enableEdgeToEdge()
        setContent {
            RafeeqTheme {
                Surface {
                    RafeeqApp()
                }
            }
        }
    }
}
