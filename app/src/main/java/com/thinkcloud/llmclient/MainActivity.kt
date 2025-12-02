package com.thinkcloud.llmclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.ThemeMode
import com.thinkcloud.llmclient.ui.chat.ChatScreen
import com.thinkcloud.llmclient.ui.chat.ChatViewModel
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent
import com.thinkcloud.llmclient.ui.config.ConfigScreen
import com.thinkcloud.llmclient.ui.conversation.ConversationListScreen
import com.thinkcloud.llmclient.ui.theme.ThemeViewModel
import com.thinkcloud.llmclient.ui.theme.ThinkCloudTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

  private val themeViewModel: ThemeViewModel by viewModel()
  private val secureConfigManager: SecureConfigManager by inject()
  private val apiConfig: ApiConfig by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
      val systemInDarkTheme = isSystemInDarkTheme()

      val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
      }

      ThinkCloudTheme(darkTheme = darkTheme) {
        val chatViewModel: ChatViewModel = koinViewModel()
        // 使用状态管理：0=加载中, 1=聊天界面, 2=配置界面
        var appState by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
          lifecycleScope.launch {
            loadApiKeysToConfig()
            val hasApiKey = secureConfigManager.hasAnyApiKeyConfigured()
            // 等待动画效果
            delay(600)
            appState = if (hasApiKey) 1 else 2
          }
        }

        // 使用 Crossfade 实现平滑过渡
        androidx.compose.animation.Crossfade(
          targetState = appState,
          animationSpec = androidx.compose.animation.core.tween(500)
        ) { state ->
          when (state) {
            0 -> LoadingAnimation()
            1 -> ChatScreen(
              onSettingsClick = { appState = 2 },
              onHistoryClick = { appState = 3 },
              viewModel = chatViewModel
            )
            2 -> ConfigScreen(onBackClick = { appState = 1 })
            3 -> ConversationListScreen(
              onBackClick = { appState = 1 },
              onNewConversation = {
                chatViewModel.onEvent(ChatEvent.NewConversation)
                appState = 1
              },
              onConversationClick = { conversationId ->
                chatViewModel.onEvent(ChatEvent.LoadConversation(conversationId))
                appState = 1
              }
            )
            else -> LoadingAnimation()
          }
        }
      }
    }
  }

  /**
   * 从 SecureConfigManager 加载已保存的 API Key 到 ApiConfig
   */
  private suspend fun loadApiKeysToConfig() {
    try {
      apiConfig.deepSeekApiKey = secureConfigManager.getDeepSeekApiKey() ?: ""
      apiConfig.alibabaApiKey = secureConfigManager.getAlibabaApiKey() ?: ""
      apiConfig.kimiApiKey = secureConfigManager.getKimiApiKey() ?: ""
    } catch (e: Exception) {
      // 忽略错误，使用默认的空值
    }
  }
}

/**
 * 启动动画加载界面
 */
@Composable
fun LoadingAnimation() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(60.dp),
      strokeWidth = 4.dp
    )
  }
}
