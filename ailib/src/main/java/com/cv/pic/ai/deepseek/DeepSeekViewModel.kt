package com.cv.pic.ai.deepseek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cv.pic.mvvm.core.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeepSeekViewModel @Inject constructor(
  private val repository: DeepSeekRepository
) : ViewModel() {
  private val _chatState = MutableStateFlow<ApiResult<ChatResponse>>(ApiResult.Loading)
  val chatState: StateFlow<ApiResult<ChatResponse>> = _chatState

  fun loadUsers() {
    viewModelScope.launch {
      _chatState.value = ApiResult.Loading
      _chatState.value = repository.chatCompletion()
    }
  }
}