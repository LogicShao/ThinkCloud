package com.thinkcloud.llmclient.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * DeepSeek API Service 接口
 */
interface DeepSeekApiService {

    /**
     * 发送聊天请求（非流式）
     */
    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: DeepSeekChatRequest
    ): Response<DeepSeekChatResponse>

    /**
     * 发送聊天请求（流式）
     */
    @Streaming
    @POST("v1/chat/completions")
    suspend fun chatStream(
        @Header("Authorization") authorization: String,
        @Body request: DeepSeekChatRequest
    ): Response<ResponseBody>
}
