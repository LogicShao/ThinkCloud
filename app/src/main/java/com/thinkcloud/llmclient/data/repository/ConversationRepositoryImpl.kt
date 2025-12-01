package com.thinkcloud.llmclient.data.repository

import com.thinkcloud.llmclient.data.local.dao.ConversationDao
import com.thinkcloud.llmclient.data.local.dao.MessageDao
import com.thinkcloud.llmclient.data.local.entity.ConversationEntity
import com.thinkcloud.llmclient.data.local.entity.MessageEntity
import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.Conversation
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.MessageRole
import com.thinkcloud.llmclient.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 对话历史仓库实现
 */
class ConversationRepositoryImpl(
  private val conversationDao: ConversationDao,
  private val messageDao: MessageDao
) : ConversationRepository {

  override fun getAllConversations(): Flow<List<Conversation>> {
    return conversationDao.getAllConversations().map { entities ->
      entities.map { it.toDomainModel(emptyList()) }
    }
  }

  override suspend fun getConversationById(conversationId: String): Conversation? {
    val conversationEntity = conversationDao.getConversationById(conversationId)
      ?: return null

    val messageEntities = messageDao.getMessagesByConversation(conversationId).first()
    val messages = messageEntities.map { it.toDomainModel() }

    return conversationEntity.toDomainModel(messages)
  }

  override suspend fun saveConversation(conversation: Conversation) {
    // 保存对话实体
    val conversationEntity = ConversationEntity(
      id = conversation.id,
      title = conversation.title,
      createdAt = conversation.createdAt,
      updatedAt = conversation.updatedAt,
      messageCount = conversation.messages.size
    )
    conversationDao.insertConversation(conversationEntity)

    // 保存消息实体
    val messageEntities = conversation.messages.map { message ->
      MessageEntity(
        id = message.id,
        conversationId = conversation.id,
        content = message.content,
        role = message.role.name,
        timestamp = message.timestamp,
        model = message.model,
        provider = message.provider?.name,
        isError = message.isError,
        errorMessage = message.errorMessage
      )
    }
    messageDao.insertMessages(messageEntities)
  }

  override suspend fun deleteConversation(conversationId: String) {
    val conversation = conversationDao.getConversationById(conversationId) ?: return
    conversationDao.deleteConversation(conversation)
    // 由于设置了级联删除，消息会自动删除
  }

  override suspend fun deleteAllConversations() {
    conversationDao.deleteAllConversations()
  }
}

/**
 * 将 ConversationEntity 转换为 Conversation 领域模型
 */
private fun ConversationEntity.toDomainModel(messages: List<ChatMessage>): Conversation {
  return Conversation(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    messageCount = messageCount,
    messages = messages
  )
}

/**
 * 将 MessageEntity 转换为 ChatMessage 领域模型
 */
private fun MessageEntity.toDomainModel(): ChatMessage {
  return ChatMessage(
    id = id,
    content = content,
    role = MessageRole.valueOf(role),
    timestamp = timestamp,
    model = model,
    provider = provider?.let { LlmProviderType.valueOf(it) },
    isStreaming = false,
    isError = isError,
    errorMessage = errorMessage
  )
}
