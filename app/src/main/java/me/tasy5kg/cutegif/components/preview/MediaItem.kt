package me.tasy5kg.cutegif.components.preview

class MediaItem(val url: String, val title: String?, val type: Int) {
  companion object {
    const val TYPE_IMAGE: Int = 0
    const val TYPE_GIF: Int = 1
    const val TYPE_VIDEO: Int = 2
    const val TYPE_MOTION_PHOTO: Int = 3
  }
}