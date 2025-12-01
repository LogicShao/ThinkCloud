package com.thinkcloud.llmclient.domain.model

/**
 * 对话会话模型
 */
data class Conversation(
  val id: String,
  val title: String,
  val createdAt: Long,
  val updatedAt: Long,
  val messageCount: Int = 0,
  val messages: List<ChatMessage> = emptyList()
)
