package com.cv.pic.mvvm.usage

import com.cv.pic.mvvm.core.ApiResult
import com.cv.pic.mvvm.core.BaseRemoteDataSource
import io.reactivex.rxjava3.core.Observable

class UserRemoteDataSource constructor(
  private val service: UserService
) : BaseRemoteDataSource() {

  fun getUser(userId: String): Observable<ApiResult<User>> {
    return executeRequest { service.getUser(userId) }
  }
}