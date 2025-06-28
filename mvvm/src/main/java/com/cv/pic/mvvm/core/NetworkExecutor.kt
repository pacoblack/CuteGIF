package com.cv.pic.mvvm.core

import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class NetworkExecutor @Inject constructor(
  private val apiService: ApiService
) {
  suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
  ): ApiResult<T> {
    return try {
      ApiResult.Success(apiCall.invoke())
    } catch (e: Exception) {
      when (e) {
        is HttpException -> ApiResult.Error(e.code(), e.message())
        is IOException -> ApiResult.Error(-1, "Network error")
        else -> ApiResult.Error(-2, "Unknown error: ${e.message}")
      }
    }
  }

  // 使用类方式执行请求
  suspend fun <T> executeClassRequest(
    endpoint: String,
    method: String = "GET",
    headers: Map<String, String> = emptyMap(),
    body: Any? = null,
    responseType: Class<T>
  ): ApiResult<T> {
    return safeApiCall {
      apiService.executeRequest(endpoint, method, headers, body, responseType)
    }
  }

  // 使用接口方式执行请求
  suspend fun <T> executeInterfaceRequest(
    serviceCall: suspend () -> T
  ): ApiResult<T> {
    return safeApiCall {
      serviceCall.invoke()
    }
  }
}