package com.thinkcloud.llmclient.domain.model

/**
 * 主题模式
 */
enum class ThemeMode(val displayName: String) {
  /**
   * 跟随系统
   */
  SYSTEM("跟随系统"),

  /**
   * 浅色模式
   */
  LIGHT("浅色"),

  /**
   * 深色模式
   */
  DARK("深色");

  companion object {
    /**
     * 从字符串值转换为枚举
     */
    fun fromString(value: String?): ThemeMode {
      return when (value) {
        "LIGHT" -> LIGHT
        "DARK" -> DARK
        else -> SYSTEM // 默认跟随系统
      }
    }
  }
}
