package com.thinkcloud.llmclient.domain.repository

import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import kotlinx.coroutines.flow.Flow

/**
 * 聊天仓库接口 - 定义与 LLM 交互的契约
 */
interface ChatRepository {

  /**
   * 发送消息到 LLM
   */
  suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse>

  /**
   * 获取支持的模型列表
   */
  suspend fun getSupportedModels(provider: LlmProviderType): List<String>

  /**
   * 验证 API 密钥
   */
  suspend fun validateApiKey(provider: LlmProviderType, apiKey: String): Boolean

  /**
   * 获取当前活跃的供应商
   */
  fun getCurrentProvider(): LlmProviderType

  /**
   * 设置当前活跃的供应商
   */
  fun setCurrentProvider(provider: LlmProviderType)
}