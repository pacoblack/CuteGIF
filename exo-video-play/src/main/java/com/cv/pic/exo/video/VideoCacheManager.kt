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
  private const val MAX_CACHE_SIZE = (200 * 1024 * 1024 // 200MB
    ).toLong()
  private const val CACHE_DIR = "video_cache"

  // 分片追踪器
  private val segmentCacheStatus = SparseArray<String>()
  private var totalSegments = 0
  private var cacheKeyPrefix = ""
  private val SEGMENT_INDEX_PATTERN: Pattern = Pattern.compile(".*segment_(\\d+)\\..*")

  val cachedSegmentIndices: List<Int>
    // 获取已缓存的分片索引
    get() {
      val indices: MutableList<Int> = ArrayList()
      for (i in 0..<segmentCacheStatus.size) {
        val key = segmentCacheStatus.keyAt(i)
        if ("cached" == segmentCacheStatus[key]) {
          indices.add(key)
        }
      }
      indices.sort()
      return indices
    }

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

  // 初始化分片追踪
  fun initSegmentTracking(segments: Int, prefix: String) {
    totalSegments = segments
    cacheKeyPrefix = prefix
    segmentCacheStatus.clear()
    for (i in 0..<segments) {
      segmentCacheStatus.put(i, "pending")
    }
  }

  // 更新分片缓存状态
  @UnstableApi
  fun updateSegmentStatus(span: CacheSpan) {
    val key = span.key
    val matcher = SEGMENT_INDEX_PATTERN.matcher(key)
    if (matcher.find() && key.startsWith(cacheKeyPrefix)) {
      try {
        val index = matcher.group(1).toInt()
        if (index < totalSegments) {
          segmentCacheStatus.put(index, "cached")
          Log.d("CacheManager", "Segment $index cached")
        }
      } catch (e: NumberFormatException) {
        Log.e("CacheManager", "Error parsing segment index", e)
      }
    }
  }

  // 获取缓存文件列表
  fun getCachedSegmentFiles(context: Context): List<File> {
    val files: MutableList<File> = ArrayList()
    val cacheDir = File(context.cacheDir, CACHE_DIR)
    if (!cacheDir.exists()) return files

    for (file in cacheDir.listFiles()) {
      if (file.isFile && file.name.startsWith(cacheKeyPrefix)) {
        files.add(file)
      }
    }
    return files
  }
}