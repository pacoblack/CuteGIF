package com.cv.pic.log

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLoggingTree(private val context:Context) : Timber.Tree() {

  override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {

    val logMessage = if (throwable == null) message else "$message\n${throwable.stackTraceToString()}"
    val fullMessage = "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())} [$tag] $logMessage\n"

    enqueueLogWrite(context, fullMessage)
  }

  private fun enqueueLogWrite(context:Context, message: String) {
    val inputData = workDataOf("log_message" to message)
    val request = OneTimeWorkRequestBuilder<LogWriteWorker>()
      .setInputData(inputData)
      .addTag("LOG_WRITE")
      .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
      "log_write",
      ExistingWorkPolicy.REPLACE,
      request
    )
  }
}