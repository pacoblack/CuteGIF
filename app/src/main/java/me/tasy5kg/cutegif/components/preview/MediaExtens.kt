package me.tasy5kg.cutegif.components.preview

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import me.tasy5kg.cutegif.components.preview.MediaItem.Companion.TYPE_GIF
import me.tasy5kg.cutegif.components.preview.MediaItem.Companion.TYPE_IMAGE
import me.tasy5kg.cutegif.components.preview.MediaItem.Companion.TYPE_UNKNOWN
import me.tasy5kg.cutegif.components.preview.MediaItem.Companion.TYPE_VIDEO

object MediaExtensions {
  fun Uri.getMediaType(context: Context): Int{
    val type: String? = context.contentResolver.getType(this)

    if (type != null) {
      if (type.startsWith("image/")) {
        // 用户选择了图片或GIF
        return if ("image/gif" == type) {
          TYPE_GIF
        } else {
          TYPE_IMAGE
        }
      } else if (type.startsWith("video/")) {
        return TYPE_VIDEO
      }
    } else {
      // 如果MIME类型为空，则尝试读取文件扩展名
      val path: String? = getFilePathFromUri(context, this)
      if (path != null) {
        val extension = path.substring(path.lastIndexOf(".") + 1).lowercase()
        if ("gif" == extension) {
          return TYPE_GIF
        } else if (isImageExtension(extension)) {
          return TYPE_IMAGE
        } else if (isVideoExtension(extension)) {
          return TYPE_VIDEO
        }
      }
    }

    return TYPE_UNKNOWN
  }

  private fun isImageExtension(extension: String?): Boolean {
    return mutableListOf<String?>("jpg", "jpeg", "png", "bmp", "webp").contains(extension)
  }

  private fun isVideoExtension(extension: String?): Boolean {
    return mutableListOf<String?>("mp4", "mkv", "avi", "mov", "flv").contains(extension)
  }

  private fun getFilePathFromUri(context: Context, uri: Uri): String? {
    var filePath: String? = null
    if ("content".equals(uri.scheme, ignoreCase = true)) {
      context.contentResolver.query(uri, arrayOf<String>(MediaStore.Images.ImageColumns.DATA), null, null, null).use { cursor ->
        if (cursor != null) {
          cursor.moveToFirst()
          val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
          if (index >= 0) {
            filePath = cursor.getString(index)
          }
        }
      }
    }
    if (filePath == null) {
      filePath = uri.path
    }
    return filePath
  }
}