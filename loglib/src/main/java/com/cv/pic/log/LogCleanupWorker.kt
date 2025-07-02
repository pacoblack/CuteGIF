package com.cv.pic.log

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class LogCleanupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

  private val logFilePath by lazy {
    inputData.getString("log_file_path") ?: return@lazy File(context.filesDir, "app_logs.txt").absolutePath
  }

  override fun doWork(): Result {
    try {
      val logFile = File(logFilePath)
      if (!logFile.exists()) return Result.success()

      val lines = mutableListOf<String>()
      FileReader(logFile).use { reader ->
        BufferedReader(reader).use { bufferedReader ->
          var line: String?
          while (bufferedReader.readLine().also { line = it } != null) {
            lines.add(line!!)
          }
        }
      }

      if (lines.isEmpty()) return Result.success()

      val halfSize = lines.size / 2
      val newLines = lines.subList(halfSize, lines.size)

      BufferedWriter(FileWriter(logFile)).use { writer ->
        for (line in newLines) {
          writer.write(line)
          writer.newLine()
        }
      }

      return Result.success()
    } catch (e: Exception) {
      return Result.failure()
    }
  }
}