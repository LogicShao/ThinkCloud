package com.thinkcloud.llmclient.ui.config.state

/**
 * 配置界面状态
 */
data class ConfigState(
    val deepSeekApiKey: String = "",
    val alibabaApiKey: String = "",
    val kimiApiKey: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val providerStatus: Map<String, Boolean> = emptyMap() // 供应商可用状态
)

/**
 * 配置界面事件
 */
sealed class ConfigEvent {
    data class DeepSeekApiKeyChanged(val apiKey: String) : ConfigEvent()
    data class AlibabaApiKeyChanged(val apiKey: String) : ConfigEvent()
    data class KimiApiKeyChanged(val apiKey: String) : ConfigEvent()
    object SaveApiKeys : ConfigEvent()
    object ClearError : ConfigEvent()
    object LoadApiKeys : ConfigEvent()
    object ClearSaveStatus : ConfigEvent()
}