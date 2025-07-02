package com.cv.pic.mvvm.core

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Activity 扩展函数，简化 ViewModel 获取
 */
inline fun <reified VM : ViewModel> ComponentActivity.viewModelWithFactory(
  noinline create: () -> VM
): Lazy<VM> {
  return lazy {
    ViewModelProvider(
      this,
      ViewModelFactory(create)
    )[VM::class.java]
  }
}

/**
 * Fragment 扩展函数，简化 ViewModel 获取
 */
inline fun <reified VM : ViewModel> Fragment.viewModelWithFactory(
  noinline create: () -> VM
): Lazy<VM> {
  return lazy {
    ViewModelProvider(
      this,
      ViewModelFactory(create)
    )[VM::class.java]
  }
}