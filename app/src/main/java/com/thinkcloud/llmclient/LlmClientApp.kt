package com.thinkcloud.llmclient

import android.app.Application
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity
import com.thinkcloud.llmclient.data.local.config.SecureConfigManager
import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

/**
 * 应用类，负责初始化依赖注入
 */
class LlmClientApp : Application() {

  override fun onCreate() {
    super.onCreate()

    // 初始化 Koin 依赖注入
    startKoin {
      androidContext(this@LlmClientApp)
      modules(appModule)
    }
  }
}