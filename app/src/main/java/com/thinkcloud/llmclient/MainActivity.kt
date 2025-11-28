package com.thinkcloud.llmclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.thinkcloud.llmclient.ui.chat.ChatScreen
import com.thinkcloud.llmclient.ui.config.ConfigScreen
import com.thinkcloud.llmclient.ui.theme.ThinkCloudTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThinkCloudTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Chat) }

                when (currentScreen) {
                    Screen.Chat -> {
                        ChatScreen(
                            onSettingsClick = {
                                currentScreen = Screen.Config
                            }
                        )
                    }

                    Screen.Config -> {
                        ConfigScreen(
                            onBackClick = {
                                currentScreen = Screen.Chat
                            }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Chat : Screen()
    object Config : Screen()
}