package com.cv.pic.ai.deepseek.viewmodel

import com.cv.pic.mvvm.core.BaseRepository
import com.cv.pic.mvvm.core.NetworkResult

class DeepSeekRepository(baseUrl: String = "https://api.deepseek.com/v1/"): BaseRepository(baseUrl) {
  private val deepSeekApi by lazy { createService<DeepSeekService>() }

  suspend fun chatCompletion(): NetworkResult<ChatResponse> {
    return deepSeekApi.chatCompletion("", "")
  }

  suspend fun login(email: String, password: String): NetworkResult<ChatResponse> {
    return deepSeekApi.post("login", UserLoginRequest(email, password))
  }
}