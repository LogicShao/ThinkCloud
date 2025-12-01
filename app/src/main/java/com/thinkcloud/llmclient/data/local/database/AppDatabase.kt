package com.thinkcloud.llmclient.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thinkcloud.llmclient.data.local.dao.ConversationDao
import com.thinkcloud.llmclient.data.local.dao.MessageDao
import com.thinkcloud.llmclient.data.local.entity.ConversationEntity
import com.thinkcloud.llmclient.data.local.entity.MessageEntity

/**
 * Room 数据库
 */
@Database(
  entities = [ConversationEntity::class, MessageEntity::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun conversationDao(): ConversationDao
  abstract fun messageDao(): MessageDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "thinkcloud_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
