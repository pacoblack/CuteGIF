package me.tasy5kg.cutegif.model

import me.tasy5kg.cutegif.MyApplication.Companion.appContext

object MyConstants {

  val CACHE_DIR_PATH: String = appContext.cacheDir.canonicalPath
  val PALETTE_PATH = "$CACHE_DIR_PATH/palette.png"
  val INPUT_FILE_DIR = "$CACHE_DIR_PATH/input_file_dir/"
  val ADD_TEXT_RENDER_PNG_PATH = "$CACHE_DIR_PATH/add_text.png"
  val GET_VIDEO_SINGLE_FRAME_WITH_FFMPEG_TEMP_PATH = "$CACHE_DIR_PATH/get_video_single_frame_with_ffmpeg_temp_path.jpg"
  val OUTPUT_GIF_TEMP_PATH = "$CACHE_DIR_PATH/output_temp.gif"
  val OUTPUT_SPLIT_DIR = "$CACHE_DIR_PATH/split_dir/"
  val VIDEO_TO_GIF_PREVIEW_CACHE_DIR = "$CACHE_DIR_PATH/video_to_gif_preview_cache_dir/"
  val VIDSTABDETECT_RESULT_PATH = "$CACHE_DIR_PATH/transforms.trf"
  val VIDEO_TO_GIF_EXTRACTED_FRAMES_PATH = "$CACHE_DIR_PATH/extracted_frames/"
  val NATIVE_LIBRARY_DIR: String = appContext.applicationInfo.nativeLibraryDir

  const val EXTRA_TEXT_RENDER = "EXTRA_TEXT_RENDER"
  const val EXTRA_TASK_BUILDER_VIDEO_TO_GIF = "EXTRA_TASK_BUILDER_VIDEO_TO_GIF"
  const val EXTRA_VIDEO_POSITION = "EXTRA_VIDEO_POSITION"
  const val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
  const val EXTRA_GIF_PATH = "EXTRA_GIF_PATH"
  const val EXTRA_MVIMG_PATH = "EXTRA_MVIMG_PATH"
  const val EXTRA_STACK_TRACE_STRING = "EXTRA_STACK_TRACE_STRING"
  const val EXTRA_ADD_TEXT_RENDER = "EXTRA_ADD_TEXT_RENDER"
  const val EXTRA_SAVED_FILE_URI = "EXTRA_SAVED_FILE_URI"

  const val MIME_TYPE_IMAGE_GIF = "image/gif"
  const val MIME_TYPE_VIDEO_MP4 = "video/mp4"

  // Specifying "-hwaccel mediacodec" may result in slower decoding than "-hwaccel auto"
  const val FFMPEG_COMMAND_PREFIX_FOR_ALL_AN = "-hwaccel auto -hide_banner -benchmark -an"
  const val FFMPEG_COMMAND_PREFIX_FOR_ALL = "-hwaccel auto -hide_banner -benchmark"
  const val URI_JOIN_QQ_GROUP =
    "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$5Frn_d8p0OxBX6NwtTAhAhDQh2FTCcI9"
}