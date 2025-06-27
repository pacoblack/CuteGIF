package com.cv.pic.mvvm.core

sealed class ApiResult<out T> {
  data class Success<out T>(val data: T) : ApiResult<T>()
  data class Error(
    val code: Int = -1,
    val message: String? = null,
    val throwable: Throwable? = null
  ) : ApiResult<Nothing>()
  object Loading : ApiResult<Nothing>()
}