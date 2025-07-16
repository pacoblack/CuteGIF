package com.cv.pic.exo.video.core

import android.net.Uri

object UriExtensions {
  fun Uri.isMp4():Boolean{
    return this.path?.contains(".mp4") ?:false
  }

  fun Uri.isHls():Boolean{
    return this.path?.contains(".m3u8") ?:false
  }

  fun Uri.isMpd():Boolean{
    return this.path?.contains(".mpd") ?:false
  }
}