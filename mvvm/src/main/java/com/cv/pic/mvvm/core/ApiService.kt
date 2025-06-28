package com.cv.pic.mvvm.core

import com.cv.pic.mvvm.BuildConfig
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

// ApiService.kt
open class ApiService(
  private val retrofit: Retrofit
) {
  // 获取基础请求器
  suspend fun <T> executeRequest(
    endpoint: String,
    method: String = "GET",
    headers: Map<String, String> = emptyMap(),
    body: Any? = null,
    responseType: Class<T>
  ): T {
    val requestBuilder = Request.Builder()
      .url("${getBaseUrl()}$endpoint")
      .apply {
        when (method.uppercase()) {
          "GET" -> get()
          "POST" -> post(createBody(body))
          "PUT" -> put(createBody(body))
          "DELETE" -> delete(createBody(body))
          else -> get()
        }
      }

    // 添加自定义头
    headers.forEach { (key, value) ->
      requestBuilder.addHeader(key, value)
    }

    val request = requestBuilder.build()
    val response = getHttpClient().newCall(request).execute()

    if (!response.isSuccessful) {
      throw HttpException(Response.error<String>(0, response.body))
    }

    val responseBody = response.body?.string() ?: throw IOException("Empty response body")
    return getGson().fromJson(responseBody, responseType)
  }

  // 获取 Retrofit 实例（用于传统接口方式）
  fun <T> createService(service: Class<T>): T {
    return retrofit.create(service)
  }

  // 获取基础 URL（可被子类重写）
  open fun getBaseUrl(): String = ""

  // 获取 HTTP 客户端（可被子类重写）
  open fun getHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG)
          HttpLoggingInterceptor.Level.BODY
        else
          HttpLoggingInterceptor.Level.NONE
      })
      .build()
  }

  // 获取 Gson 实例（可被子类重写）
  open fun getGson(): Gson = Gson()

  private fun createBody(body: Any?): RequestBody {
    return if (body != null) {
      getGson().toJson(body)
        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    } else {
      ByteArray(0).toRequestBody(null)
    }
  }
}