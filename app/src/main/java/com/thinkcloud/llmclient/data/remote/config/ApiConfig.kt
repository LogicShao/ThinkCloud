package com.thinkcloud.llmclient.data.remote.config

/**
 * API 配置管理类
 * 负责管理所有供应商的 API 密钥和基础 URL
 */
class ApiConfig {
  // DeepSeek 配置
  var deepSeekApiKey: String = ""
  val deepSeekBaseUrl: String = "https://api.deepseek.com/"

  // 阿里云配置（通义千问）
  var alibabaApiKey: String = ""
  val alibabaBaseUrl: String = "https://dashscope.aliyuncs.com/compatible-mode/v1/"

  // Kimi 配置
  var kimiApiKey: String = ""
  val kimiBaseUrl: String = "https://api.moonshot.cn/v1/"

  /**
   * 检查是否有至少一个供应商可用
   */
  fun hasAvailableProvider(): Boolean {
    return deepSeekApiKey.isNotBlank() ||
      alibabaApiKey.isNotBlank() ||
      kimiApiKey.isNotBlank()
  }

  /**
   * 获取可用的供应商数量
   */
  fun getAvailableProviderCount(): Int {
    return listOf(
      deepSeekApiKey.isNotBlank(),
      alibabaApiKey.isNotBlank(),
      kimiApiKey.isNotBlank()
    ).count { it }
  }
}