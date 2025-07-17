package com.cv.pic.exo.video

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheSpan
import java.nio.ByteBuffer

/**
 * MP4合并
 */
class MP4Merger {
  @UnstableApi
  private fun getCacheSpans(cache: Cache, mediaKey: String): List<CacheSpan> {
    return cache.getCachedSpans(mediaKey).toList()
  }

  @UnstableApi
  fun mergeCacheSpansToMp4(cache: Cache, mediaKey: String, outputFilePath: String) {
    val spans = getCacheSpans(cache, mediaKey)
    val files = spans.mapNotNull { span ->
      val file = span.file
      if (file?.exists() == true) file else null
    }

    if (files.isEmpty()) return

    val mediaMuxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    val extractor = MediaExtractor()

    try {
      // 添加第一个分片的轨道
      extractor.setDataSource(files[0].path)
      for (i in 0 until extractor.trackCount) {
        val format = extractor.getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME)
        if (mime?.startsWith("video/") == true || mime?.startsWith("audio/") == true) {
          mediaMuxer.addTrack(format)
          extractor.selectTrack(i)
          break
        }
      }
      mediaMuxer.start()

      // 合并所有分片
      for (file in files) {
        extractor.setDataSource(file.path)
        val buffer = ByteBuffer.allocate(512 * 1024)
        val bufferInfo = MediaCodec.BufferInfo()

        while (true) {
          val sampleSize = extractor.readSampleData(buffer, 0)
          if (sampleSize < 0) break
          bufferInfo.size = sampleSize
          bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
          bufferInfo.presentationTimeUs = extractor.sampleTime
          mediaMuxer.writeSampleData(0, buffer, bufferInfo)
          extractor.advance()
        }
      }

      mediaMuxer.stop()
      mediaMuxer.release()
      Log.d("Merge", "Merged video saved to $outputFilePath")
    } catch (e: Exception) {
      Log.e("Merge", "Merge failed", e)
    } finally {
      extractor.release()
      mediaMuxer.release()
    }
  }
}