package com.cv.pic.log

import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.io.File

object LogRecorder {
  private lateinit var inMemoryTree: InMemoryTree

  fun init(context: Context) {
    val logFile = File(context.filesDir, "app_logs.txt")
    inMemoryTree = InMemoryTree()
    Timber.plant(inMemoryTree, FileLoggingTree(context))
  }

  fun d(tag:String, message: String) = Timber.tag(tag).d(message)
  fun i(tag:String, message: String) = Timber.tag(tag).i(message)
  fun w(tag:String, message: String) = Timber.tag(tag).w(message)
  fun e(tag:String, message: String) = Timber.tag(tag).e(message)
  fun e(tag:String, throwable: Throwable, message: String) = Timber.tag(tag).e(throwable, message)

  fun showLogActivity(context: Context) {
    context.startActivity(Intent(context, LogViewerActivity::class.java))
  }

  fun clearInMemoryLogs() {
    inMemoryTree.clearLogs()
  }
}