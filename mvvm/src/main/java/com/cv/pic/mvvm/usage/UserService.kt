package com.cv.pic.mvvm.usage

import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
  @GET("users")
  suspend fun getUsers(): List<User>

  @GET("users/{id}")
  suspend fun getUser(@Path("id") userId: String): User
}