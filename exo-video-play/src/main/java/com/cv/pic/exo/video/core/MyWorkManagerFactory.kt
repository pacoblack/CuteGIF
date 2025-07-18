package com.cv.pic.exo.video.core

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.cv.pic.exo.video.download.MediaDownloadWorker
import com.cv.pic.exo.video.work.HlsMergeWorker
import java.util.concurrent.Executors

class MyWorkManagerFactory(private val context: Context) : Configuration.Provider {

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(android.util.Log.DEBUG) // 设置日志级别
      .setExecutor(Executors.newFixedThreadPool(4))
      .setWorkerFactory(DownloadWorkerFactory(context))
      .setTaskExecutor(Executors.newScheduledThreadPool(2)) // 任务执行器
      .build()
}

class DownloadWorkerFactory(private val context: Context) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker? {
    return when (workerClassName) {
      MediaDownloadWorker::class.java.name -> {
        MediaDownloadWorker(context, workerParameters)
      }
      HlsMergeWorker::class.java.name -> {
        HlsMergeWorker(context, workerParameters)
      }
      else -> null
    }

  }
}