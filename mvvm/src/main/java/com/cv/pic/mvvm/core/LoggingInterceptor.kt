package com.cv.pic.mvvm.core

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class LoggingInterceptor constructor() : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val t1 = System.nanoTime()
    Log.d("NetworkLib", "Sending request: ${request.url}")

    // 打印请求体
    if (request.body != null) {
      val buffer = Buffer()
      request.body!!.writeTo(buffer)
      Log.d("NetworkLib", "Request Body: ${buffer.readUtf8()}")
    }

    val response = chain.proceed(request)

    val t2 = System.nanoTime()
    Log.d("NetworkLib", "Received response for ${response.request.url} in ${(t2 - t1) / 1e6} ms")
    Log.d("NetworkLib", "Response Code: ${response.code}")

    // 打印响应体
    val responseBody = response.peekBody(1024 * 1024) // 限制打印大小
    Log.d("NetworkLib", "Response Body: ${responseBody.string()}")

    return response
  }
}