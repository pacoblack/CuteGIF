package com.cv.pic.mvvm.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 扩展函数：简化LiveData观察
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
  observe(owner, object : Observer<T> {
    override fun onChanged(value: T) {
      observer.onChanged(value)
      removeObserver(this)
    }
  })
}

/**
 * 扩展函数：简化Disposable管理
 */
fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
  compositeDisposable.add(this)
}

/**
 * 扩展函数：处理API结果
 */
fun <T> LiveData<ApiResult<T>>.observeResult(
  owner: LifecycleOwner,
  onLoading: () -> Unit,
  onSuccess: (T) -> Unit,
  onError: (String?) -> Unit,
  onEmpty: () -> Unit = {}
) {
  this.observe(owner) { result ->
    when (result) {
      is ApiResult.Loading -> onLoading.invoke()
      is ApiResult.Success -> onSuccess.invoke(result.data)
      is ApiResult.Error -> onError.invoke(result.message)
      is ApiResult.Empty -> onEmpty.invoke()
    }
  }
}