package com.cv.pic.exo.video

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File
import java.util.Collections
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

  fun getCachedFilesForPrefix(context: Context, prefix: String): MutableList<File?> {
    val files: MutableList<File?> = ArrayList()
    val cacheDir = File(context.cacheDir, CACHE_DIR)

    if (cacheDir.exists() && cacheDir.isDirectory()) {
      val allFiles = cacheDir.listFiles()
      if (allFiles != null) {
        for (file in allFiles) {
          if (file.getName().startsWith(prefix)) {
            files.add(file)
          }
        }

        // 按文件名排序（确保分段顺序）
        Collections.sort<File?>(files, Comparator { f1: File?, f2: File? ->
          try {
            val idx1 = extractSegmentIndex(f1!!.getName())
            val idx2 = extractSegmentIndex(f2!!.getName())
            return@Comparator idx1.compareTo(idx2)
          } catch (e: Exception) {
            return@Comparator f1!!.getName().compareTo(f2!!.getName())
          }
        })
      }
    }
    return files
  }

  private fun extractSegmentIndex(fileName: String): Int {
    // 文件名格式：prefix_0.ts, prefix_1.ts, ...
    val pattern = Pattern.compile(".*_(\\d+)\\..*")
    val matcher = pattern.matcher(fileName)
    if (matcher.find()) {
      return matcher.group(1).toInt()
    }
    return 0
  }

}