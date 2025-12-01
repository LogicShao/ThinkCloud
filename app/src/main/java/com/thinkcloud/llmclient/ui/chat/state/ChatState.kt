package com.thinkcloud.llmclient.ui.chat.state

import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.LlmProviderType

/**
 * 聊天界面状态
 */
data class ChatState(
  val currentConversationId: String? = null,
  val conversationTitle: String? = null,
  val messages: List<ChatMessage> = emptyList(),
  val inputText: String = "",
  val isLoading: Boolean = false,
  val selectedProvider: LlmProviderType = LlmProviderType.DEEPSEEK,
  val selectedModel: String = "deepseek-chat",
  val availableModels: List<String> = listOf(
    "deepseek-chat",
    "deepseek-coder",
    "deepseek-reasoner"
  ),
  val isStreaming: Boolean = false,
  val errorMessage: String? = null
)

/**
 * 聊天界面事件
 */
sealed class ChatEvent {
  data class InputTextChanged(val text: String) : ChatEvent()
  data class SendMessage(val text: String) : ChatEvent()
  data class ProviderSelected(val provider: LlmProviderType) : ChatEvent()
  data class ModelSelected(val model: String) : ChatEvent()
  object ClearError : ChatEvent()
  object RetryLastMessage : ChatEvent()
  object SaveConversation : ChatEvent()
  data class LoadConversation(val conversationId: String) : ChatEvent()
  object NewConversation : ChatEvent()
}
