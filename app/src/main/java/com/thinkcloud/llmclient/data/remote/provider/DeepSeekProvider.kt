package com.thinkcloud.llmclient.data.remote.provider

import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * DeepSeek 供应商实现
 * 支持官方 API 和阿里云百炼/Model Studio
 */
class DeepSeekProvider(private val apiConfig: ApiConfig) : LlmProvider {

    override val providerType: LlmProviderType = LlmProviderType.DEEPSEEK

    override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> {
        // 简化实现，返回非流式响应
        return flowOf(
            LlmResponse.Success(
                content = "这是来自 DeepSeek 的响应",
                model = request.model
            )
        )
    }

    override suspend fun getSupportedModels(): List<String> {
        return listOf(
            "deepseek-chat",
            "deepseek-coder",
            "deepseek-reasoner"
        )
    }

    override suspend fun validateApiKey(apiKey: String): Boolean {
        // 简化的验证逻辑 - 实际应该调用验证接口
        return apiKey.isNotBlank() && apiKey.startsWith("sk-")
    }

    override fun isAvailable(): Boolean {
        return apiConfig.deepSeekApiKey.isNotBlank()
    }

    override fun getDisplayName(): String {
        return "DeepSeek"
    }

    override fun getDefaultModel(): String {
        return "deepseek-chat"
    }
}