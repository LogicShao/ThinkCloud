package com.thinkcloud.llmclient.data.remote.provider

import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Kimi (月之暗面) 供应商实现
 */
class KimiProvider(private val apiConfig: ApiConfig) : LlmProvider {

    override val providerType: LlmProviderType = LlmProviderType.KIMI

    override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> {
        // 简化实现，返回非流式响应
        return flowOf(
            LlmResponse.Success(
                content = "这是来自 Kimi 的响应",
                model = request.model
            )
        )
    }

    override suspend fun getSupportedModels(): List<String> {
        return listOf(
            "moonshot-v1-8k",
            "moonshot-v1-32k",
            "moonshot-v1-128k"
        )
    }

    override suspend fun validateApiKey(apiKey: String): Boolean {
        return apiKey.isNotBlank() && apiKey.startsWith("sk-")
    }

    override fun isAvailable(): Boolean {
        return apiConfig.kimiApiKey.isNotBlank()
    }

    override fun getDisplayName(): String {
        return "Kimi"
    }

    override fun getDefaultModel(): String {
        return "moonshot-v1-8k"
    }
}