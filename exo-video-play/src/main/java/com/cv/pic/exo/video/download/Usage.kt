package com.cv.pic.exo.video.download

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.work.WorkManager
import com.cv.pic.exo.video.core.MyWorkManagerFactory

class Usage {
  @OptIn(UnstableApi::class)
  fun init(context:Context){
    WorkManager.initialize(context, MyWorkManagerFactory(context).workManagerConfiguration)

    MediaDownloadWorker.addRequest(context, "url")

  }

}