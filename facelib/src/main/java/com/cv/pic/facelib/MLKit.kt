package com.cv.pic.facelib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import androidx.camera.core.ImageProcessingUtil.convertYUVToBitmap

//TODO: 需要接入google ML kit
class MLKit {

  fun usage(context: Context, bitmap: Bitmap){
//    val image: Image = imageReader.acquireLatestImage()
//    val bitmap = convertYUVToBitmap(image)
//    val faces: Array<Face> = faceDetector.detect(bitmap) // 人脸检测
//    for (face in faces) {
//      val faceRect: Rect = face.getBoundingBox()
//      val faceROI: Bitmap = cropBitmap(bitmap, faceRect)
//      val emotions: FloatArray = tfliteClassifier.predict(faceROI) // 表情推理
//      val expressionId: Int = argmax(emotions)
//      renderMask(faceRect, expressionId) // 渲染对应脸谱
//    }
  }
}