package com.cv.pic.log

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class LogWriteWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {

  private val logFilePath by lazy {
    inputData.getString("log_file_path") ?: return@lazy File(context.filesDir, "app_logs.txt").absolutePath
  }

  override fun doWork(): Result {
    try {
      val logFile = File(logFilePath)
      val message = inputData.getString("log_message") ?: return Result.failure()

      BufferedWriter(FileWriter(logFile, true)).use { writer ->
        writer.write(message)
      }

      // 检查文件大小
      if (logFile.length() > 1024 * 1024) {
        enqueueLogCleanup(context, logFile)
      }

      return Result.success()
    } catch (e: Exception) {
      return Result.failure()
    }
  }

  // ✅ 提交 WorkManager 清理任务
  private fun enqueueLogCleanup(context: Context, logFile: File) {
    val inputData = workDataOf("log_file_path" to logFile.absolutePath)
    val request = OneTimeWorkRequestBuilder<LogCleanupWorker>()
      .setInputData(inputData)
      .addTag("LOG_CLEANUP")
      .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
      "log_cleanup",
      ExistingWorkPolicy.REPLACE,
      request
    )
  }
}