package com.thinkcloud.llmclient.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * Kimi API Service 接口（OpenAI 兼容）
 */
interface KimiApiService {

  /**
   * 发送聊天请求（非流式）
   */
  @POST("chat/completions")
  suspend fun chat(
    @Header("Authorization") authorization: String,
    @Body request: DeepSeekChatRequest // 复用 DeepSeek 的请求模型（OpenAI 兼容）
  ): Response<DeepSeekChatResponse> // 复用响应模型

  /**
   * 发送聊天请求（流式）
   */
  @Streaming
  @POST("chat/completions")
  suspend fun chatStream(
    @Header("Authorization") authorization: String,
    @Body request: DeepSeekChatRequest
  ): Response<ResponseBody>
}
