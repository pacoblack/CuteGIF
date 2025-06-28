package com.cv.pic.nativelib

class NativeLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun getWxAppIdFromJNI(): String

    external fun encryptString(input: String): ByteArray


  companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("nativelib")
        }

      // 打印字节数组为十六进制（用于生成C数组）
      fun printAsHexArray(data: ByteArray, arrayName: String?) {
        val sb = StringBuilder()
        sb.append("static const unsigned char ").append(arrayName).append("[] = {\n    ")

        for (i in data.indices) {
          sb.append(String.format("0x%02X", data[i].toInt() and 0xFF))
          if (i < data.size - 1) {
            sb.append(", ")
            if ((i + 1) % 8 == 0) sb.append("\n    ")
          }
        }

        sb.append("\n};")
        println(sb.toString())
      }
    }
}