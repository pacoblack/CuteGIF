package com.cv.pic.exo.video.download

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.CompletableDeferred

class MediaDownloadWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params) {

  @UnstableApi
  override suspend fun doWork(): Result {
    val downloadManager = UnifiedMediaManager.getInstance(applicationContext)
    val mediaUri = inputData.getString(PARAMS_MEDIA_URI) ?: return Result.failure()
    val mediaId = mediaUri.hashCode().toString()
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

  companion object{
    const val PARAMS_MEDIA_URI= "media_uri"

    fun addRequest(context: Context, uri:String){
      val requestData = workDataOf(PARAMS_MEDIA_URI to uri)

      val downloadRequest = OneTimeWorkRequestBuilder<MediaDownloadWorker>() // PeriodicWorkRequest
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
}