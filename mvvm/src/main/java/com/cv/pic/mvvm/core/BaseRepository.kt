package com.cv.pic.mvvm.core

abstract class BaseRepository(protected val baseUrl: String) {

  protected inline fun <reified T : ApiService> createService(): T {
    return RetrofitFactory.createService(T::class.java, baseUrl)
  }

}