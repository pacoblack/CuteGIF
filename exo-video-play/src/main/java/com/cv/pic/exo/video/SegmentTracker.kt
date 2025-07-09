package com.cv.pic.exo.video

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheSpan
import com.cv.pic.exo.video.VideoCacheManager.cachedSegmentIndices
import com.cv.pic.exo.video.VideoCacheManager.updateSegmentStatus

class SegmentTracker : Cache.Listener {
  @UnstableApi
  override fun onSpanAdded(cache: Cache, span: CacheSpan) {
    updateSegmentStatus(span)
  }

  @UnstableApi
  override fun onSpanRemoved(cache: Cache, span: CacheSpan) {
    // 处理分片移除
  }

  @UnstableApi
  override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) {
    // 处理分片更新
  }

  companion object {
    val downloadedSegmentIndices: List<Int>
      get() = cachedSegmentIndices
  }
}