package com.thinkcloud.llmclient.data.remote.provider

import android.util.Log
import com.google.gson.Gson
import com.thinkcloud.llmclient.data.remote.api.DeepSeekChatRequest
import com.thinkcloud.llmclient.data.remote.api.DeepSeekChatResponse
import com.thinkcloud.llmclient.data.remote.api.DeepSeekErrorResponse
import com.thinkcloud.llmclient.data.remote.api.DeepSeekMessage
import com.thinkcloud.llmclient.data.remote.api.KimiApiService
import com.thinkcloud.llmclient.data.remote.config.ApiConfig
import com.thinkcloud.llmclient.domain.model.LlmProviderType
import com.thinkcloud.llmclient.domain.model.LlmRequest
import com.thinkcloud.llmclient.domain.model.LlmResponse
import com.thinkcloud.llmclient.domain.model.MessageRole
import com.thinkcloud.llmclient.domain.model.UsageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Kimi（月之暗面）供应商实现
 */
class KimiProvider(private val apiConfig: ApiConfig) : LlmProvider {

  companion object {
    private const val TAG = "KimiProvider"
  }

  override val providerType: LlmProviderType = LlmProviderType.KIMI

  private val gson = Gson()

  private val apiService: KimiApiService by lazy {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()

    Retrofit.Builder()
      .baseUrl(apiConfig.kimiBaseUrl)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(KimiApiService::class.java)
  }

  override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> = flow {
    try {
      Log.d(TAG, "开始发送消息到Kimi")
      Log.d(TAG, "模型: ${request.model}, 流式: ${request.stream}, 消息数: ${request.messages.size}")

      val apiRequest = DeepSeekChatRequest(
        model = request.model,
        messages = request.messages.map { msg ->
          DeepSeekMessage(
            role = when (msg.role) {
              MessageRole.USER -> "user"
              MessageRole.ASSISTANT -> "assistant"
              MessageRole.SYSTEM -> "system"
            },
            content = msg.content
          )
        },
        stream = request.stream,
        maxTokens = request.maxTokens,
        temperature = request.temperature,
        topP = request.topP
      )

      val authorization = "Bearer ${apiConfig.kimiApiKey}"
      Log.d(TAG, "API密钥长度: ${apiConfig.kimiApiKey.length}")

      if (request.stream) {
        Log.d(TAG, "使用流式响应模式")
        handleStreamResponse(authorization, apiRequest, this)
      } else {
        Log.d(TAG, "使用非流式响应模式")
        handleNonStreamResponse(authorization, apiRequest, this)
      }
    } catch (e: Exception) {
      Log.e(TAG, "发送消息失败", e)
      emit(
        LlmResponse.Error(
          message = "发送消息失败: ${e.message}",
          retryable = true
        )
      )
    }
  }

  private suspend fun handleNonStreamResponse(
    authorization: String,
    request: DeepSeekChatRequest,
    collector: kotlinx.coroutines.flow.FlowCollector<LlmResponse>
  ) {
    Log.d(TAG, "发起非流式API请求")
    val response = withContext(Dispatchers.IO) {
      apiService.chat(authorization, request)
    }

    Log.d(TAG, "收到API响应: code=${response.code()}, success=${response.isSuccessful}")

    if (response.isSuccessful) {
      val body = response.body()
      if (body != null && body.choices.isNotEmpty()) {
        val content = body.choices.first().message?.content ?: ""
        Log.d(TAG, "响应成功，内容长度: ${content.length}")
        val usage = body.usage?.let {
          UsageInfo(
            promptTokens = it.promptTokens,
            completionTokens = it.completionTokens,
            totalTokens = it.totalTokens
          )
        }
        Log.d(TAG, "Token使用: ${usage?.totalTokens ?: 0}")
        collector.emit(
          LlmResponse.Success(
            content = content,
            model = body.model,
            usage = usage
          )
        )
      } else {
        Log.e(TAG, "响应体为空或没有choices")
        collector.emit(
          LlmResponse.Error(
            message = "响应为空",
            retryable = true
          )
        )
      }
    } else {
      val errorBody = response.errorBody()?.string()
      Log.e(TAG, "API请求失败: ${response.code()} - $errorBody")
      val errorMessage = try {
        val error = gson.fromJson(errorBody, DeepSeekErrorResponse::class.java)
        error.error.message
      } catch (e: Exception) {
        "HTTP ${response.code()}: ${response.message()}"
      }
      collector.emit(
        LlmResponse.Error(
          message = errorMessage,
          code = response.code().toString(),
          retryable = response.code() in 500..599
        )
      )
    }
  }

  private suspend fun handleStreamResponse(
    authorization: String,
    request: DeepSeekChatRequest,
    collector: kotlinx.coroutines.flow.FlowCollector<LlmResponse>
  ) {
    Log.d(TAG, "发起流式API请求")
    val response = withContext(Dispatchers.IO) {
      apiService.chatStream(authorization, request)
    }

    Log.d(TAG, "收到流式API响应: code=${response.code()}, success=${response.isSuccessful}")

    if (response.isSuccessful) {
      val responseBody = response.body()
      if (responseBody != null) {
        Log.d(TAG, "开始处理流式数据")
        val source = responseBody.source()
        val contentBuilder = StringBuilder()
        var chunkCount = 0

        while (!source.exhausted()) {
          val line = source.readUtf8Line() ?: continue

          if (line.startsWith("data: ")) {
            val data = line.substring(6).trim()

            if (data == "[DONE]") {
              Log.d(TAG, "流式数据接收完成，共${chunkCount}个chunk，总长度: ${contentBuilder.length}")
              collector.emit(
                LlmResponse.Streaming(
                  content = contentBuilder.toString(),
                  isComplete = true
                )
              )
              break
            }

            try {
              val chunk = gson.fromJson(data, DeepSeekChatResponse::class.java)
              val delta = chunk.choices.firstOrNull()?.delta?.content

              if (delta != null) {
                chunkCount++
                contentBuilder.append(delta)
                if (chunkCount % 10 == 0) {
                  Log.d(TAG, "已接收${chunkCount}个chunk，当前总长度: ${contentBuilder.length}")
                }
                collector.emit(
                  LlmResponse.Streaming(
                    content = contentBuilder.toString(),
                    isComplete = false
                  )
                )
              }
            } catch (e: Exception) {
              Log.w(TAG, "解析流式数据失败: $data", e)
            }
          }
        }
      } else {
        Log.e(TAG, "流式响应体为空")
        collector.emit(
          LlmResponse.Error(
            message = "响应体为空",
            retryable = true
          )
        )
      }
    } else {
      val errorBody = response.errorBody()?.string()
      Log.e(TAG, "流式API请求失败: ${response.code()} - $errorBody")
      val errorMessage = try {
        val error = gson.fromJson(errorBody, DeepSeekErrorResponse::class.java)
        error.error.message
      } catch (e: Exception) {
        "HTTP ${response.code()}: ${response.message()}"
      }
      collector.emit(
        LlmResponse.Error(
          message = errorMessage,
          code = response.code().toString(),
          retryable = response.code() in 500..599
        )
      )
    }
  }

  override suspend fun getSupportedModels(): List<String> {
    return listOf(
      "moonshot-v1-8k",
      "moonshot-v1-32k",
      "moonshot-v1-128k",
      "kimi-k2-0905-Preview",
      "kimi-k2-turbo-preview",
      "kimi-k2-thinking",
      "kimi-k2-thinking-turbo"
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