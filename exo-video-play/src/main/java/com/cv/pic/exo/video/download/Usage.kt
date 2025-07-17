package com.cv.pic.exo.video.download

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.cv.pic.exo.video.download.UnifiedMediaManager.DownloadListener

class Usage {
  @OptIn(UnstableApi::class)
  fun init(context:Context){
    UnifiedMediaManager.getInstance(context)
  }

  @OptIn(UnstableApi::class)
  fun download(context: Context){
    val mediaManager = UnifiedMediaManager.getInstance(context);
    mediaManager.downloadMedia(MediaItem.Builder().build(), UseDownloadListener())
  }

  @UnstableApi
  class UseDownloadListener : DownloadListener {
    override fun onDownloadProgress(mediaId: String?, progress: Float) {
      TODO("Not yet implemented")
    }

    override fun onDownloadCompleted(mediaId: String?) {
      TODO("Not yet implemented")
    }

    override fun onDownloadFailed(mediaId: String?, exception: Exception?) {
      TODO("Not yet implemented")
    }

    override fun onDownloadPaused(mediaId: String?) {
      TODO("Not yet implemented")
    }

  }
}