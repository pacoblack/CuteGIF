package com.cv.pic.mvvm.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
  protected val compositeDisposable = CompositeDisposable()

  protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    handleError(throwable)
  }

  protected fun launchCoroutine(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(exceptionHandler, block = block)
  }

  abstract fun handleError(throwable: Throwable)

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.dispose()
  }
}