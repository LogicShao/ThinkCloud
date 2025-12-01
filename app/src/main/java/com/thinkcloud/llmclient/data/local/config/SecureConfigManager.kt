package com.thinkcloud.llmclient.data.local.config

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 安全的配置管理器
 * 使用 Android Keystore 和 EncryptedSharedPreferences 安全存储 API 密钥
 */
class SecureConfigManager(private val context: Context) {

  companion object {
    private const val SHARED_PREFS_NAME = "secure_llm_config"
    private const val KEY_DEEPSEEK_API_KEY = "deepseek_api_key"
    private const val KEY_ALIBABA_API_KEY = "alibaba_api_key"
    private const val KEY_KIMI_API_KEY = "kimi_api_key"
    private const val KEY_THEME_MODE = "theme_mode"
  }

  private val sharedPreferences by lazy {
    try {
      val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

      EncryptedSharedPreferences.create(
        context,
        SHARED_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
    } catch (e: Exception) {
      // 如果加密存储失败，回退到普通 SharedPreferences（仅用于开发/测试）
      context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }
  }

  /**
   * 保存 DeepSeek API 密钥
   */
  suspend fun saveDeepSeekApiKey(apiKey: String) = withContext(Dispatchers.IO) {
    sharedPreferences.edit()
      .putString(KEY_DEEPSEEK_API_KEY, apiKey)
      .apply()
  }

  /**
   * 获取 DeepSeek API 密钥
   */
  suspend fun getDeepSeekApiKey(): String? = withContext(Dispatchers.IO) {
    sharedPreferences.getString(KEY_DEEPSEEK_API_KEY, null)
  }

  /**
   * 保存阿里云 API 密钥
   */
  suspend fun saveAlibabaApiKey(apiKey: String) = withContext(Dispatchers.IO) {
    sharedPreferences.edit()
      .putString(KEY_ALIBABA_API_KEY, apiKey)
      .apply()
  }

  /**
   * 获取阿里云 API 密钥
   */
  suspend fun getAlibabaApiKey(): String? = withContext(Dispatchers.IO) {
    sharedPreferences.getString(KEY_ALIBABA_API_KEY, null)
  }

  /**
   * 保存 Kimi API 密钥
   */
  suspend fun saveKimiApiKey(apiKey: String) = withContext(Dispatchers.IO) {
    sharedPreferences.edit()
      .putString(KEY_KIMI_API_KEY, apiKey)
      .apply()
  }

  /**
   * 获取 Kimi API 密钥
   */
  suspend fun getKimiApiKey(): String? = withContext(Dispatchers.IO) {
    sharedPreferences.getString(KEY_KIMI_API_KEY, null)
  }

  /**
   * 清除所有保存的 API 密钥
   */
  suspend fun clearAllApiKeys() = withContext(Dispatchers.IO) {
    sharedPreferences.edit()
      .remove(KEY_DEEPSEEK_API_KEY)
      .remove(KEY_ALIBABA_API_KEY)
      .remove(KEY_KIMI_API_KEY)
      .apply()
  }

  /**
   * 检查是否有任何 API 密钥已配置
   */
  suspend fun hasAnyApiKeyConfigured(): Boolean = withContext(Dispatchers.IO) {
    getDeepSeekApiKey() != null ||
      getAlibabaApiKey() != null ||
      getKimiApiKey() != null
  }

  /**
   * 获取所有已配置的供应商类型
   */
  suspend fun getConfiguredProviders(): List<String> = withContext(Dispatchers.IO) {
    val providers = mutableListOf<String>()
    if (getDeepSeekApiKey() != null) providers.add("DeepSeek")
    if (getAlibabaApiKey() != null) providers.add("通义千问")
    if (getKimiApiKey() != null) providers.add("Kimi")
    providers
  }

  /**
   * 保存主题模式
   */
  suspend fun saveThemeMode(themeMode: String) = withContext(Dispatchers.IO) {
    sharedPreferences.edit()
      .putString(KEY_THEME_MODE, themeMode)
      .apply()
  }

  /**
   * 获取主题模式
   */
  suspend fun getThemeMode(): String? = withContext(Dispatchers.IO) {
    sharedPreferences.getString(KEY_THEME_MODE, null)
  }
}