package com.thinkcloud.llmclient.ui.chat

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.thinkcloud.llmclient.ui.chat.components.MessageBubble
import com.thinkcloud.llmclient.ui.chat.components.MessageInput
import com.thinkcloud.llmclient.ui.chat.components.ModelSelector
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  onSettingsClick: () -> Unit,
  onHistoryClick: () -> Unit = {},
  viewModel: ChatViewModel = koinViewModel()
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  // 添加State变化日志
  LaunchedEffect(state.messages.size, state.isStreaming) {
    Log.d("ChatScreen", "📊 State变化 - 消息数: ${state.messages.size}, 流式中: ${state.isStreaming}")
    state.messages.lastOrNull()?.let { lastMsg ->
      Log.d("ChatScreen", "📝 最后消息 - 长度: ${lastMsg.content.length}, 流式: ${lastMsg.isStreaming}")
    }
  }
  val snackbarHostState = remember { SnackbarHostState() }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  // 判断是否在底部
  val isAtBottom by remember {
    derivedStateOf {
      val layoutInfo = listState.layoutInfo
      val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
      lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
    }
  }

  // 自动滚动到底部（仅当用户在底部时）
  LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.content) {
    if (state.messages.isNotEmpty() && (isAtBottom || state.messages.size == 1)) {
      // 平滑滚动到底部
      listState.animateScrollToItem(state.messages.size - 1)
    }
  }

  // 显示错误消息
  LaunchedEffect(state.errorMessage) {
    state.errorMessage?.let { error ->
      snackbarHostState.showSnackbar(error)
      viewModel.onEvent(ChatEvent.ClearError)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "ThinkCloud AI",
            style = MaterialTheme.typography.titleLarge
          )
        },
        actions = {
          IconButton(onClick = onHistoryClick) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "对话历史"
            )
          }
          IconButton(onClick = onSettingsClick) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "设置"
            )
          }
        }
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // 模型选择器
      ModelSelector(
        selectedProvider = state.selectedProvider,
        selectedModel = state.selectedModel,
        availableModels = state.availableModels,
        onProviderSelected = { provider ->
          viewModel.onEvent(ChatEvent.ProviderSelected(provider))
        },
        onModelSelected = { model ->
          viewModel.onEvent(ChatEvent.ModelSelected(model))
        }
      )

      // 消息列表
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxSize()
      ) {
        if (state.messages.isEmpty()) {
          // 空状态
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "开始与 AI 对话",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            reverseLayout = false,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
              top = 8.dp,
              bottom = 8.dp
            )
          ) {
            items(
              items = state.messages,
              key = { message -> message.id },
              contentType = { message ->
                "${message.role}_${message.isStreaming}_${message.content.length}"
              }
            ) { message ->
              MessageBubble(message = message)
            }
          }
        }

        // 加载指示器
        if (state.isLoading) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
          ) {
            CircularProgressIndicator()
          }
        }

        // "滚动到底部"按钮
        androidx.compose.animation.AnimatedVisibility(
          visible = !isAtBottom && state.messages.isNotEmpty(),
          enter = fadeIn(),
          exit = fadeOut(),
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
        ) {
          SmallFloatingActionButton(
            onClick = {
              coroutineScope.launch {
                listState.animateScrollToItem(state.messages.size - 1)
              }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "滚动到底部"
            )
          }
        }
      }

      // 消息输入框
      MessageInput(
        text = state.inputText,
        onTextChanged = { text ->
          viewModel.onEvent(ChatEvent.InputTextChanged(text))
        },
        onSendClicked = {
          viewModel.onEvent(ChatEvent.SendMessage(state.inputText))
        },
        isLoading = state.isLoading
      )
    }
  }
}