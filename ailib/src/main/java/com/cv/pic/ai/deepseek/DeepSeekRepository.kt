package com.cv.pic.ai.deepseek

import com.cv.pic.mvvm.core.ApiResult
import com.cv.pic.mvvm.core.ApiService
import com.cv.pic.mvvm.core.NetworkExecutor
import javax.inject.Inject

class DeepSeekRepository@Inject constructor(
  private val networkExecutor: NetworkExecutor,
  private val apiService: ApiService
) {
  suspend fun chatCompletion(): ApiResult<ChatResponse> {
    val userService = apiService.createService(DeepSeekService::class.java)
    return networkExecutor.executeInterfaceRequest {
      userService.chatCompletion(token = "", request = ChatRequest(messages = mutableListOf()))
    }
  }
}