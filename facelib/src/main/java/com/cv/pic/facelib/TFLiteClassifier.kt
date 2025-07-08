package com.cv.pic.facelib

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class TFLiteClassifier(context: Context, modelPath: String) {
  private lateinit var tflite: Interpreter
  private lateinit var outputBuffer: TensorBuffer
  private lateinit var imageProcessor: ImageProcessor

  init {
    try {
      // 1. 加载模型
      val modelBuffer = loadModelFile(context, modelPath);
      val options = Interpreter.Options();

      // 2. 添加 GPU 委托（可选）
      if(isGpuDelegateSupported()) {
        val gpuDelegate = GpuDelegate()
        options.addDelegate(gpuDelegate);
      } else {
        options.setNumThreads(4); // 多线程推理
      }

      tflite = Interpreter(modelBuffer, options);

      // 3. 初始化输入/输出 Tensor
      val inputShape = tflite.getInputTensor(0).shape(); // [1, 48, 48, 1]
      val inputDataType = tflite.getInputTensor(0).dataType()
      val outputShape = tflite.getOutputTensor(0).shape(); // [1, 7]
      outputBuffer = TensorBuffer.createFixedSize(outputShape, inputDataType)

      // 4. 初始化图像处理器（归一化、裁剪等）
      imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(inputShape[1], inputShape[2], ResizeOp.ResizeMethod.BILINEAR))
      .add(NormalizeOp(0f, 255f)) // 归一化到 [0,1]
      .build();
    } catch (e:Exception) {  }
  }

  @Throws(IOException::class)
  private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelPath)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }

  // 推理方法
  fun predict(bitmap: Bitmap?): FloatArray {
    // 预处理
    var tensorImage = TensorImage(DataType.FLOAT32)
    tensorImage.load(bitmap)
    tensorImage = imageProcessor.process(tensorImage)

    // 推理
    tflite.run(tensorImage.buffer, outputBuffer.buffer)
    return outputBuffer.floatArray // 输出概率数组
  }

  fun isGpuDelegateSupported(): Boolean {
    val compatList = CompatibilityList()
    return compatList.isDelegateSupportedOnThisDevice
  }

}