package com.thinkcloud.llmclient.domain.repository

import com.thinkcloud.llmclient.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

/**
 * 对话历史仓库接口
 */
interface ConversationRepository {

  /**
   * 获取所有对话列表
   */
  fun getAllConversations(): Flow<List<Conversation>>

  /**
   * 根据 ID 获取对话（包含消息）
   */
  suspend fun getConversationById(conversationId: String): Conversation?

  /**
   * 保存对话（新建或更新）
   */
  suspend fun saveConversation(conversation: Conversation)

  /**
   * 删除对话
   */
  suspend fun deleteConversation(conversationId: String)

  /**
   * 删除所有对话
   */
  suspend fun deleteAllConversations()
}
