package com.cv.pic.face

import androidx.lifecycle.ViewModel

/**
 *  This ViewModel is used to store image segmenter helper settings
 */
class ConfidenceMaskMainViewModel : ViewModel() {

  private var _delegate: Int = ConfidenceMaskImageSegmenterHelper.DELEGATE_CPU
  private var _model: Int = ConfidenceMaskImageSegmenterHelper.MODEL_DEEPLABV3

  val currentDelegate: Int get() = _delegate
  val currentModel: Int get() = _model

  fun setDelegate(delegate: Int) {
    _delegate = delegate
  }

  fun setModel(model: Int) {
    _model = model
  }
}