package com.cv.pic.exo.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.cv.pic.exo.video.MediaTypeDetector.isHls
import com.cv.pic.exo.video.MediaTypeDetector.isMp4
import com.cv.pic.exo.video.VideoCacheManager.initSegmentTracking
import com.cv.pic.exo.video.databinding.ActivityVideoPlayerBinding
import java.io.IOException
import java.util.concurrent.Executors

class VideoPlayerActivity : AppCompatActivity() {

  private lateinit var segmentTracker: SegmentTracker
  private val binding by lazy { ActivityVideoPlayerBinding.inflate(layoutInflater) }
  private val videoUrl by lazy { intent.extras?.getString(EXTRA_VIDEO_URI)}

  private lateinit var playerView: PlayerView
  private lateinit var progressBar: ProgressBar
  private lateinit var player: ExoPlayer
  private lateinit var videoCache: Cache
  private var isFullscreen = true

  private var totalSegments = 1
  private var cacheKeyPrefix:String? = ""
  private val executor = Executors.newSingleThreadExecutor()

  @OptIn(UnstableApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    playerView = binding.playerView
    progressBar = binding.progressBar
    playerView.setFullscreenButtonClickListener {toggleFullscreen(!isFullscreen)}
    toggleFullscreen(false)
    // 初始化缓存目录
    videoCache = VideoCacheManager.getCache(this)
    segmentTracker = SegmentTracker()
    videoUrl?.let { videoCache.addListener(it, segmentTracker) }

    // 检测媒体类型并获取分片信息
    if (videoUrl.isNullOrEmpty()) {
      Toast.makeText(this, "Url is empty", Toast.LENGTH_SHORT).show()
      finish()
    } else if (isHls(videoUrl!!)) {
      loadHlsVideo(videoUrl!!)
    } else if (isMp4(videoUrl!!)) {
      loadMp4Video(videoUrl!!)
    } else {
      Toast.makeText(this, "Unsupported video format", Toast.LENGTH_SHORT).show()
      finish()
    }
  }

   private fun showCachedSegments() {
     val cachedIndices = SegmentTracker.downloadedSegmentIndices;
     val sb = StringBuilder("Cached segments: ")
     cachedIndices.forEachIndexed { index, _ ->
       if (index > 0) sb.append(", ")
       sb.append(cachedIndices[index])
     }
     Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show()
   }

  private fun toggleFullscreen(value:Boolean) {
    isFullscreen = value

    if (isFullscreen) {
      enterFullscreen()
    } else {
      exitFullscreen()
    }
  }

  private fun enterFullscreen() {
    // 1. 隐藏系统UI
    hideSystemUI()

    // 2. 锁定横屏
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
  }

  private fun exitFullscreen() {
    // 1. 显示系统UI
    showSystemUI()

    // 2. 解锁屏幕方向
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }

  private fun hideSystemUI() {
    window.decorView.systemUiVisibility = (
      View.SYSTEM_UI_FLAG_FULLSCREEN
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      )
  }

  private fun showSystemUI() {
    window.decorView.systemUiVisibility = (
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      )
  }

  override fun onStart() {
    super.onStart()
    initializePlayer()
  }

  override fun onStop() {
    super.onStop()
    releasePlayer()
  }

  @OptIn(UnstableApi::class)
  override fun onDestroy() {
    super.onDestroy()
    releaseCache()
    videoCache.removeListener(videoUrl!!, segmentTracker);
    executor.shutdown();
  }
  public fun mergeAllCached(view:View) {
      val cachedIndices = SegmentTracker.downloadedSegmentIndices;
      if (cachedIndices.isNotEmpty()) {
          startMergeWork(cachedIndices[cachedIndices.size - 1] + 1);
      } else {
          Toast.makeText(this, "No segments cached yet", Toast.LENGTH_SHORT).show();
      }
  }
  private fun startMergeWork(mergeUpTo:Int) {
      if (totalSegments <= 0) {
          Toast.makeText(this, "Segment count not available", Toast.LENGTH_SHORT).show();
          return;
      }

      val inputData = Data.Builder()
          .putString("cache_key_prefix", cacheKeyPrefix)
          .putInt("total_segments", totalSegments)
          .putInt("merge_up_to", mergeUpTo.coerceAtMost(totalSegments))
          .build();

      val mergeRequest = OneTimeWorkRequest.Builder(VideoMergeWorker::class.java)
          .setInputData(inputData)
          .build()

      WorkManager.getInstance(this).enqueue(mergeRequest)
      Toast.makeText(this, "Merge started for first $mergeUpTo segments", Toast.LENGTH_SHORT).show();
  }

  private fun loadHlsVideo(videoUrl:String) {
        executor.execute {
          try {
            val segmentInfo = HlsParser.parse(videoUrl);
            totalSegments = segmentInfo.segmentCount;
            cacheKeyPrefix = segmentInfo.playlistUrl.toUri().lastPathSegment;

            runOnUiThread {
              initSegmentTracking(totalSegments, cacheKeyPrefix ?: "");
              initializePlayer();
              Toast.makeText(this, "Total segments: $totalSegments", Toast.LENGTH_SHORT).show();
            };
          } catch (e: IOException) {
            runOnUiThread {
              Toast.makeText(this, "Error loading HLS playlist", Toast.LENGTH_SHORT).show();
              initializePlayer() // 尝试继续播放
            };
          }
        };
    }

  private fun loadMp4Video(videoUrl: String) {
    totalSegments = 1
    cacheKeyPrefix = videoUrl.toUri().lastPathSegment!!
    initSegmentTracking(totalSegments, cacheKeyPrefix?:"")
    initializePlayer()
  }

  @OptIn(UnstableApi::class)
  private fun initializePlayer() {
    // 创建播放器
    player = ExoPlayer.Builder(this)
      .setSeekBackIncrementMs(5000)
      .setSeekForwardIncrementMs(5000)
      .build()
      .also { exoPlayer ->
        playerView.player = exoPlayer

        // 设置监听器
        exoPlayer.addListener(object : Player.Listener {
          override fun onPlaybackStateChanged(state: Int) {
            when (state) {
              Player.STATE_BUFFERING -> showProgress(true)
              Player.STATE_READY -> showProgress(false)
              Player.STATE_ENDED -> Toast.makeText(
                this@VideoPlayerActivity,
                "播放完成",
                Toast.LENGTH_SHORT
              ).show()
              Player.STATE_IDLE -> {
                showProgress(false)
              }
            }
          }
        })

        // 准备媒体源
        val uri = videoUrl?.toUri()
        if (uri == null) {
          Toast.makeText(this, "Uri参数问题", Toast.LENGTH_LONG).show()
          finish()
          return
        } else {
          val mediaSource =  buildMediaSource(uri)
          exoPlayer.setMediaSource(mediaSource)
          exoPlayer.prepare()
          exoPlayer.playWhenReady = true
        }

      }
  }

  @OptIn(UnstableApi::class)
  private fun buildMediaSource(uri: Uri): MediaSource {
    // 创建基础数据源工厂
    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

    // 创建支持缓存的数据源工厂
    val cacheDataSourceFactory = CacheDataSource.Factory()
      .setCache(videoCache)
      .setUpstreamDataSourceFactory(dataSourceFactory)
      .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    // 根据文件类型创建对应的媒体源
    val path = uri.path ?: ""
    return when {
      path.contains(".m3u8") -> HlsMediaSource.Factory(cacheDataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))
      path.contains(".mpd") -> DashMediaSource.Factory(cacheDataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))
      else -> ProgressiveMediaSource.Factory(cacheDataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))
    }
  }

  private fun showProgress(show: Boolean) {
    progressBar.visibility = if (show) View.VISIBLE else View.GONE
  }

  private fun releasePlayer() {
    player.release()
  }

  @OptIn(UnstableApi::class)
  private fun releaseCache() {
    videoCache.release()
  }

  companion object {
    const val EXTRA_VIDEO_URI ="extra_video_uri"
    fun start(context: Context, uri: String) {
      context.startActivity(
        Intent(
          context, VideoPlayerActivity::class.java
        ).putExtra(EXTRA_VIDEO_URI, uri)
      )
    }
  }
}