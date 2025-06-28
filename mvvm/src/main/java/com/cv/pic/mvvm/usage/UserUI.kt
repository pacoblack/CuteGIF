package com.cv.pic.mvvm.usage

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cv.pic.mvvm.core.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserUI: AppCompatActivity() {
  private val viewModel: UserViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_user)

    // 观察用户状态
    lifecycleScope.launch {
      viewModel.userState.collect { result ->
        when (result) {
          is ApiResult.Loading -> showLoading()
          is ApiResult.Success -> showUser(result.data)
          is ApiResult.Error -> showError(result.message)
          ApiResult.Empty -> TODO()
        }
      }
    }

    // 观察用户列表状态
    lifecycleScope.launch {
      viewModel.usersState.collect { result ->
        when (result) {
          is ApiResult.Loading -> showLoading()
          is ApiResult.Success -> showUsers(result.data)
          is ApiResult.Error -> showError(result.message)
          ApiResult.Empty -> TODO()
        }
      }
    }

    // 加载用户数据
    viewModel.loadUser("123")
    viewModel.loadUsers()
  }

  private fun showLoading() {
    // 显示加载指示器
  }

  private fun showUser(user: User) {
    // 显示用户信息
  }

  private fun showUsers(users: List<User>) {
    // 显示用户列表
  }

  private fun showError(message: String?) {
    // 显示错误信息
  }
}