package com.cv.pic.log

import timber.log.Timber

object LogRecorder {
  fun save(tag:String, content:String){
    Timber.tag(tag).d(content)
  }
}