package com.thinkcloud.llmclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thinkcloud.llmclient.domain.model.ThemeMode
import com.thinkcloud.llmclient.ui.chat.ChatScreen
import com.thinkcloud.llmclient.ui.chat.ChatViewModel
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent
import com.thinkcloud.llmclient.ui.config.ConfigScreen
import com.thinkcloud.llmclient.ui.conversation.ConversationListScreen
import com.thinkcloud.llmclient.ui.theme.ThemeViewModel
import com.thinkcloud.llmclient.ui.theme.ThinkCloudTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

  private val themeViewModel: ThemeViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
      val systemInDarkTheme = isSystemInDarkTheme()

      // 根据主题模式决定是否使用深色主题
      val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
      }

      ThinkCloudTheme(darkTheme = darkTheme) {
        val chatViewModel: ChatViewModel = koinViewModel()
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Chat) }

        when (currentScreen) {
          Screen.Chat -> {
            ChatScreen(
              onSettingsClick = {
                currentScreen = Screen.Config
              },
              onHistoryClick = {
                currentScreen = Screen.ConversationList
              },
              viewModel = chatViewModel
            )
          }

          Screen.Config -> {
            ConfigScreen(
              onBackClick = {
                currentScreen = Screen.Chat
              }
            )
          }

          Screen.ConversationList -> {
            ConversationListScreen(
              onBackClick = {
                currentScreen = Screen.Chat
              },
              onNewConversation = {
                chatViewModel.onEvent(ChatEvent.NewConversation)
                currentScreen = Screen.Chat
              },
              onConversationClick = { conversationId ->
                chatViewModel.onEvent(ChatEvent.LoadConversation(conversationId))
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
  object ConversationList : Screen()
}
