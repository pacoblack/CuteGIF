package com.cv.pic.mvvm.usage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cv.pic.mvvm.core.ApiResult
import com.cv.pic.mvvm.core.BaseViewModel
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Job

class UserViewModel constructor(
  private val userRepo: UserRemoteDataSource
) : BaseViewModel() {

  private val _userState = MutableLiveData<ApiResult<User>>()
  val userState: LiveData<ApiResult<User>> = _userState

  fun fetchUser(userId: String) {
    userRepo.getUser(userId)
      .subscribe { result ->
        _userState.value = result
      }
      .addToDisposable()
  }

  private fun Disposable.addToDisposable() {
    viewModelScope.coroutineContext[Job]?.invokeOnCompletion {
      dispose()
    }
  }

  override fun handleError(throwable: Throwable) {
    TODO("Not yet implemented")
  }
}