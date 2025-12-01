package com.thinkcloud.llmclient.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 主题管理 ViewModel
 * 负责管理应用主题状态
 */
class ThemeViewModel(
  private val secureConfigManager: SecureConfigManager
) : ViewModel() {

  private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  init {
    loadThemeMode()
  }

  /**
   * 加载主题配置
   */
  private fun loadThemeMode() {
    viewModelScope.launch {
      try {
        val themeModeStr = secureConfigManager.getThemeMode()
        _themeMode.value = ThemeMode.fromString(themeModeStr)
      } catch (e: Exception) {
        // 加载失败时使用默认值 (系统默认)
        _themeMode.value = ThemeMode.SYSTEM
      }
    }
  }

  /**
   * 更新主题模式
   * 由 ConfigViewModel 在保存配置后调用
   */
  fun updateThemeMode(themeMode: ThemeMode) {
    _themeMode.value = themeMode
  }
}
