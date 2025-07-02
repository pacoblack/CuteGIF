package com.cv.pic.mvvm.core

import android.util.Log
import com.cv.pic.log.LogRecorder
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

class LoggingInterceptor() : Interceptor {

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
    recordStart(request)

    val response = chain.proceed(request)

    val t2 = System.nanoTime()
    Log.d("NetworkLib", "Received response for ${response.request.url} in ${(t2 - t1) / 1e6} ms")
    Log.d("NetworkLib", "Response Code: ${response.code}")

    // 打印响应体
    val responseBody = response.peekBody(1024 * 1024) // 限制打印大小
    Log.d("NetworkLib", "Response Body: ${responseBody.string()}")
    recordAfter(t1, response)
    return response
  }

  private fun recordStart(request: Request) {
    // 记录请求信息
    val requestLog = String.format(
      "Request:\n%s %s\nHeaders: %s",
      request.method,
      request.url,
      request.headers
    )
    LogRecorder.i("NETWORK", requestLog)
  }

  private fun recordAfter(t1: Long, response: Response) {
    val t2 = System.nanoTime()

    // 记录响应信息
    val responseLog = String.format(
      "Response for %s in %.1fms\nStatus: %d %s\nHeaders: %s",
      response.request.url,
      (t2 - t1) / 1e6,
      response.code,
      response.message,
      response.headers
    )
    LogRecorder.i("NETWORK", responseLog)
  }
}