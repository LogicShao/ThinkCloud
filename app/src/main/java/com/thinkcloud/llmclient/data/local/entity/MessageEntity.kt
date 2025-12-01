package com.thinkcloud.llmclient.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 消息实体
 */
@Entity(
  tableName = "messages",
  foreignKeys = [
    ForeignKey(
      entity = ConversationEntity::class,
      parentColumns = ["id"],
      childColumns = ["conversationId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("conversationId")]
)
data class MessageEntity(
  @PrimaryKey
  val id: String,
  val conversationId: String,
  val content: String,
  val role: String, // USER, ASSISTANT, SYSTEM
  val timestamp: Long,
  val model: String? = null,
  val provider: String? = null,
  val isError: Boolean = false,
  val errorMessage: String? = null
)
