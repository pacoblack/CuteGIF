package com.cv.pic.exo.video.download

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DefaultDownloadIndex
import androidx.media3.exoplayer.offline.DefaultDownloaderFactory
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.offline.DownloaderFactory
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.cv.pic.exo.video.R
import com.cv.pic.exo.video.core.VideoCacheManager
import com.cv.pic.exo.video.core.VideoDataSourceFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * 统一媒体管理模块：支持在线播放与离线下载
 */
@UnstableApi
class UnifiedMediaManager private constructor(context: Context) {
  // 核心组件
  private val context: Context = context.applicationContext
  // 创建缓存目录（共享用于播放和下载）
  private val cache: Cache = VideoCacheManager.getCache(context)
  val downloadManager: DownloadManager
  private val downloaderFactory: DownloaderFactory

  // 创建数据源工厂
  private val cacheDataSourceFactory: CacheDataSource.Factory = VideoDataSourceFactory.buildCacheSourceFactory(context, cache)

  // 下载状态监听器
  private val downloadListeners: MutableMap<String?, DownloadListener?> = HashMap<String?, DownloadListener?>()

  // 线程池
  private val executor: Executor = Executors.newFixedThreadPool(4)

  /**
   * 初始化媒体管理模块
   */
  init {

    // 创建下载器工厂
    downloaderFactory = DefaultDownloaderFactory(
      cacheDataSourceFactory,
      executor
    )

    // 创建下载管理器
    downloadManager = DownloadManager(
      context,
      DefaultDownloadIndex(StandaloneDatabaseProvider(context)),
      downloaderFactory
    )

    // 配置下载管理器
    downloadManager.setMaxParallelDownloads(3)

    // 注册下载状态监听器
    downloadManager.addListener(DownloadManagerListener())
  }

  /**
   * 创建支持缓存的播放器
   */
  fun createPlayer(): ExoPlayer {
    // 创建缓存数据源工厂
    val cacheDataSourceFactory = CacheDataSource.Factory()
      .setCache(cache)
      .setUpstreamDataSourceFactory(cacheDataSourceFactory)
      .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)


    // 创建播放器
    return ExoPlayer.Builder(context)
      .setMediaSourceFactory(
        DefaultMediaSourceFactory(context)
          .setDataSourceFactory(cacheDataSourceFactory)
      )
      .build()
  }

  /**
   * 播放媒体内容（自动使用缓存/离线内容）
   */
  fun playMedia(player: ExoPlayer, mediaItem: MediaItem) {
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
  }

  /**
   * 开始下载媒体内容
   */
  fun downloadMedia(mediaItem: MediaItem, listener: DownloadListener?) {
    var mediaId = mediaItem.mediaId
    if (mediaId.isEmpty()) {
      mediaId = if (mediaItem.mediaMetadata.title != null)
        mediaItem.mediaMetadata.title.toString()
      else System.currentTimeMillis().toString()
    }

    if (mediaItem.requestMetadata.mediaUri == null) {
      return
    }
    // 创建下载请求
    val request: DownloadRequest = DownloadRequest.Builder(mediaId, mediaItem.requestMetadata.mediaUri!!)
      .setCustomCacheKey(mediaId) // 使用唯一缓存键
      .build()


    // 注册监听器
    if (listener != null) {
      downloadListeners.put(mediaId, listener)
    }


    // 添加下载任务
    downloadManager.addDownload(request)
  }

  /**
   * 暂停下载
   */
  fun pauseDownload(mediaId: String?) {
    downloadManager.pauseDownloads()
  }

  /**
   * 恢复下载
   */
  fun resumeDownload(mediaId: String?) {
    downloadManager.resumeDownloads()
  }

  /**
   * 删除下载内容
   */
  fun removeDownload(mediaId: String) {
    downloadManager.removeDownload(mediaId)
    cache.removeResource(mediaId)
    downloadListeners.remove(mediaId)
  }

  /**
   * 获取下载状态
   */
  fun getDownloadState(mediaId: String): Int {
    val download = downloadManager.downloadIndex.getDownload(mediaId)
    return download?.state ?: Download.STATE_STOPPED
  }

  /**
   * 检查内容是否已下载
   */
  fun isDownloaded(mediaId: String): Boolean {
    val download = downloadManager.downloadIndex.getDownload(mediaId)
    return download != null && download.state == Download.STATE_COMPLETED
  }

  /**
   * 下载状态监听器接口
   */
  interface DownloadListener {
    fun onDownloadProgress(mediaId: String?, progress: Float)
    fun onDownloadCompleted(mediaId: String?)
    fun onDownloadFailed(mediaId: String?, exception: Exception?)
    fun onDownloadPaused(mediaId: String?)
  }

  /**
   * 下载管理器监听器实现
   */
  private inner class DownloadManagerListener : DownloadManager.Listener {
    override fun onDownloadChanged(
      downloadManager: DownloadManager,
      download: Download,
      finalException: Exception?
    ) {
      val mediaId = download.request.id
      val listener = downloadListeners.get(mediaId)

      if (listener == null) return

      when (download.state) {
        Download.STATE_DOWNLOADING -> {
          val progress = download.percentDownloaded
          if (progress != C.PERCENTAGE_UNSET.toFloat()) {
            listener.onDownloadProgress(mediaId, progress)
          }
        }

        Download.STATE_COMPLETED -> listener.onDownloadCompleted(mediaId)
        Download.STATE_FAILED -> listener.onDownloadFailed(mediaId, finalException)
        Download.STATE_QUEUED, Download.STATE_RESTARTING -> {}
        Download.STATE_STOPPED -> listener.onDownloadPaused(mediaId)
        Download.STATE_REMOVING -> {}
      }
    }
  }



  companion object {
    // 单例实例
    private var instance: UnifiedMediaManager? = null

    /**
     * 获取单例实例
     */
    @Synchronized
    fun getInstance(context: Context): UnifiedMediaManager {
      if (instance == null) {
        instance = UnifiedMediaManager(context)
      }
      return instance!!
    }
  }
}