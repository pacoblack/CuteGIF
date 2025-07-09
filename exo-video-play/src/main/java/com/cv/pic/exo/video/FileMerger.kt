package com.cv.pic.exo.video

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileMerger {
  fun mergeFiles(sourceFiles: List<File>, outputFile: File): Boolean {
    try {
      FileOutputStream(outputFile).use { fos ->
        BufferedOutputStream(fos).use { bos ->
          val buffer = ByteArray(1024 * 8)
          for (sourceFile in sourceFiles) {
            FileInputStream(sourceFile).use { fis ->
              BufferedInputStream(fis).use { bis ->
                var bytesRead: Int
                while ((bis.read(buffer).also { bytesRead = it }) != -1) {
                  bos.write(buffer, 0, bytesRead)
                }
                bos.flush()
              }
            }
          }
          return true
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
      return false
    }
  }
}