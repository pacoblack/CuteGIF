package com.cv.pic.mvvm.core

sealed class NetworkResult<out T> {
  data class Success<out T>(val data: T) : NetworkResult<T>()
  data class Error(val code: Int = -1, val message: String?) : NetworkResult<Nothing>()
  object Loading : NetworkResult<Nothing>()
}