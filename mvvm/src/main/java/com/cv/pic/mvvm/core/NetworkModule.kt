package com.cv.pic.mvvm.core

import android.content.Context
import com.cv.pic.mvvm.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  const val BASE_URL = ""

  @Provides
  @Singleton
  fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    loggingInterceptor: LoggingInterceptor,
    @ApplicationContext context: Context
  ): OkHttpClient {
    val builder = OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .addInterceptor(authInterceptor)

    if (BuildConfig.LOG_NETWORK) {
      builder.addInterceptor(loggingInterceptor)
    }

    return builder.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
  }

  @Provides
  @Singleton
  fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

  @Provides
  @Singleton
  fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor()
}