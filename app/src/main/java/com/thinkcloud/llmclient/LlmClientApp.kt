package com.thinkcloud.llmclient

import android.app.Application
import com.thinkcloud.llmclient.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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