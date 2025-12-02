package com.thinkcloud.llmclient.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.Conversation
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.MessageRole
import com.thinkcloud.llmclient.domain.repository.ChatRepository
import com.thinkcloud.llmclient.domain.repository.ConversationRepository
import com.thinkcloud.llmclient.ui.chat.state.ChatEvent
import com.thinkcloud.llmclient.ui.chat.state.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 聊天界面 ViewModel
 * 负责管理聊天状态、消息处理和对话历史
 */
class ChatViewModel(
  private val chatRepository: ChatRepository,
  private val conversationRepository: ConversationRepository
) : ViewModel() {

  companion object {
    private const val TAG = "ChatViewModel"
    // 流式更新节流间隔（毫秒）
    private const val STREAM_UPDATE_THROTTLE_MS = 50L
  }

  private val _state = MutableStateFlow(ChatState())
  val state: StateFlow<ChatState> = _state.asStateFlow()

  fun onEvent(event: ChatEvent) {
    when (event) {
      is ChatEvent.InputTextChanged -> {
        _state.update { it.copy(inputText = event.text) }
      }

      is ChatEvent.SendMessage -> {
        if (event.text.isNotBlank()) {
          sendMessage(event.text)
        }
      }

      is ChatEvent.ProviderSelected -> {
        _state.update { it.copy(selectedProvider = event.provider) }
        updateDefaultModel(event.provider)
      }

      is ChatEvent.ModelSelected -> {
        _state.update { it.copy(selectedModel = event.model) }
      }

      ChatEvent.ClearError -> {
        _state.update { it.copy(errorMessage = null) }
      }

      ChatEvent.RetryLastMessage -> {
        retryLastMessage()
      }

      ChatEvent.SaveConversation -> {
        saveConversation()
      }

      is ChatEvent.LoadConversation -> {
        loadConversation(event.conversationId)
      }

      ChatEvent.NewConversation -> {
        newConversation()
      }
    }
  }

  private fun sendMessage(text: String) {
    viewModelScope.launch {
      try {
        Log.d(TAG, "开始发送消息: $text")
        Log.d(TAG, "当前供应商: ${_state.value.selectedProvider}, 模型: ${_state.value.selectedModel}")

        // 清空输入框
        _state.update { it.copy(inputText = "") }

        // 添加用户消息
        val userMessage = ChatMessage(
          content = text,
          role = MessageRole.USER,
          model = _state.value.selectedModel,
          provider = _state.value.selectedProvider
        )

        _state.update {
          it.copy(messages = it.messages + userMessage)
        }
        Log.d(TAG, "已添加用户消息到UI")

        // 设置加载状态
        _state.update { it.copy(isLoading = true, isStreaming = true, errorMessage = null) }

        // 创建助手消息（初始流式消息）
        val assistantMessageId = UUID.randomUUID().toString()
        val assistantMessage = ChatMessage(
          id = assistantMessageId,
          content = "",
          role = MessageRole.ASSISTANT,
          model = _state.value.selectedModel,
          provider = _state.value.selectedProvider,
          isStreaming = true
        )

        _state.update { it.copy(messages = it.messages + assistantMessage) }
        Log.d(TAG, "已添加空的助手消息占位符")

        // 准备发送给LLM的消息历史（不包括空的助手消息）
        val messagesToSend = _state.value.messages.filter {
          it.role != MessageRole.ASSISTANT || it.content.isNotEmpty()
        }

        // 发送消息到 LLM
        val request = com.thinkcloud.llmclient.domain.model.LlmRequest(
          messages = messagesToSend,
          model = _state.value.selectedModel,
          stream = true,
          provider = _state.value.selectedProvider
        )

        Log.d(TAG, "准备调用API，消息数量: ${messagesToSend.size}")

        // 流式更新节流变量
        var lastUpdateTime = 0L
        var pendingContent = ""

        // 收集流式响应
        chatRepository.sendMessage(request)
          .catch { e ->
            Log.e(TAG, "流式响应发生错误", e)
            // 更新助手消息为错误状态
            _state.update { currentState ->
              currentState.copy(
                messages = currentState.messages.map { message ->
                  if (message.id == assistantMessageId) {
                    message.copy(
                      content = "",
                      isStreaming = false,
                      isError = true,
                      errorMessage = "网络错误: ${e.message}"
                    )
                  } else {
                    message
                  }
                },
                isLoading = false,
                isStreaming = false,
                errorMessage = "网络错误: ${e.message}"
              )
            }
          }
          .collect { response ->
            Log.d(TAG, "收到响应: ${response.javaClass.simpleName}")

            when (response) {
              is com.thinkcloud.llmclient.domain.model.LlmResponse.Streaming -> {
                val currentTime = System.currentTimeMillis()
                val shouldUpdate = response.isComplete ||
                                  (currentTime - lastUpdateTime) >= STREAM_UPDATE_THROTTLE_MS

                if (shouldUpdate) {
                  Log.d(TAG, "✓ UI更新 - 内容长度: ${response.content.length}, 完成: ${response.isComplete}, 间隔: ${currentTime - lastUpdateTime}ms")
                  lastUpdateTime = currentTime

                  // 更新助手消息内容 - 创建全新的列表引用
                  val updatedMessages = _state.value.messages.map { message ->
                    if (message.id == assistantMessageId) {
                      message.copy(
                        content = response.content,
                        isStreaming = !response.isComplete
                      )
                    } else {
                      message
                    }
                  }

                  _state.update { currentState ->
                    currentState.copy(
                      messages = updatedMessages,  // 使用新的列表引用
                      isLoading = false,
                      isStreaming = !response.isComplete
                    )
                  }

                  Log.d(TAG, "🔄 State已更新 - 列表hashCode: ${updatedMessages.hashCode()}")

                  // 如果流式响应完成，保存对话
                  if (response.isComplete) {
                    Log.d(TAG, "流式响应完成，最终内容长度: ${response.content.length}")
                    saveConversation()
                  }
                } else {
                  // 缓存待更新的内容
                  pendingContent = response.content
                  Log.v(TAG, "⊗ 节流跳过 - 内容长度: ${response.content.length}, 距上次: ${currentTime - lastUpdateTime}ms")
                }
              }

              is com.thinkcloud.llmclient.domain.model.LlmResponse.Success -> {
                Log.d(TAG, "收到成功响应，内容长度: ${response.content.length}")

                // 更新助手消息
                _state.update { currentState ->
                  currentState.copy(
                    messages = currentState.messages.map { message ->
                      if (message.id == assistantMessageId) {
                        message.copy(
                          content = response.content,
                          isStreaming = false,
                          model = response.model
                        )
                      } else {
                        message
                      }
                    },
                    isLoading = false,
                    isStreaming = false
                  )
                }

                Log.d(TAG, "非流式响应完成")
                // 保存对话
                saveConversation()
              }

              is com.thinkcloud.llmclient.domain.model.LlmResponse.Error -> {
                Log.e(TAG, "收到错误响应: ${response.message}, 代码: ${response.code}")

                // 更新助手消息为错误状态
                _state.update { currentState ->
                  currentState.copy(
                    messages = currentState.messages.map { message ->
                      if (message.id == assistantMessageId) {
                        message.copy(
                          content = "",
                          isStreaming = false,
                          isError = true,
                          errorMessage = response.message
                        )
                      } else {
                        message
                      }
                    },
                    isLoading = false,
                    isStreaming = false,
                    errorMessage = response.message
                  )
                }
              }
            }
          }

        Log.d(TAG, "消息发送流程完成")
      } catch (e: Exception) {
        Log.e(TAG, "发送消息失败", e)
        _state.update {
          it.copy(
            isLoading = false,
            isStreaming = false,
            errorMessage = "发送消息失败: ${e.message}"
          )
        }
      }
    }
  }

  private fun retryLastMessage() {
    val currentMessages = _state.value.messages
    if (currentMessages.isEmpty()) return

    // 找到最后一个用户消息
    val lastUserMessageIndex = currentMessages.indexOfLast { it.role == MessageRole.USER }
    if (lastUserMessageIndex == -1) return

    val userMessage = currentMessages[lastUserMessageIndex]

    // 移除该用户消息之后的所有消息
    val messagesBeforeRetry = currentMessages.take(lastUserMessageIndex + 1)
    _state.update { it.copy(messages = messagesBeforeRetry) }

    // 重新发送该消息
    sendMessage(userMessage.content)
  }

  private fun saveConversation() {
    viewModelScope.launch {
      try {
        val currentState = _state.value
        if (currentState.messages.isEmpty()) {
          Log.d(TAG, "消息列表为空，跳过保存")
          return@launch
        }

        val title = currentState.conversationTitle ?: run {
          val firstUserMessage = currentState.messages.firstOrNull { it.role == MessageRole.USER }
          firstUserMessage?.content?.take(30) ?: "新对话"
        }

        val conversationId = currentState.currentConversationId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val createdAt = if (currentState.currentConversationId != null) {
          conversationRepository.getConversationById(currentState.currentConversationId)?.createdAt
            ?: now
        } else {
          now
        }

        val conversation = Conversation(
          id = conversationId,
          title = title,
          createdAt = createdAt,
          updatedAt = now,
          messageCount = currentState.messages.size,
          messages = currentState.messages
        )

        Log.d(TAG, "保存对话: id=$conversationId, 标题=$title, 消息数=${currentState.messages.size}")
        conversationRepository.saveConversation(conversation)
        Log.d(TAG, "对话保存成功")

        _state.update {
          it.copy(
            currentConversationId = conversationId,
            conversationTitle = title
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "保存对话失败", e)
        // 静默失败
      }
    }
  }

  private fun loadConversation(conversationId: String) {
    viewModelScope.launch {
      try {
        Log.d(TAG, "加载对话: $conversationId")
        _state.update { it.copy(isLoading = true) }

        val conversation = conversationRepository.getConversationById(conversationId)
        if (conversation != null) {
          Log.d(TAG, "对话加载成功: 标题=${conversation.title}, 消息数=${conversation.messages.size}")
          _state.update {
            it.copy(
              currentConversationId = conversation.id,
              conversationTitle = conversation.title,
              messages = conversation.messages,
              isLoading = false
            )
          }
        } else {
          Log.w(TAG, "对话不存在: $conversationId")
          _state.update {
            it.copy(
              isLoading = false,
              errorMessage = "对话不存在"
            )
          }
        }
      } catch (e: Exception) {
        Log.e(TAG, "加载对话失败", e)
        _state.update {
          it.copy(
            isLoading = false,
            errorMessage = "加载对话失败: ${e.message}"
          )
        }
      }
    }
  }

  private fun newConversation() {
    Log.d(TAG, "创建新对话")
    _state.update {
      ChatState(
        selectedProvider = it.selectedProvider,
        selectedModel = it.selectedModel,
        availableModels = it.availableModels
      )
    }
  }

  /**
   * 更新默认模型
   * 当切换供应商时，自动加载该供应商支持的模型列表并选择第一个模型
   */
  private fun updateDefaultModel(provider: LlmProviderType) {
    viewModelScope.launch {
      try {
        Log.d(TAG, "更新供应商的默认模型: $provider")
        val models = chatRepository.getSupportedModels(provider)
        if (models.isNotEmpty()) {
          Log.d(TAG, "获取到${models.size}个模型: $models")
          _state.update {
            it.copy(
              selectedModel = models.first(),
              availableModels = models
            )
          }
        } else {
          Log.w(TAG, "供应商 $provider 没有可用模型")
        }
      } catch (e: Exception) {
        Log.e(TAG, "更新默认模型失败", e)
        // 忽略错误，保持当前模型
      }
    }
  }
}
