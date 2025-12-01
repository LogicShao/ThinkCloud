package com.thinkcloud.llmclient.data.remote.provider

import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.LlmProviderType

/**
 * LLM 供应商工厂
 * 负责创建和管理不同的 LLM 供应商实例
 */
class ProviderFactory(private val apiConfig: ApiConfig) {

  private val providers = mutableMapOf<LlmProviderType, LlmProvider>()

  /**
   * 获取指定类型的供应商
   */
  fun getProvider(type: LlmProviderType): LlmProvider {
    return providers.getOrPut(type) {
      when (type) {
        LlmProviderType.DEEPSEEK -> DeepSeekProvider(apiConfig)
        LlmProviderType.ALIBABA -> AlibabaProvider(apiConfig)
        LlmProviderType.KIMI -> KimiProvider(apiConfig)
      }
    }
  }

  /**
   * 获取所有可用的供应商
   */
  fun getAvailableProviders(): List<LlmProvider> {
    return LlmProviderType.values()
      .map { getProvider(it) }
      .filter { it.isAvailable() }
  }

  /**
   * 检查供应商是否可用
   */
  fun isProviderAvailable(type: LlmProviderType): Boolean {
    return getProvider(type).isAvailable()
  }

  /**
   * 更新 API 配置
   */
  fun updateApiConfig(deepSeekApiKey: String, alibabaApiKey: String, kimiApiKey: String) {
    apiConfig.deepSeekApiKey = deepSeekApiKey
    apiConfig.alibabaApiKey = alibabaApiKey
    apiConfig.kimiApiKey = kimiApiKey
    // 清除缓存，强制重新创建 Provider
    providers.clear()
  }
}