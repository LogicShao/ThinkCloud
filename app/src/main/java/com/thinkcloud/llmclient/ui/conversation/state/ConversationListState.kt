package com.thinkcloud.llmclient.ui.conversation.state

import com.thinkcloud.llmclient.domain.model.Conversation

/**
 * 对话列表状态
 */
data class ConversationListState(
  val conversations: List<Conversation> = emptyList(),
  val isLoading: Boolean = false,
  val errorMessage: String? = null
)
