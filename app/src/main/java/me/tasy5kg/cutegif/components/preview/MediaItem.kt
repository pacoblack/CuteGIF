package me.tasy5kg.cutegif.components.preview

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

  fun isVideo(): Boolean {
    return type == TYPE_VIDEO
  }

  fun isImage(): Boolean {
    return type == TYPE_IMAGE || type == TYPE_MOTION_PHOTO || type == TYPE_GIF
  }
}