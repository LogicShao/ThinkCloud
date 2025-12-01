package com.thinkcloud.llmclient.domain.model

/**
 * 统一的 LLM 请求参数
 */
data class LlmRequest(
  val messages: List<ChatMessage>,
  val model: String,
  val maxTokens: Int? = null,
  val temperature: Float? = null,
  val topP: Float? = null,
  val stream: Boolean = false,
  val provider: LlmProviderType
)

/**
 * LLM 响应结果
 */
sealed class LlmResponse {
  data class Success(
    val content: String,
    val model: String,
    val usage: UsageInfo? = null
  ) : LlmResponse()

  data class Streaming(
    val content: String,
    val isComplete: Boolean = false
  ) : LlmResponse()

  data class Error(
    val message: String,
    val code: String? = null,
    val retryable: Boolean = false
  ) : LlmResponse()
}

/**
 * 使用量信息
 */
data class UsageInfo(
  val promptTokens: Int,
  val completionTokens: Int,
  val totalTokens: Int
)

/**
 * 支持的模型配置
 */
data class ModelConfig(
  val name: String,
  val provider: LlmProviderType,
  val maxTokens: Int,
  val supportsStreaming: Boolean
)