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
import androidx.media3.datasource.cache.CacheEvictor
import androidx.media3.datasource.cache.CacheSpan
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.cv.pic.exo.video.databinding.ActivityVideoPlayerBinding
import java.io.File

class VideoPlayerActivity : AppCompatActivity() {

  private val binding by lazy { ActivityVideoPlayerBinding.inflate(layoutInflater) }
  private val videoUri by lazy { intent.extras?.getString(EXTRA_VIDEO_URI)}

  private lateinit var playerView: PlayerView
  private lateinit var progressBar: ProgressBar
  private lateinit var player: ExoPlayer
  private lateinit var videoCache: Cache
  private var isFullscreen = true

  @OptIn(UnstableApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    playerView = binding.playerView
    progressBar = binding.progressBar
    playerView.setFullscreenButtonClickListener {toggleFullscreen(!isFullscreen)}
    toggleFullscreen(false)
    // 初始化缓存目录
    val cacheDir = getCacheDirectory(this)
    if (cacheDir != null) {
      // 确保缓存目录存在
      cacheDir.mkdirs()
      // 初始化缓存
      videoCache = SimpleCache(cacheDir, MyCacheEvictor())
    } else {
      finish()
    }
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

  override fun onDestroy() {
    super.onDestroy()
    releaseCache()
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
        val uri = videoUri?.toUri()
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

  private fun isExternalSdCard(file: File): Boolean {
    val path = file.absolutePath
    // 常见SD卡路径标识
    val patterns = arrayOf(
      "extSdCard", "sdcard1", "external_sd",
      "ext_sd", "external", "microSd"
    )
    return patterns.any { path.contains(it, ignoreCase = true) }
  }

  // 获取SD卡上的缓存目录
  private fun getCacheDirectory(context: Context): File? {
    // 尝试获取SD卡路径
    val externalDirs = context.getExternalFilesDirs(null)
    val sdCardDir = externalDirs.find {
      isExternalSdCard(it)
    }

    // 如果找到SD卡，使用SD卡路径，否则使用主存储
    val baseDir = sdCardDir ?: context.getExternalFilesDir(null)

    return baseDir?.let { File(it, "video/cache") }
  }

  // 自定义缓存驱逐器
  @UnstableApi
  private class MyCacheEvictor : CacheEvictor {
    override fun requiresCacheSpanTouches() = false
    override fun onCacheInitialized() = Unit
    override fun onStartFile(cache: Cache, key: String, position: Long, length: Long) = Unit

    override fun onSpanAdded(cache: Cache, span: CacheSpan) = Unit
    override fun onSpanRemoved(cache: Cache, span: CacheSpan) = Unit
    override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) = Unit

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