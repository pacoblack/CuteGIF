package com.cv.pic.mvvm.core

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()

    // 添加认证头
    val authenticatedRequest = originalRequest.newBuilder()
      .header("Authorization", "Bearer YOUR_ACCESS_TOKEN")
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .build()

    return chain.proceed(authenticatedRequest)
  }
}