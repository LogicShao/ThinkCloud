package com.thinkcloud.llmclient.data.remote.api

import com.google.gson.annotations.SerializedName

/**
 * 阿里云 DashScope / 通义千问 API 请求数据模型（OpenAI 兼容模式）
 * 参考: https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope
 */
data class AlibabaChatRequest(
  @SerializedName("model")
  val model: String,

  @SerializedName("messages")
  val messages: List<AlibabaMessage>,

  @SerializedName("stream")
  val stream: Boolean = false,

  @SerializedName("max_tokens")
  val maxTokens: Int? = null,

  @SerializedName("temperature")
  val temperature: Float? = null,

  @SerializedName("top_p")
  val topP: Float? = null
)

/**
 * 阿里云消息模型（OpenAI 兼容）
 */
data class AlibabaMessage(
  @SerializedName("role")
  val role: String,

  @SerializedName("content")
  val content: String
)

/**
 * 阿里云 API 响应数据模型（非流式，OpenAI 兼容）
 */
data class AlibabaChatResponse(
  @SerializedName("id")
  val id: String,

  @SerializedName("object")
  val objectType: String,

  @SerializedName("created")
  val created: Long,

  @SerializedName("model")
  val model: String,

  @SerializedName("choices")
  val choices: List<AlibabaChoice>,

  @SerializedName("usage")
  val usage: AlibabaUsage
)

/**
 * 阿里云 Choice 模型
 */
data class AlibabaChoice(
  @SerializedName("index")
  val index: Int,

  @SerializedName("message")
  val message: AlibabaMessage?,

  @SerializedName("delta")
  val delta: AlibabaMessage?,

  @SerializedName("finish_reason")
  val finishReason: String?
)

/**
 * 阿里云流式响应数据模型（OpenAI 兼容）
 */
data class AlibabaStreamChatResponse(
  @SerializedName("id")
  val id: String,

  @SerializedName("object")
  val objectType: String,

  @SerializedName("created")
  val created: Long,

  @SerializedName("model")
  val model: String,

  @SerializedName("choices")
  val choices: List<AlibabaChoice>
)

/**
 * 阿里云 Usage 信息
 */
data class AlibabaUsage(
  @SerializedName("input_tokens")
  val inputTokens: Int,

  @SerializedName("output_tokens")
  val outputTokens: Int,

  @SerializedName("total_tokens")
  val totalTokens: Int
)

/**
 * 阿里云错误响应
 */
data class AlibabaErrorResponse(
  @SerializedName("code")
  val code: String,

  @SerializedName("message")
  val message: String,

  @SerializedName("request_id")
  val requestId: String? = null
)