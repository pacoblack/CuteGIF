package com.cv.pic.mvvm.usage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cv.pic.mvvm.core.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
  private val repository: UserRepository
) : ViewModel() {

  private val _userState = MutableStateFlow<ApiResult<User>>(ApiResult.Loading)
  val userState: StateFlow<ApiResult<User>> = _userState

  private val _usersState = MutableStateFlow<ApiResult<List<User>>>(ApiResult.Loading)
  val usersState: StateFlow<ApiResult<List<User>>> = _usersState

  fun loadUser(userId: String) {
    viewModelScope.launch {
      _userState.value = ApiResult.Loading
      _userState.value = repository.getUser(userId)
    }
  }

  fun loadUsers() {
    viewModelScope.launch {
      _usersState.value = ApiResult.Loading
      _usersState.value = repository.getUsers()
    }
  }

  fun updateUser(user: User) {
    viewModelScope.launch {
      _userState.value = ApiResult.Loading
      _userState.value = repository.updateUser(user)
    }
  }
}