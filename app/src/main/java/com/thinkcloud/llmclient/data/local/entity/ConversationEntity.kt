package com.thinkcloud.llmclient.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 对话会话实体
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val createdAt: Long,
  val updatedAt: Long,
  val messageCount: Int = 0
)
