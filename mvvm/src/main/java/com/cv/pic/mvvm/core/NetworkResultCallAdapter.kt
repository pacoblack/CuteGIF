package com.cv.pic.mvvm.core

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResultCallAdapter private constructor(
  private val responseType: Type
) : CallAdapter<Type, Call<NetworkResult<Type>>> {

  override fun responseType() = responseType

  override fun adapt(call: Call<Type>): Call<NetworkResult<Type>> {
    return NetworkResultCall(call)
  }

  class Factory : CallAdapter.Factory() {
    override fun get(
      returnType: Type,
      annotations: Array<Annotation>,
      retrofit: Retrofit
    ): CallAdapter<*, *>? {
      if (getRawType(returnType) != Call::class.java) return null

      val callType = getParameterUpperBound(0, returnType as ParameterizedType)
      if (getRawType(callType) != NetworkResult::class.java) return null

      val resultType = getParameterUpperBound(0, callType as ParameterizedType)
      return NetworkResultCallAdapter(resultType)
    }
  }
}

private class NetworkResultCall<T>(
  private val delegate: Call<T>
) : Call<NetworkResult<T>> {

  override fun enqueue(callback: Callback<NetworkResult<T>>) {
    delegate.enqueue(object : Callback<T> {
      override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
          response.body()?.let {
            callback.onResponse(
              this@NetworkResultCall,
              Response.success(NetworkResult.Success(it))
            )
          } ?: callback.onResponse(
            this@NetworkResultCall,
            Response.success(NetworkResult.Error(-1, "Empty response body"))
          )
        } else {
          callback.onResponse(
            this@NetworkResultCall,
            Response.success(NetworkResult.Error(response.code(), response.message()))
          )
        }
      }

      override fun onFailure(call: Call<T>, t: Throwable) {
        val code = (t as? HttpException)?.code() ?: -1
        callback.onResponse(
          this@NetworkResultCall,
          Response.success(NetworkResult.Error(code, t.message ?: "Network error"))
        )
      }
    })
  }

  override fun clone() = NetworkResultCall(delegate.clone())
  override fun execute() = throw UnsupportedOperationException("NetworkResultCall doesn't support execute")
  override fun isExecuted() = delegate.isExecuted
  override fun cancel() = delegate.cancel()
  override fun isCanceled() = delegate.isCanceled
  override fun request() = delegate.request()
  override fun timeout() = delegate.timeout()
}