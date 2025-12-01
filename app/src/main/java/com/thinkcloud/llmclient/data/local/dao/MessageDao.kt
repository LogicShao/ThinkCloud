package com.thinkcloud.llmclient.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thinkcloud.llmclient.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 消息 DAO
 */
@Dao
interface MessageDao {

  /**
   * 获取对话的所有消息（按时间戳排序）
   */
  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>>

  /**
   * 插入消息
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity)

  /**
   * 插入多条消息
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<MessageEntity>)

  /**
   * 删除消息
   */
  @Delete
  suspend fun deleteMessage(message: MessageEntity)

  /**
   * 删除对话的所有消息
   */
  @Query("DELETE FROM messages WHERE conversationId = :conversationId")
  suspend fun deleteMessagesByConversation(conversationId: String)

  /**
   * 获取对话的消息数量
   */
  @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
  suspend fun getMessageCount(conversationId: String): Int
}
