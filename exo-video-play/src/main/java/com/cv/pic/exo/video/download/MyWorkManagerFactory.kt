package com.cv.pic.exo.video.download

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class MyWorkManagerFactory(private val context: Context) : Configuration.Provider {

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
        .setWorkerFactory(DownloadWorkerFactory(context))
        .build()
}

class DownloadWorkerFactory(private val context: Context) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker {
    return MediaDownloadWorker(context, workerParameters)
  }
}