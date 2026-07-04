package com.example.unitrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.unitrack.navigation.AppNavGraph
import com.example.unitrack.ui.theme.UniTrackTheme
import com.example.unitrack.ui.components.NotificationPermissionHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniTrackTheme {
                NotificationPermissionHandler()
                AppNavGraph()
            }
        }
    }
}