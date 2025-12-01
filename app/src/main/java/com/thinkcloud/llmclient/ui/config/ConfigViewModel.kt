package com.thinkcloud.llmclient.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.data.remote.provider.ProviderFactory
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.ThemeMode
import com.thinkcloud.llmclient.ui.config.state.ConfigEvent
import com.thinkcloud.llmclient.ui.config.state.ConfigState
import com.thinkcloud.llmclient.ui.theme.ThemeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfigViewModel(
  private val secureConfigManager: SecureConfigManager,
  private val providerFactory: ProviderFactory,
  private val themeViewModel: ThemeViewModel
) : ViewModel() {

  private val _state = MutableStateFlow(ConfigState())
  val state: StateFlow<ConfigState> = _state.asStateFlow()

  init {
    loadApiKeys()
  }

  fun onEvent(event: ConfigEvent) {
    when (event) {
      is ConfigEvent.DeepSeekApiKeyChanged -> {
        _state.update { it.copy(deepSeekApiKey = event.apiKey) }
      }

      is ConfigEvent.AlibabaApiKeyChanged -> {
        _state.update { it.copy(alibabaApiKey = event.apiKey) }
      }

      is ConfigEvent.KimiApiKeyChanged -> {
        _state.update { it.copy(kimiApiKey = event.apiKey) }
      }

      is ConfigEvent.ThemeModeChanged -> {
        _state.update { it.copy(themeMode = event.themeMode) }
      }

      ConfigEvent.SaveApiKeys -> {
        saveApiKeys()
      }

      ConfigEvent.ClearError -> {
        _state.update { it.copy(errorMessage = null) }
      }

      ConfigEvent.LoadApiKeys -> {
        loadApiKeys()
      }

      ConfigEvent.ClearSaveStatus -> {
        _state.update { it.copy(saveSuccess = false) }
      }
    }
  }

  private fun loadApiKeys() {
    _state.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      try {
        val deepSeekApiKey = secureConfigManager.getDeepSeekApiKey() ?: ""
        val alibabaApiKey = secureConfigManager.getAlibabaApiKey() ?: ""
        val kimiApiKey = secureConfigManager.getKimiApiKey() ?: ""
        val themeModeStr = secureConfigManager.getThemeMode()
        val themeMode = ThemeMode.fromString(themeModeStr)

        // 验证供应商可用性
        val providerStatus = mutableMapOf<String, Boolean>()
        if (deepSeekApiKey.isNotBlank()) {
          providerStatus["DeepSeek"] =
            providerFactory.isProviderAvailable(LlmProviderType.DEEPSEEK)
        }
        if (alibabaApiKey.isNotBlank()) {
          providerStatus["通义千问"] =
            providerFactory.isProviderAvailable(LlmProviderType.ALIBABA)
        }
        if (kimiApiKey.isNotBlank()) {
          providerStatus["Kimi"] =
            providerFactory.isProviderAvailable(LlmProviderType.KIMI)
        }

        _state.update {
          it.copy(
            deepSeekApiKey = deepSeekApiKey,
            alibabaApiKey = alibabaApiKey,
            kimiApiKey = kimiApiKey,
            themeMode = themeMode,
            providerStatus = providerStatus,
            isLoading = false
          )
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            errorMessage = "加载配置失败: ${e.message}",
            isLoading = false
          )
        }
      }
    }
  }

  private fun saveApiKeys() {
    _state.update { it.copy(isSaving = true) }

    viewModelScope.launch {
      try {
        // 保存 API 密钥
        secureConfigManager.saveDeepSeekApiKey(_state.value.deepSeekApiKey)
        secureConfigManager.saveAlibabaApiKey(_state.value.alibabaApiKey)
        secureConfigManager.saveKimiApiKey(_state.value.kimiApiKey)

        // 保存主题模式
        secureConfigManager.saveThemeMode(_state.value.themeMode.name)

        // 立即更新主题
        themeViewModel.updateThemeMode(_state.value.themeMode)

        // 更新 ProviderFactory 的配置
        providerFactory.updateApiConfig(
          _state.value.deepSeekApiKey,
          _state.value.alibabaApiKey,
          _state.value.kimiApiKey
        )

        // 验证供应商可用性
        val providerStatus = mutableMapOf<String, Boolean>()
        if (_state.value.deepSeekApiKey.isNotBlank()) {
          providerStatus["DeepSeek"] =
            providerFactory.isProviderAvailable(LlmProviderType.DEEPSEEK)
        }
        if (_state.value.alibabaApiKey.isNotBlank()) {
          providerStatus["通义千问"] =
            providerFactory.isProviderAvailable(LlmProviderType.ALIBABA)
        }
        if (_state.value.kimiApiKey.isNotBlank()) {
          providerStatus["Kimi"] =
            providerFactory.isProviderAvailable(LlmProviderType.KIMI)
        }

        _state.update {
          it.copy(
            isSaving = false,
            saveSuccess = true,
            providerStatus = providerStatus,
            errorMessage = null
          )
        }

        // 3秒后清除成功状态
        viewModelScope.launch {
          kotlinx.coroutines.delay(3000)
          _state.update { it.copy(saveSuccess = false) }
        }
      } catch (e: Exception) {
        _state.update {
          it.copy(
            isSaving = false,
            errorMessage = "保存配置失败: ${e.message}"
          )
        }
      }
    }
  }
}