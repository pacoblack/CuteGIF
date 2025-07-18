package com.cv.pic.exo.video.download

import android.Manifest
import android.app.Notification
import androidx.annotation.OptIn
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import com.cv.pic.exo.video.R
import com.cv.pic.exo.video.download.UnifiedMediaManager.Companion.getInstance


/**
 * 后台下载服务
 */
@OptIn(UnstableApi::class)
class MediaDownloadService : DownloadService(
  FOREGROUND_NOTIFICATION_ID,
  DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
  DOWNLOAD_CHANNEL_ID,
  R.string.download_channel_name,  // 字符串资源
  R.string.download_channel_description // 字符串资源
) {
  override fun getDownloadManager(): DownloadManager {
    return getInstance(this).downloadManager
  }

  @RequiresPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
  override fun getScheduler(): Scheduler {
    return PlatformScheduler(this, FOREGROUND_NOTIFICATION_ID)
  }

  override fun getForegroundNotification(
    downloads: List<Download>,
    notMetRequirements: Int
  ): Notification {
    return NotificationCompat.Builder(this, "download_channel")
      .setSmallIcon(R.drawable.ic_save)
      .setContentTitle("Downloading Media Files")
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setOngoing(true)
      .build()
  }

  companion object {
    private const val FOREGROUND_NOTIFICATION_ID = 1001
    private const val DOWNLOAD_CHANNEL_ID = "media_download_channel"
  }
}