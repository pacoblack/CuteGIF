package com.cv.pic.face

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.facestylizer.FaceStylizer
import com.google.mediapipe.tasks.vision.facestylizer.FaceStylizer.FaceStylizerOptions
import com.google.mediapipe.tasks.vision.facestylizer.FaceStylizerResult

/**
 * face_stylizer_color_ink.task
 * face_stylizer_color_sketch.task
 * face_stylizer_oil_painting.task
 */
class FaceStylizationHelper(
  private val modelPosition: Int,
  private val context: Context,
  var faceStylizerListener: FaceStylizerListener? = null
) {

  private var faceStylizer: FaceStylizer? = null

  init {
    setupFaceStylizer()
  }

  private fun setupFaceStylizer() {
    val baseOptionsBuilder = BaseOptions.builder()
    // Sets the model selection.
    baseOptionsBuilder.setModelAssetPath(
      when (modelPosition) {
        0 -> MODEL_PATH_COLOR_SKETCH
        1 -> MODEL_PATH_COLOR_INK
        2 -> MODEL_PATH_OIL_PAINTING
        else -> throw Throwable("Invalid model type position")
      }
    )

    try {
      val baseOptions = baseOptionsBuilder.build()
      val optionsBuilder = FaceStylizerOptions.builder()
        .setBaseOptions(baseOptions)

      val options = optionsBuilder.build()
      faceStylizer = FaceStylizer.createFromOptions(context, options)
    } catch (e: IllegalStateException) {
      faceStylizerListener?.onError(
        "Face stylizer failed to initialize. See error logs for " +
          "details"
      )
      Log.e(
        TAG,
        "Face stylizer failed to load model with error: " + e.message
      )
    } catch (e: RuntimeException) {
      // This occurs if the model being used does not support GPU
      faceStylizerListener?.onError(
        "Face stylizer failed to initialize. See error logs for " +
          "details", GPU_ERROR
      )
      Log.e(
        TAG,
        "Face stylizer failed to load model with error: " + e.message
      )
    }
  }

  fun stylize(bitmap: Bitmap): ResultBundle {
    val mpImage = BitmapImageBuilder(bitmap).build()
    var timestampMs = System.currentTimeMillis()
    val result = faceStylizer?.stylize(mpImage)
    timestampMs = System.currentTimeMillis() - timestampMs

    return ResultBundle(result, timestampMs)
  }

  fun close() {
    faceStylizer?.close()
  }

  // Wraps results from inference, the time it takes for inference to be
  // performed.
  data class ResultBundle(
    val stylizedFace: FaceStylizerResult?,
    val inferenceTime: Long,
  )

  companion object {
    const val MODEL_PATH_OIL_PAINTING = "face_stylizer_oil_painting.task"
    const val MODEL_PATH_COLOR_INK = "face_stylizer_color_ink.task"
    const val MODEL_PATH_COLOR_SKETCH = "face_stylizer_color_sketch.task"
    const val OTHER_ERROR = 0
    const val GPU_ERROR = 1
    private const val TAG = "FaceStylizationHelper"
  }

  interface FaceStylizerListener {
    fun onError(error: String, errorCode: Int = OTHER_ERROR)
  }
}