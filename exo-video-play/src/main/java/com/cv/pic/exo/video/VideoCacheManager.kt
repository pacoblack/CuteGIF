package com.cv.pic.exo.video

import android.content.Context
import android.util.Log
import android.util.SparseArray
import androidx.core.util.size
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheSpan
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File
import java.util.regex.Pattern

object VideoCacheManager {
  private var videoCache: Cache? = null
  private const val MAX_CACHE_SIZE = (200 * 1024 * 1024).toLong()  // 200MB
  private const val CACHE_DIR = "video_cache"

  @UnstableApi
  @Synchronized
  fun getCache(context: Context): Cache {
    if (videoCache == null) {
      val cacheDir = File(context.cacheDir, CACHE_DIR)
      if (!cacheDir.exists()) cacheDir.mkdirs()
      videoCache = SimpleCache(
        cacheDir,
        LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
      )
    }
    return videoCache!!
  }

}