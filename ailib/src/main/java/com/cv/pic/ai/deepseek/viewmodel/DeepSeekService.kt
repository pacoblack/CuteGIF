package com.cv.pic.ai.deepseek.viewmodel

import com.cv.pic.mvvm.core.ApiService
import com.cv.pic.mvvm.core.NetworkResult
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService: ApiService {

  @POST("chat/completions")
  suspend fun chatCompletion(@Header("Authorization") token: String, @Field("message") message: String ): NetworkResult<ChatResponse>

}

