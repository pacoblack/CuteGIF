package com.cv.pic.exo.video.download

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CompletableDeferred

class MediaDownloadWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params) {

  @UnstableApi
  override suspend fun doWork(): Result {
    val downloadManager = UnifiedMediaManager.getInstance(applicationContext)
    val mediaId = inputData.getString("media_id") ?: return Result.failure()
    val mediaUri = inputData.getString("media_uri") ?: return Result.failure()
    val deferred: CompletableDeferred<Result> = CompletableDeferred()
    try {
      downloadManager.downloadMedia(mediaId,  mediaUri.toUri(), object:UnifiedMediaManager.DownloadListener{
        override fun onDownloadProgress(mediaId: String?, progress: Float) {}

        override fun onDownloadCompleted(mediaId: String?) {
          deferred.complete(Result.success())
        }

        override fun onDownloadFailed(mediaId: String?, exception: Exception?) {
          deferred.complete(Result.failure())
        }

        override fun onDownloadPaused(mediaId: String?) {}

      }) // 提交下载
      return deferred.await()
    } catch (e: Exception) {
      return Result.retry() // 失败时重试
    }
  }
}