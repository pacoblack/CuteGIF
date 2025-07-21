package com.cv.pic.exo.video

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.ContentMetadata
import com.cv.pic.exo.video.core.VideoDataSourceFactory.MyCacheKeyFactory.Companion.generateCacheKey
import com.cv.pic.exo.video.core.VideoDataSourceFactory.createCacheDataSource
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

/**
 * 缓存文件合并
 */
class MediaCacheMerger(private val context: Context) {

    /**
     * 合并缓存为完整视频
     *
     * @param cache 播放器使用的缓存实例
     * @param mediaUri 原始媒体URI
     * @param outputFile 输出文件
     * @return 是否合并成功
     */
    @UnstableApi
    fun mergeCacheToFile(
        cache: Cache,
        mediaUri: Uri,
        outputFile: File,
    ): Boolean {
        // 1. 生成缓存键（与播放器使用的相同）
        val cacheKey = generateCacheKey(mediaUri)

        // 2. 检查缓存是否完整
        if (!isCacheComplete(cache, cacheKey)) {
            return false
        }
        val merger = MP4Merger()
        merger.mergeCacheSpansToMp4(cache, cacheKey, outputFile.absolutePath)
        return true
    }

    /**
     * 检查缓存是否完整
     */
    @UnstableApi
    private fun isCacheComplete(cache: Cache, cacheKey: String): Boolean {
        val contentMetadata = cache.getContentMetadata(cacheKey)
        val contentLength = ContentMetadata.getContentLength(contentMetadata)

        if (contentLength == C.LENGTH_UNSET.toLong()) return false

        val cachedBytes = cache.getCachedBytes(cacheKey, 0, contentLength)
        return cachedBytes == contentLength
    }

    /**
     * 合并HLS缓存（M3U8 + TS分片）
     */
    @UnstableApi
    fun mergeHlsCache(
        cache: Cache,
        masterPlaylistUri: Uri,
        outputFile: File,
    ): Boolean {
        // 1. 读取主播放列表
        val playlistContent = readCachedContent(cache, masterPlaylistUri) ?: return false

        // 2. 解析TS片段列表
        val basePath = masterPlaylistUri.toString().substringBeforeLast('/') + "/"
        val segmentUrls = parseM3u8Playlist(playlistContent, basePath)

        // 3. 合并所有TS片段
        FileOutputStream(outputFile).use { output ->
            for (segmentUrl in segmentUrls) {
                val segmentUri = segmentUrl.toUri()
                if (!mergeCacheSegment(cache, segmentUri, output)) {
                    return false
                }
            }
        }
        return true
    }

  /**
   * 读取缓存内容为字符串
   */
  @UnstableApi
  private fun readCachedContent1(cache: Cache, uri: Uri): String? {
    val cacheKey = generateCacheKey(uri)
    val stringBuilder = StringBuilder()
    return try {
      val spans = cache.getCachedSpans( cacheKey)
      val files = spans.mapNotNull { span ->
        val file = span.file
        if (file?.exists() == true) file else null
      }

      files.map { file->
        FileReader(file).use { reader ->
          BufferedReader(reader).use { bufferedReader ->
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
              stringBuilder.append(line!!)
            }
          }
        }
      }
      stringBuilder.toString()
    } catch (e: Exception) {
      stringBuilder.toString()
    } finally {
      stringBuilder.toString()
    }
  }

    /**
     * 读取缓存内容为字符串
     */
    @UnstableApi
    private fun readCachedContent(cache: Cache, uri: Uri): String? {
        val cacheKey = generateCacheKey(uri)
        val cacheDataSource = createCacheDataSource(context, cache)

        return try {
            val dataSpec = DataSpec.Builder()
                .setUri(uri)
                .setKey(cacheKey)
                .build()

            cacheDataSource.open(dataSpec)

            // 读取内容到字节数组
            val length = cacheDataSource.responseHeaders["Content-Length"]?.firstOrNull()?.toIntOrNull() ?: 0
            if (length <= 0) return null

            val buffer = ByteArray(length)
            var totalRead = 0

            while (length > totalRead) {
                val bytesRead = cacheDataSource.read(buffer, totalRead, length - totalRead)
                if (bytesRead == C.RESULT_END_OF_INPUT) break
                totalRead += bytesRead
            }

            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        } finally {
            cacheDataSource.close()
        }
    }

    /**
     * 解析M3U8播放列表
     */
    private fun parseM3u8Playlist(content: String, basePath: String): List<String> {
        return content.lines()
            .filter { !it.startsWith("#") && it.isNotBlank() }
            .map { if (it.startsWith("http")) it else basePath + it }
    }

    /**
     * 合并单个缓存片段
     */
    @UnstableApi
    private fun mergeCacheSegment(cache: Cache, segmentUri: Uri, output: FileOutputStream): Boolean {
        val cacheKey = generateCacheKey(segmentUri)
        val cacheDataSource = createCacheDataSource(context, cache)

        return try {
            val dataSpec = DataSpec.Builder()
                .setUri(segmentUri)
                .setKey(cacheKey)
                .build()

            cacheDataSource.open(dataSpec)

            val buffer = ByteArray(64 * 1024)
            while (true) {
                val bytesRead = cacheDataSource.read(buffer, 0, buffer.size)
                if (bytesRead == C.RESULT_END_OF_INPUT) break
                output.write(buffer, 0, bytesRead)
            }
            true
        } catch (e: Exception) {
            false
        } finally {
            cacheDataSource.close()
        }
    }
}