package com.cv.pic.exo.video

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object VideoDataSourceFactory {


  @OptIn(UnstableApi::class)
  fun buildCacheSourceFactory(context: Context, cache: Cache): CacheDataSource.Factory {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
      else HttpLoggingInterceptor.Level.NONE
    }

    val okHttpClient = OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(loggingInterceptor) // 示例：添加日志拦截器
      .build()

    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context, OkHttpDataSource.Factory(okHttpClient))

    // 创建支持缓存的数据源工厂
    val cacheDataSourceFactory = CacheDataSource.Factory()
      .setCache(cache)
      .setUpstreamDataSourceFactory(dataSourceFactory)
      .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    return cacheDataSourceFactory
  }
}