package com.cv.pic.exo.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.cv.pic.exo.video.databinding.ActivityVideoPlayerBinding
import java.io.File
import java.util.concurrent.Executors

class VideoPlayerActivity : AppCompatActivity() {
  private val binding by lazy { ActivityVideoPlayerBinding.inflate(layoutInflater) }
  private val videoUrl by lazy { intent.extras?.getString(EXTRA_VIDEO_URI)}

  private lateinit var playerView: PlayerView
  private lateinit var progressBar: ProgressBar
  private lateinit var player: ExoPlayer
  private lateinit var videoCache: Cache
  private var isFullscreen = true
  private var isHlsVideo = false

  private var cacheKeyPrefix:String? = ""
  private val executor = Executors.newSingleThreadExecutor()

  @OptIn(UnstableApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      initSystemBar()
    }
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)

    playerView = binding.playerView
    progressBar = binding.progressBar
    playerView.setFullscreenButtonClickListener {toggleFullscreen(!isFullscreen)}
    toggleFullscreen(false)
    playerView.setControllerVisibilityListener(ControllerVisibilityListener { visibility ->
      if (View.VISIBLE == visibility) {
        if (!isFullscreen) {
          binding.toolbar.visibility = View.VISIBLE
        }
      } else {
        binding.toolbar.visibility = View.GONE
      }
    })

    // 初始化缓存目录
    videoCache = VideoCacheManager.getCache(this)

  }

  @RequiresApi(Build.VERSION_CODES.R)
  private fun initSystemBar(){
    // 获取 WindowInsetsController（需在 View 已附加到窗口后调用）
    val decorView = window.decorView;
    val controller = decorView.getWindowInsetsController();

    // 隐藏系统栏（状态栏 + 导航栏）
    controller?.hide(WindowInsets.Type.systemBars());

    // 启用沉浸式粘性模式（滑动边缘临时显示系统栏）
    controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE;

    // 设置状态栏图标深色模式（需浅色背景）
    controller?.setSystemBarsAppearance(
      WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
      WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
    );
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
    binding.appBar.visibility = View.GONE
    // 1. 隐藏系统UI
    hideSystemUI()

    // 2. 锁定横屏
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
  }

  private fun exitFullscreen() {
    binding.appBar.visibility = View.VISIBLE
    // 1. 显示系统UI
    showSystemUI()

    // 2. 解锁屏幕方向
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }

  private fun hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val controller = window.insetsController;
      controller?.hide(WindowInsets.Type.systemBars())
    } else {
      window.decorView.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_FULLSCREEN
          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
    }
  }

  private fun showSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val controller = window.insetsController
      controller?.show(WindowInsets.Type.systemBars())
    } else {
      window.decorView.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
    }
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
    executor.shutdown();
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
              Player.STATE_ENDED -> {
                Toast.makeText(this@VideoPlayerActivity, "播放完成", Toast.LENGTH_SHORT).show()
                saveVideoAfterPlayback()
              }
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
    var mediaItem = MediaItem.fromUri(uri)
    return when {
      path.contains(".m3u8") -> {
        isHlsVideo = true
        cacheKeyPrefix = "hls_" + uri.hashCode() + "_"
        HlsMediaSource.Factory(cacheDataSourceFactory)
          .createMediaSource(mediaItem)
      }
      path.contains(".mpd") -> {
        val cacheKey: String = cacheDataSourceFactory.cacheKeyFactory.buildCacheKey(DataSpec(uri))
        cacheKeyPrefix = cacheKey.toString()
        DashMediaSource.Factory(cacheDataSourceFactory)
          .createMediaSource(mediaItem)
      }
      else -> {
        val cacheKey: String = cacheDataSourceFactory.cacheKeyFactory.buildCacheKey(DataSpec(uri))
        cacheKeyPrefix = cacheKey.toString()
        ProgressiveMediaSource.Factory(cacheDataSourceFactory)
          .createMediaSource(mediaItem)
      }
    }
  }

  @OptIn(UnstableApi::class)
  @UnstableApi
  private fun saveVideoAfterPlayback() {
    val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
    val outputFile = File(outputDir, "merged_video.mp4");

    HlsMergeManager.startMerge(this, videoUrl, outputFile, "mp4");
    WorkManager.getInstance(this)
      .getWorkInfosForUniqueWorkLiveData("mergeWork")
      .observe(this, Observer { workInfos->
        if (workInfos != null && workInfos.isNotEmpty()) {
          val workInfo = workInfos[0];

          when (workInfo.state) {
            WorkInfo.State.SUCCEEDED->{
              Toast.makeText(this, "视频合并成功", Toast.LENGTH_SHORT).show();
              // 打开视频文件
            }
            WorkInfo.State.FAILED ->{
              Toast.makeText(this, "视频合并失败", Toast.LENGTH_SHORT).show();
            }
            else -> {}
          }
        }

      });
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

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.toolbar_video, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_video_info -> {}
      R.id.menu_video_save -> {}
    }
    return true
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