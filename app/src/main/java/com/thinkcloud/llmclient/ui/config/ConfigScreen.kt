package com.thinkcloud.llmclient.ui.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.thinkcloud.llmclient.ui.config.components.ApiKeyInput
import com.thinkcloud.llmclient.ui.config.components.SaveButton
import com.thinkcloud.llmclient.ui.config.state.ConfigEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    onBackClick: () -> Unit,
    viewModel: ConfigViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 显示错误消息
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ConfigEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "API 密钥配置",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            // 加载状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 说明文本
                Text(
                    text = "请配置您要使用的 AI 供应商 API 密钥。配置完成后，您可以在聊天界面切换不同的供应商。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // DeepSeek 配置
                ApiKeyInput(
                    providerName = "DeepSeek",
                    apiKey = state.deepSeekApiKey,
                    onApiKeyChanged = { apiKey ->
                        viewModel.onEvent(ConfigEvent.DeepSeekApiKeyChanged(apiKey))
                    },
                    isAvailable = state.providerStatus.get("DeepSeek")
                )

                // 阿里云通义千问配置
                ApiKeyInput(
                    providerName = "通义千问",
                    apiKey = state.alibabaApiKey,
                    onApiKeyChanged = { apiKey ->
                        viewModel.onEvent(ConfigEvent.AlibabaApiKeyChanged(apiKey))
                    },
                    isAvailable = state.providerStatus.get("通义千问")
                )

                // Kimi 配置
                ApiKeyInput(
                    providerName = "Kimi",
                    apiKey = state.kimiApiKey,
                    onApiKeyChanged = { apiKey ->
                        viewModel.onEvent(ConfigEvent.KimiApiKeyChanged(apiKey))
                    },
                    isAvailable = state.providerStatus.get("Kimi")
                )

                // 保存按钮
                SaveButton(
                    isSaving = state.isSaving,
                    isSuccess = state.saveSuccess,
                    onClick = {
                        viewModel.onEvent(ConfigEvent.SaveApiKeys)
                    }
                )
            }
        }
    }
}