package me.tasy5kg.cutegif.components.preview

import me.tasy5kg.cutegif.toolbox.Toolbox.logRed

class MediaItem(val url: String, val title: String?, val type: Int) {

  init {
//      logRed("####### type:$type", url)
  }

  companion object {
    const val TYPE_IMAGE: Int = 0
    const val TYPE_GIF: Int = 1
    const val TYPE_VIDEO: Int = 2
    const val TYPE_MOTION_PHOTO: Int = 3
    const val TYPE_UNKNOWN: Int = 4
  }

  fun mediaSelectString(type: Int):String{
    return when(type) {
      TYPE_IMAGE-> "image/*"
      TYPE_GIF -> "image/gif"
      TYPE_VIDEO-> "image/*"
      TYPE_MOTION_PHOTO-> "image/*"
      else -> "*/*"
    }
  }
}