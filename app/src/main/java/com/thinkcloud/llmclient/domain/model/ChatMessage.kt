package com.thinkcloud.llmclient.domain.model

import java.util.UUID

/**
 * 聊天消息数据模型
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis(),
    val model: String? = null, // 使用的模型名称
    val provider: LlmProviderType? = null, // 使用的供应商
    val isStreaming: Boolean = false, // 是否正在流式输出
    val isError: Boolean = false, // 是否是错误消息
    val errorMessage: String? = null // 错误信息
)

/**
 * 消息角色枚举
 */
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

/**
 * LLM 供应商类型枚举
 */
enum class LlmProviderType {
    DEEPSEEK, ALIBABA, KIMI
    // 预留扩展：ZHIPU, BAIDU, TENCENT, etc.
}