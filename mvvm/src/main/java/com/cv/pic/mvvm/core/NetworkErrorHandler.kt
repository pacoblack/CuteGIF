package com.cv.pic.mvvm.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.cv.pic.mvvm.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkErrorHandler @Inject constructor(private val context: Context) {

  fun handle(throwable: Throwable): NetworkResult.Error {
    return when (throwable) {
      is HttpException -> {
        val errorBody = throwable.response()?.errorBody()?.string()
        NetworkResult.Error(
          code = throwable.code(),
          message = parseErrorMessage(errorBody) ?: context.getString(R.string.network_error_server)
        )
      }
      is SocketTimeoutException -> NetworkResult.Error(message = context.getString(R.string.network_error_timeout))
      is UnknownHostException -> {
        if (isNetworkAvailable(context)) {
          NetworkResult.Error(message = context.getString(R.string.network_error_dns))
        } else {
          NetworkResult.Error(message = context.getString(R.string.network_error_no_internet))
        }
      }
      is IOException -> NetworkResult.Error(message = context.getString(R.string.network_error_io))
      else -> NetworkResult.Error(message = context.getString(R.string.network_error_unknown))
    }
  }

  private fun parseErrorMessage(errorBody: String?): String? {
    return try {
      errorBody?.let {
        // 这里可以根据实际API错误格式解析
        it // 简化处理，实际项目需要解析JSON
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
      capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
  }

  companion object {
    fun handle(context: Context, throwable: Throwable): NetworkResult.Error {
      // 简化处理，实际项目应注入Context
      return NetworkErrorHandler(context).handle(throwable)
    }
  }
}