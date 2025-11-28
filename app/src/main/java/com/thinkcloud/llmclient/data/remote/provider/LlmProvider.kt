package com.thinkcloud.llmclient.data.remote.provider

import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import kotlinx.coroutines.flow.Flow

/**
 * 统一的 LLM 供应商接口
 * 所有供应商实现都必须遵循此接口
 */
interface LlmProvider {

    /**
     * 获取供应商类型
     */
    val providerType: LlmProviderType

    /**
     * 发送消息到 LLM
     * @param request LLM 请求参数
     * @return 响应流，支持流式和非流式
     */
    suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse>

    /**
     * 获取支持的模型列表
     */
    suspend fun getSupportedModels(): List<String>

    /**
     * 验证 API 密钥
     */
    suspend fun validateApiKey(apiKey: String): Boolean

    /**
     * 检查供应商是否可用（配置了有效的 API Key）
     */
    fun isAvailable(): Boolean

    /**
     * 获取供应商显示名称
     */
    fun getDisplayName(): String

    /**
     * 获取默认模型
     */
    fun getDefaultModel(): String
}