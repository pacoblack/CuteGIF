package com.cv.pic.ai.deepseek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cv.pic.mvvm.core.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeepSeekViewModel(
  private val repository: DeepSeekRepository
) : ViewModel() {
  private val _chatState = MutableStateFlow<NetworkResult<ChatResponse>>(NetworkResult.Loading)
  val chatState: StateFlow<NetworkResult<ChatResponse>> = _chatState

  fun login(email: String, password: String) {
    viewModelScope.launch(Dispatchers.IO) {
      _chatState.value = NetworkResult.Loading
      _chatState.value = repository.login(email, password)
    }
  }
}