package com.cv.pic.exo.video

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.regex.Pattern

object HlsParser {
  private val SEGMENT_PATTERN: Pattern = Pattern.compile("#EXTINF:[\\d\\.]+,\\s*\\n(\\S+)")
  private val PLAYLIST_PATTERN: Pattern = Pattern.compile(".*\\.m3u8.*")
  private val BYTERANGE_PATTERN: Pattern = Pattern.compile("BYTERANGE:(\\d+)@(\\d+)")

  @Throws(IOException::class)
  fun parse(url: String): SegmentInfo {
    var url = url
    var playlistContent = fetchPlaylistContent(url)


    // 检查是否是主播放列表
    if (isMasterPlaylist(playlistContent)) {
      val mediaPlaylistUrl = extractMediaPlaylistUrl(playlistContent, url)
      if (mediaPlaylistUrl != null) {
        playlistContent = fetchPlaylistContent(mediaPlaylistUrl)
        url = mediaPlaylistUrl // 更新URL为实际媒体播放列表
      }
    }

    val segmentUrls = extractSegmentUrls(playlistContent, url)
    return SegmentInfo(segmentUrls.size, url)
  }

  private fun isMasterPlaylist(content: String): Boolean {
    return content.contains("#EXT-X-STREAM-INF")
  }

  private fun extractMediaPlaylistUrl(masterContent: String, baseUrl: String): String? {
    // 取最高码率的媒体播放列表
    val lines = masterContent.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var bestUrl: String? = null
    var bestBandwidth = 0

    for (i in lines.indices) {
      if (lines[i].startsWith("#EXT-X-STREAM-INF")) {
        val bandwidth = extractAttribute(lines[i], "BANDWIDTH")
        if (bandwidth != null) {
          val bw = bandwidth.toInt()
          if (bw > bestBandwidth && i + 1 < lines.size) {
            val segmentUrl = lines[i + 1].trim { it <= ' ' }
            if (!segmentUrl.startsWith("#")) {
              bestBandwidth = bw
              bestUrl = resolveRelativeUrl(baseUrl, segmentUrl)
            }
          }
        }
      }
    }
    return bestUrl
  }

  private fun extractAttribute(line: String, attr: String): String? {
    val pattern = Pattern.compile("$attr=(\\d+)")
    val matcher = pattern.matcher(line)
    if (matcher.find()) {
      return matcher.group(1)
    }
    return null
  }

  private fun extractSegmentUrls(playlistContent: String, baseUrl: String): List<String> {
    val urls: MutableList<String> = ArrayList()
    val matcher = SEGMENT_PATTERN.matcher(playlistContent)
    while (matcher.find()) {
      val segmentPath = matcher.group(1).trim { it <= ' ' }
      urls.add(resolveRelativeUrl(baseUrl, segmentPath))
    }
    return urls
  }

  private fun resolveRelativeUrl(baseUrl: String, relativePath: String): String {
    if (relativePath.startsWith("http")) return relativePath

    val lastSlash = baseUrl.lastIndexOf('/')
    if (lastSlash != -1) {
      return baseUrl.substring(0, lastSlash + 1) + relativePath
    }
    return "$baseUrl/$relativePath"
  }

  @Throws(IOException::class)
  private fun fetchPlaylistContent(url: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url(url)
      .header("User-Agent", "Mozilla/5.0")
      .build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) throw IOException("Unexpected code $response")
      return response.body!!.string()
    }
  }

  class SegmentInfo(val segmentCount: Int, val playlistUrl: String)
}