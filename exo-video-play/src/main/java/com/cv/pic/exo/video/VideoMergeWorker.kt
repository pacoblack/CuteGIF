package com.cv.pic.exo.video

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cv.pic.exo.video.FileMerger.mergeFiles
import com.cv.pic.exo.video.VideoCacheManager.getCachedSegmentFiles
import java.io.File
import java.util.Collections

class VideoMergeWorker(
  context: Context,
  params: WorkerParameters
) : Worker(context, params) {
  override fun doWork(): Result {
    // 获取输入参数
    val cacheKeyPrefix = inputData.getString("cache_key_prefix")
    val totalSegments = inputData.getInt("total_segments", 0)
    val mergeUpTo = inputData.getInt("merge_up_to", totalSegments)

    if (totalSegments == 0 || cacheKeyPrefix == null) {
      return Result.failure()
    }


    // 获取已缓存的分片文件
    val segmentFiles = getCachedSegmentFiles(applicationContext)
    val filesToMerge: MutableList<File> = ArrayList()


    // 按分片索引排序
    Collections.sort(segmentFiles, java.util.Comparator { f1: File, f2: File ->
      val idx1 = extractSegmentIndex(f1.name)
      val idx2 = extractSegmentIndex(f2.name)
      idx1.compareTo(idx2)
    })


    // 选择要合并的分片
    for (file in segmentFiles) {
      val index = extractSegmentIndex(file.name)
      if (index in 0..<mergeUpTo) {
        filesToMerge.add(file)
      }
    }

    if (filesToMerge.isEmpty()) {
      return Result.failure()
    }


    // 创建输出文件
    val outputDir = File(applicationContext.filesDir, "merged_videos")
    if (!outputDir.exists()) outputDir.mkdirs()

    val fileName = "merged_" + filesToMerge.size + "of" + totalSegments + ".mp4"
    val outputFile = File(outputDir, fileName)

    // 执行合并
    val success = mergeFiles(filesToMerge, outputFile)


    // 可选：清理已合并的分片
    if (success) {
      for (segment in filesToMerge) {
        segment.delete()
      }
    }

    return if (success) Result.success() else Result.failure()
  }

  private fun extractSegmentIndex(fileName: String): Int {
    try {
      val start = fileName.lastIndexOf("segment_") + 8
      val end = fileName.indexOf(".", start)
      val indexStr = fileName.substring(start, end)
      return indexStr.toInt()
    } catch (e: Exception) {
      return -1
    }
  }
}