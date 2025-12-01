package com.thinkcloud.llmclient.ui.chat

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

        // 设置加载状态
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        // 创建助手消息（初始流式消息）
        val assistantMessage = ChatMessage(
          content = "",
          role = MessageRole.ASSISTANT,
          model = _state.value.selectedModel,
          provider = _state.value.selectedProvider,
          isStreaming = true
        )

        val currentMessages = _state.value.messages + assistantMessage
        _state.update { it.copy(messages = currentMessages) }

        // 发送消息到 LLM
        val request = com.thinkcloud.llmclient.domain.model.LlmRequest(
          messages = currentMessages.filter { it.role != MessageRole.ASSISTANT || it.content.isNotEmpty() },
          model = _state.value.selectedModel,
          stream = true,
          provider = _state.value.selectedProvider
        )

        // 这里调用聊天仓库发送消息
        // 根据实现方式，可能需要处理流式响应
        val response = chatRepository.sendMessage(request)

        // 更新助手消息
        val updatedMessages = _state.value.messages.map { message ->
          if (message === assistantMessage) {
            when (response) {
              is com.thinkcloud.llmclient.domain.model.LlmResponse.Success -> {
                message.copy(
                  content = response.content,
                  isStreaming = false,
                  model = response.model
                )
              }

              is com.thinkcloud.llmclient.domain.model.LlmResponse.Error -> {
                message.copy(
                  content = "",
                  isStreaming = false,
                  isError = true,
                  errorMessage = response.message
                )
              }

              else -> message
            }
          } else {
            message
          }
        }

        _state.update {
          it.copy(
            messages = updatedMessages,
            isLoading = false,
            isStreaming = false
          )
        }

        // 保存对话
        saveConversation()
      } catch (e: Exception) {
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
        if (currentState.messages.isEmpty()) return@launch

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

        conversationRepository.saveConversation(conversation)

        _state.update {
          it.copy(
            currentConversationId = conversationId,
            conversationTitle = title
          )
        }
      } catch (e: Exception) {
        // 静默失败
      }
    }
  }

  private fun loadConversation(conversationId: String) {
    viewModelScope.launch {
      try {
        _state.update { it.copy(isLoading = true) }

        val conversation = conversationRepository.getConversationById(conversationId)
        if (conversation != null) {
          _state.update {
            it.copy(
              currentConversationId = conversation.id,
              conversationTitle = conversation.title,
              messages = conversation.messages,
              isLoading = false
            )
          }
        } else {
          _state.update {
            it.copy(
              isLoading = false,
              errorMessage = "对话不存在"
            )
          }
        }
      } catch (e: Exception) {
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
}
