package com.thinkcloud.llmclient.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.thinkcloud.llmclient.data.local.entity.ConversationEntity
import com.thinkcloud.llmclient.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 对话会话 DAO
 */
@Dao
interface ConversationDao {

  /**
   * 获取所有对话列表（按更新时间倒序）
   */
  @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
  fun getAllConversations(): Flow<List<ConversationEntity>>

  /**
   * 根据 ID 获取对话
   */
  @Query("SELECT * FROM conversations WHERE id = :conversationId")
  suspend fun getConversationById(conversationId: String): ConversationEntity?

  /**
   * 插入新对话
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConversation(conversation: ConversationEntity)

  /**
   * 更新对话
   */
  @Update
  suspend fun updateConversation(conversation: ConversationEntity)

  /**
   * 删除对话
   */
  @Delete
  suspend fun deleteConversation(conversation: ConversationEntity)

  /**
   * 删除所有对话
   */
  @Query("DELETE FROM conversations")
  suspend fun deleteAllConversations()
}
