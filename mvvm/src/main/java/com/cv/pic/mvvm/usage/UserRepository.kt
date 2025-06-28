package com.cv.pic.mvvm.usage

import com.cv.pic.mvvm.core.ApiResult
import com.cv.pic.mvvm.core.ApiService
import com.cv.pic.mvvm.core.NetworkExecutor
import javax.inject.Inject

// UserRepository.kt
class UserRepository @Inject constructor(
  private val networkExecutor: NetworkExecutor,
  private val apiService: ApiService
) {
  // 使用类方式获取用户
  suspend fun getUser(userId: String): ApiResult<User> {
    return networkExecutor.executeClassRequest(
      endpoint = "users/$userId",
      responseType = User::class.java
    )
  }

  // 使用接口方式获取用户列表
  suspend fun getUsers(): ApiResult<List<User>> {
    val userService = apiService.createService(UserService::class.java)
    return networkExecutor.executeInterfaceRequest {
      userService.getUsers()
    }
  }

  // 更新用户信息（使用类方式）
  suspend fun updateUser(user: User): ApiResult<User> {
    return networkExecutor.executeClassRequest(
      endpoint = "users/${user.id}",
      method = "PUT",
      body = user,
      responseType = User::class.java
    )
  }
}