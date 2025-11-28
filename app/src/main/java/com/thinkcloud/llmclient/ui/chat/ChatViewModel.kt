package com.thinkcloud.llmclient.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import com.thinkcloud.llmclient.domain.model.MessageRole
import com.thinkcloud.llmclient.domain.repository.ChatRepository
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent
import com.thinkcloud.llmclient.ui.chat.state.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var currentStreamingMessageId: String? = null

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.InputTextChanged -> {
                _state.update { it.copy(inputText = event.text) }
            }

            is ChatEvent.SendMessage -> {
                sendMessage(event.text)
            }

            is ChatEvent.ProviderSelected -> {
                _state.update { it.copy(selectedProvider = event.provider) }
                // 更新默认模型
                updateDefaultModel(event.provider)
            }

            is ChatEvent.ModelSelected -> {
                _state.update { it.copy(selectedModel = event.model) }
            }

            ChatEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            ChatEvent.RetryLastMessage -> {
                // 重试最后一条消息的逻辑
                retryLastMessage()
            }
        }
    }

    private fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            content = text,
            role = MessageRole.USER
        )

        // 添加用户消息
        _state.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                inputText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                // 创建 AI 回复消息（初始为空）
                val assistantMessage = ChatMessage(
                    content = "",
                    role = MessageRole.ASSISTANT,
                    model = _state.value.selectedModel,
                    provider = _state.value.selectedProvider,
                    isStreaming = true
                )

                currentStreamingMessageId = assistantMessage.id

                // 添加初始的 AI 消息
                _state.update { state ->
                    state.copy(
                        messages = state.messages + assistantMessage
                    )
                }

                // 构建请求
                val request = LlmRequest(
                    messages = _state.value.messages.map { msg ->
                        ChatMessage(
                            content = msg.content,
                            role = msg.role
                        )
                    },
                    model = _state.value.selectedModel,
                    provider = _state.value.selectedProvider,
                    stream = true // 启用流式输出
                )

                // 发送消息并处理响应
                chatRepository.sendMessage(request).collect { response ->
                    when (response) {
                        is LlmResponse.Streaming -> {
                            // 更新流式消息内容
                            updateStreamingMessage(response.content, response.isComplete)
                        }

                        is LlmResponse.Success -> {
                            // 非流式响应
                            updateMessageWithFinalContent(response.content)
                        }

                        is LlmResponse.Error -> {
                            handleError(response.message, response.retryable)
                        }
                    }
                }
            } catch (e: Exception) {
                handleError("发送消息失败: ${e.message}", true)
            } finally {
                _state.update { it.copy(isLoading = false) }
                currentStreamingMessageId = null
            }
        }
    }

    private fun updateStreamingMessage(content: String, isComplete: Boolean) {
        _state.update { state ->
            val updatedMessages = state.messages.map { message ->
                if (message.id == currentStreamingMessageId) {
                    message.copy(
                        content = content,
                        isStreaming = !isComplete
                    )
                } else {
                    message
                }
            }
            state.copy(messages = updatedMessages)
        }
    }

    private fun updateMessageWithFinalContent(content: String) {
        _state.update { state ->
            val updatedMessages = state.messages.map { message ->
                if (message.id == currentStreamingMessageId) {
                    message.copy(
                        content = content,
                        isStreaming = false
                    )
                } else {
                    message
                }
            }
            state.copy(messages = updatedMessages)
        }
    }

    private fun handleError(errorMessage: String, retryable: Boolean) {
        _state.update { state ->
            val updatedMessages = if (currentStreamingMessageId != null) {
                state.messages.map { message ->
                    if (message.id == currentStreamingMessageId) {
                        message.copy(
                            isError = true,
                            errorMessage = errorMessage,
                            isStreaming = false
                        )
                    } else {
                        message
                    }
                }
            } else {
                // 添加错误消息
                val errorMessageObj = ChatMessage(
                    content = "",
                    role = MessageRole.ASSISTANT,
                    isError = true,
                    errorMessage = errorMessage
                )
                state.messages + errorMessageObj
            }

            state.copy(
                messages = updatedMessages,
                errorMessage = errorMessage,
                isLoading = false
            )
        }
    }

    private fun updateDefaultModel(provider: LlmProviderType) {
        viewModelScope.launch {
            try {
                val models = chatRepository.getSupportedModels(provider)
                if (models.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            selectedModel = models.first(),
                            availableModels = models
                        )
                    }
                }
            } catch (e: Exception) {
                // 忽略错误，保持当前模型
            }
        }
    }

    private fun retryLastMessage() {
        // 实现重试逻辑
        val lastUserMessage = _state.value.messages.lastOrNull { it.role == MessageRole.USER }
        lastUserMessage?.let { message ->
            sendMessage(message.content)
        }
    }
}