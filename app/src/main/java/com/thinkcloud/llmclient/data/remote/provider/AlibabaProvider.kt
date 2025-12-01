package com.thinkcloud.llmclient.data.remote.provider

import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * 阿里云 DashScope / 通义千问供应商实现
 */
class AlibabaProvider(private val apiConfig: ApiConfig) : LlmProvider {

  override val providerType: LlmProviderType = LlmProviderType.ALIBABA

  override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> {
    // 简化实现，返回非流式响应
    return flowOf(
      LlmResponse.Success(
        content = "这是来自通义千问的响应",
        model = request.model
      )
    )
  }

  override suspend fun getSupportedModels(): List<String> {
    return listOf(
      "qwen-turbo",
      "qwen-plus",
      "qwen-max",
      "qwen-long",
      "qwen-vl-plus"
    )
  }

  override suspend fun validateApiKey(apiKey: String): Boolean {
    return apiKey.isNotBlank()
  }

  override fun isAvailable(): Boolean {
    return apiConfig.alibabaApiKey.isNotBlank()
  }

  override fun getDisplayName(): String {
    return "通义千问"
  }

  override fun getDefaultModel(): String {
    return "qwen-turbo"
  }
}