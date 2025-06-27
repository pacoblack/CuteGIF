package com.cv.pic.mvvm.core

import com.google.gson.annotations.SerializedName

/**
 * 统一API响应封装
 * @param T 实际数据类型
 */
sealed class ApiResult<out T> {
  data class Success<out T>(val data: T) : ApiResult<T>()
  data class Error(
    val code: Int = -1,
    val message: String? = null,
    val throwable: Throwable? = null
  ) : ApiResult<Nothing>()
  object Loading : ApiResult<Nothing>()
  object Empty : ApiResult<Nothing>()
}

/**
 * 基础API响应模型
 * @param T 实际数据类型
 */
data class ApiResponse<T>(
  @SerializedName("code") val statusCode: Int,
  @SerializedName("message") val message: String?,
  @SerializedName("data") val data: T?
) {
  fun toResult(): ApiResult<T> {
    return when (statusCode) {
      in 200..299 -> {
        if (data != null) {
          ApiResult.Success(data)
        } else {
          ApiResult.Empty
        }
      }
      else -> ApiResult.Error(statusCode, message)
    }
  }
}