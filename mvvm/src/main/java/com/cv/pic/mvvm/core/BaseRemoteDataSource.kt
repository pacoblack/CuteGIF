package com.cv.pic.mvvm.core

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

abstract class BaseRemoteDataSource {
  protected fun <T> executeRequest(
    request: () -> Observable<T>
  ): Observable<ApiResult<T>> {
    return request.invoke()
      .map { ApiResult.Success(it) as ApiResult<T> }
      .onErrorReturn { handleError(it) }
      .startWithItem(ApiResult.Loading)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  private fun handleError(throwable: Throwable): ApiResult.Error {
    return when (throwable) {
      is HttpException -> {
        val errorBody = throwable.response()?.errorBody()?.string()
        ApiResult.Error(
          code = throwable.code(),
          message = parseErrorMessage(errorBody),
          throwable = throwable
        )
      }
      is SocketTimeoutException -> ApiResult.Error(message = "Timeout")
      is IOException -> ApiResult.Error(message = "Network error")
      else -> ApiResult.Error(message = "Unknown error")
    }
  }

  private fun parseErrorMessage(errorBody: String?): String? {
    return try {
      errorBody?.let { JSONObject(it).getString("message") }
    } catch (e: Exception) {
      null
    }
  }
}