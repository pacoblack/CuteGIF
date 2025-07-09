package com.cv.pic.exo.video

import java.util.regex.Pattern

object MediaTypeDetector {
  private val HLS_PATTERN: Pattern = Pattern.compile(".*\\.m3u8.*")
  private val MP4_PATTERN: Pattern = Pattern.compile(".*\\.mp4.*")

  fun isHls(url: String): Boolean {
    return HLS_PATTERN.matcher(url).matches()
  }

  fun isMp4(url: String): Boolean {
    return MP4_PATTERN.matcher(url).matches()
  }
}
