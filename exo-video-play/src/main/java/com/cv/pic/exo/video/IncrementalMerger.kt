package com.cv.pic.exo.video

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.max

/**
 * m3u8视频增量合并
 */
class IncrementalMerger {
  private var muxer: MediaMuxer? = null
  private val trackMap: MutableMap<Int, Int> = HashMap()
  private var outputFile: File? = null
  private var maxPts: Long = 0

  @Throws(IOException::class)
  fun start(output: File) {
    this.outputFile = output
    this.muxer = MediaMuxer(
      output.absolutePath,
      MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
    )
  }

  @Throws(IOException::class)
  fun addSegment(segmentFile: File) {
    val extractor = MediaExtractor()
    extractor.setDataSource(segmentFile.absolutePath)


    // 如果是第一个分段，初始化轨道
    if (trackMap.isEmpty()) {
      for (i in 0..<extractor.trackCount) {
        val format = extractor.getTrackFormat(i)
        val trackIndex = muxer!!.addTrack(format)
        trackMap[i] = trackIndex
      }
      muxer!!.start()
    }


    // 写入数据
    for (i in 0..<extractor.trackCount) {
      extractor.selectTrack(i)
      val buffer = ByteBuffer.allocate(1024 * 1024)
      val bufferInfo = MediaCodec.BufferInfo()

      while (true) {
        val sampleSize = extractor.readSampleData(buffer, 0)
        if (sampleSize < 0) break

        val sampleTime = extractor.sampleTime + maxPts
        bufferInfo[0, sampleSize, sampleTime] = MediaCodec.BUFFER_FLAG_KEY_FRAME
        muxer!!.writeSampleData(trackMap[i]!!, buffer, bufferInfo)
        extractor.advance()
      }
    }


    // 更新最大时间戳
    for (i in 0..<extractor.trackCount) {
      extractor.selectTrack(i)
      while (extractor.advance()) {
        maxPts = max(maxPts, extractor.sampleTime)
      }
    }

    extractor.release()
  }

  fun finish() {
    if (muxer != null) {
      try {
        muxer!!.stop()
        muxer!!.release()
      } catch (e: Exception) {
        // 处理错误
      }
    }
  }
}