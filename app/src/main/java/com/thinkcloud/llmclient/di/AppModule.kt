package com.thinkcloud.llmclient.di

import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.data.remote.provider.ProviderFactory
import com.thinkcloud.llmclient.data.repository.ChatRepositoryImpl
import com.thinkcloud.llmclient.domain.repository.ChatRepository
import com.thinkcloud.llmclient.ui.chat.ChatViewModel
import com.thinkcloud.llmclient.ui.config.ConfigViewModel
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

    // 仓库
    single<ChatRepository> { ChatRepositoryImpl(get<ApiConfig>(), get<ProviderFactory>()) }

    // ViewModel
    viewModel { ChatViewModel(get<ChatRepository>()) }
    viewModel { ConfigViewModel(get<SecureConfigManager>(), get<ProviderFactory>()) }
}