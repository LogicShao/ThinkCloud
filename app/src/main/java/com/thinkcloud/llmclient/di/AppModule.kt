package com.thinkcloud.llmclient.di

import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.data.local.database.AppDatabase
import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.data.remote.provider.ProviderFactory
import com.thinkcloud.llmclient.data.repository.ChatRepositoryImpl
import com.thinkcloud.llmclient.data.repository.ConversationRepositoryImpl
import com.thinkcloud.llmclient.domain.repository.ChatRepository
import com.thinkcloud.llmclient.domain.repository.ConversationRepository
import com.thinkcloud.llmclient.ui.chat.ChatViewModel
import com.thinkcloud.llmclient.ui.config.ConfigViewModel
import com.thinkcloud.llmclient.ui.conversation.ConversationListViewModel
import com.thinkcloud.llmclient.ui.theme.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 应用依赖注入模块
 */
val appModule = module {

  // 安全配置管理器
  single { SecureConfigManager(get()) }

  // API 配置
  single { ApiConfig() }

  // Provider 工厂
  single { ProviderFactory(get<ApiConfig>()) }

  // 数据库
  single { AppDatabase.getDatabase(androidContext()) }

  // 仓库
  single<ChatRepository> { ChatRepositoryImpl(get<ApiConfig>(), get<ProviderFactory>()) }
  single<ConversationRepository> {
    ConversationRepositoryImpl(
      get<AppDatabase>().conversationDao(),
      get<AppDatabase>().messageDao()
    )
  }

  // 主题 ViewModel - 使用 single 确保全局单例
  single { ThemeViewModel(get<SecureConfigManager>()) }

  // 其他 ViewModel
  viewModel { ChatViewModel(get<ChatRepository>(), get<ConversationRepository>()) }
  viewModel {
    ConfigViewModel(
      get<SecureConfigManager>(),
      get<ProviderFactory>(),
      get<ThemeViewModel>()
    )
  }
  viewModel { ConversationListViewModel(get<ConversationRepository>()) }
}
