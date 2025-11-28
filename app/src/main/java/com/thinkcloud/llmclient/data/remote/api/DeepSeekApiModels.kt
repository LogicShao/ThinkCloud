package com.thinkcloud.llmclient.data.remote.api

import com.google.gson.annotations.SerializedName

/**
 * DeepSeek API 请求数据模型
 */
data class DeepSeekChatRequest(
    @SerializedName("model")
    val model: String,

    @SerializedName("messages")
    val messages: List<DeepSeekMessage>,

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
 * DeepSeek 消息模型
 */
data class DeepSeekMessage(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: String
)

/**
 * DeepSeek API 响应数据模型
 */
data class DeepSeekChatResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("created")
    val created: Long,

    @SerializedName("model")
    val model: String,

    @SerializedName("choices")
    val choices: List<DeepSeekChoice>,

    @SerializedName("usage")
    val usage: DeepSeekUsage?
)

/**
 * DeepSeek Choice 模型
 */
data class DeepSeekChoice(
    @SerializedName("index")
    val index: Int,

    @SerializedName("message")
    val message: DeepSeekMessage?,

    @SerializedName("delta")
    val delta: DeepSeekMessage?,

    @SerializedName("finish_reason")
    val finishReason: String?
)

/**
 * DeepSeek Usage 信息
 */
data class DeepSeekUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,

    @SerializedName("completion_tokens")
    val completionTokens: Int,

    @SerializedName("total_tokens")
    val totalTokens: Int
)

/**
 * DeepSeek 错误响应
 */
data class DeepSeekErrorResponse(
    @SerializedName("error")
    val error: DeepSeekError
)

data class DeepSeekError(
    @SerializedName("message")
    val message: String,

    @SerializedName("type")
    val type: String?,

    @SerializedName("code")
    val code: String?
)
