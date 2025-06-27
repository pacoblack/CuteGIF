package com.cv.pic.mvvm.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

  private const val BASE_URL = "https://api.example.com/"

  private val retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
      .client(createOkHttpClient())
      .build()
  }

  fun createApiService(): ApiService {
    return retrofit.create(ApiService::class.java)
  }

  private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
      })
      .addInterceptor { chain ->
        val request = chain.request().newBuilder()
          .addHeader("Authorization", "Bearer token")
          .build()
        chain.proceed(request)
      }
      .build()
  }
}