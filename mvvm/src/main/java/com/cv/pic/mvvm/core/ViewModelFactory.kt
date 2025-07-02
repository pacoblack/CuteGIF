package com.cv.pic.mvvm.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 通用 ViewModel 工厂，支持带参数的 ViewModel 创建
 *
 * @param create 创建 ViewModel 实例的函数
 */
class ViewModelFactory<T : ViewModel>(
  private val create: () -> T
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return create() as T
  }
}

/**
 * 支持多个 ViewModel 类型的增强工厂
 */
class MultiViewModelFactory(
  private val creators: Map<Class<out ViewModel>, () -> ViewModel>
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    val creator = creators[modelClass] ?: creators.entries.firstOrNull {
      modelClass.isAssignableFrom(it.key)
    }?.value ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")

    return creator() as T
  }
}