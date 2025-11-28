package com.thinkcloud.llmclient.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.thinkcloud.llmclient.ui.chat.components.MessageBubble
import com.thinkcloud.llmclient.ui.chat.components.MessageInput
import com.thinkcloud.llmclient.ui.chat.components.ModelSelector
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onSettingsClick: () -> Unit,
    viewModel: ChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // 自动滚动到底部
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.messages,
                            key = { message -> message.id }
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