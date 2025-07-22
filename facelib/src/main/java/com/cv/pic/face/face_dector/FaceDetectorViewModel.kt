package com.cv.pic.face.face_dector

import androidx.lifecycle.ViewModel

class FaceDetectorViewModel : ViewModel() {
  private var _delegate: Int = FaceDetectorHelper.DELEGATE_CPU
  private var _threshold: Float =
    FaceDetectorHelper.THRESHOLD_DEFAULT

  val currentDelegate: Int get() = _delegate
  val currentThreshold: Float get() = _threshold

  fun setDelegate(delegate: Int) {
    _delegate = delegate
  }

  fun setThreshold(threshold: Float) {
    _threshold = threshold
  }
}