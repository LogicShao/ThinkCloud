package com.thinkcloud.llmclient.data.repository

import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.data.remote.provider.LlmProvider
import com.thinkcloud.llmclient.data.remote.provider.ProviderFactory
import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import com.thinkcloud.llmclient.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

/**
 * 聊天仓库实现
 */
class ChatRepositoryImpl(
  private val apiConfig: ApiConfig,
  private val providerFactory: ProviderFactory
) : ChatRepository {

  private var currentProviderType: LlmProviderType = LlmProviderType.DEEPSEEK

  override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> {
    val provider = providerFactory.getProvider(request.provider)
    return provider.sendMessage(request)
  }

  override suspend fun getSupportedModels(provider: LlmProviderType): List<String> {
    return providerFactory.getProvider(provider).getSupportedModels()
  }

  override suspend fun validateApiKey(provider: LlmProviderType, apiKey: String): Boolean {
    return providerFactory.getProvider(provider).validateApiKey(apiKey)
  }

  override fun getCurrentProvider(): LlmProviderType {
    return currentProviderType
  }

  override fun setCurrentProvider(provider: LlmProviderType) {
    currentProviderType = provider
  }
}