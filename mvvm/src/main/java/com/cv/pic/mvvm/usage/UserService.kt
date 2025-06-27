package com.cv.pic.mvvm.usage

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
  @GET("users/{id}")
  fun getUser(@Path("id") userId: String): Observable<User>
}