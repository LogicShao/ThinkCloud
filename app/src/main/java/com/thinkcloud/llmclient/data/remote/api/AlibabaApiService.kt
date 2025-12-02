package com.thinkcloud.llmclient.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * 阿里云 DashScope / 通义千问 API Service 接口
 * 参考: https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope
 */
interface AlibabaApiService {

  /**
   * 发送聊天请求（非流式）
   * 使用 OpenAI 兼容模式：/chat/completions
   */
  @POST("chat/completions")
  suspend fun chat(
    @Header("Authorization") authorization: String,
    @Header("Content-Type") contentType: String = "application/json",
    @Body request: AlibabaChatRequest
  ): Response<AlibabaChatResponse>

  /**
   * 发送聊天请求（流式）
   * 使用 OpenAI 兼容模式：/chat/completions
   */
  @Streaming
  @POST("chat/completions")
  suspend fun chatStream(
    @Header("Authorization") authorization: String,
    @Header("Content-Type") contentType: String = "application/json",
    @Body request: AlibabaChatRequest
  ): Response<ResponseBody>
}