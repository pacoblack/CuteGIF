package com.cv.pic.mvvm.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {

  private val retrofitCache = mutableMapOf<String, Retrofit>()
  private val okHttpClient by lazy {
    OkHttpClient.Builder()
      .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
      .addInterceptor(LoggingInterceptor())
      .addInterceptor(AuthInterceptor())
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
    val retrofit = retrofitCache.getOrPut(baseUrl) {
      Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResultCallAdapter.Factory())
        .build()
    }
    return retrofit.create(serviceClass)
  }
}