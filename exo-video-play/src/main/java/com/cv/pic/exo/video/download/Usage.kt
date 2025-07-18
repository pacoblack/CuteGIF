package com.cv.pic.exo.video.download

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class Usage {
  @OptIn(UnstableApi::class)
  fun init(context:Context){
    WorkManager.initialize(context, MyWorkManagerFactory(context).workManagerConfiguration)

    val requestData = workDataOf("media_id" to "params_mediaId", "media_uri" to "params_mediaUri")
    val downloadRequest = OneTimeWorkRequestBuilder<MediaDownloadWorker>()
      .setInputData(requestData)
      .setConstraints(
        Constraints.Builder()
          .setRequiredNetworkType(NetworkType.CONNECTED)
          .setRequiresBatteryNotLow(true)
          .build()
      )
      .build()

   WorkManager.getInstance(context).enqueue(downloadRequest)

  }

}