package me.tasy5kg.cutegif.task

import me.tasy5kg.cutegif.components.TextRender
import me.tasy5kg.cutegif.model.CropParams
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MyConstants.ADD_TEXT_RENDER_PNG_PATH
import me.tasy5kg.cutegif.model.MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL_AN
import me.tasy5kg.cutegif.model.MyConstants.OUTPUT_GIF_TEMP_PATH
import me.tasy5kg.cutegif.toolbox.MediaTools.saveToPng
import me.tasy5kg.cutegif.toolbox.Toolbox.toEmptyStringIf
import java.io.Serializable
import kotlin.math.ceil
import kotlin.math.min

data class TaskBuilderVideoToGif(
  val inputVideoPath: String,
  /** 1 == 1ms , do not use IntRange because it is not serializable */
  val trimTime: Pair<Int, Int>?,
  val cropParams: CropParams,
  /** the resolution of the short side when outputting */
  val shortLength: Int,
  val outputSpeed: Float,
  val outputFps: Int,
  val colorQuality: Int,
  val reverse: Boolean,
  val textRender: TextRender?,
  val lossy: Int?,
  val videoWH: Pair<Int, Int>,
  val duration: Int,
  /** The interval between every loops, in centi seconds. (1 == 0.01 sec) */
  val finalDelay: Int,
  /** Color(RRGGBB), Similarity * 100 */
  val colorKey: Pair<String, Int>?
) : Serializable {

  init {
    TextRender.render(textRender, videoWH.first, videoWH.second).saveToPng(ADD_TEXT_RENDER_PNG_PATH)
  }

  fun getForPreviewOnly() = TaskBuilderVideoToGifForPreview(shortLength, colorQuality, lossy, videoWH, colorKey)

  fun getOutputFramesEstimated() = ceil((trimTime?.let { it.second - it.first } ?: duration) * outputFps / outputSpeed / 1000.0).toInt()

  private fun resolutionParams(cropParams: CropParams, shortLength: Int): String {
    val short = cropParams.shortLength()
    val pixel = min(shortLength, short)
    return if (shortLength == 0 || shortLength >= short) {
      ""
    } else {
      ",scale=" + if ((cropParams.outW > cropParams.outH)) {
        "-2:$pixel"
      } else {
        "$pixel:-2"
      } + ":flags=lanczos"
    }
  }

  // 第一步 配置视频的各种参数
  fun getCommandExtractFrame() =
    "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN ${trimTime?.let { "-ss ${trimTime.first}ms -to ${trimTime.second}ms " } ?: ""}" + // 配置视频的开始时间和截止时间
      " -i \"$inputVideoPath\" -i \"$ADD_TEXT_RENDER_PNG_PATH\" " + // 给视频配置文字
      "-filter_complex \"[0:v] setpts=PTS/$outputSpeed,fps=fps=$outputFps [0vPreprocessed]; " + // 配置视频的播放速度
      "[0vPreprocessed][1:v] overlay=0:0," + cropParams.toFFmpegCropCommand() + resolutionParams(cropParams, shortLength) +  // 配置视频的宽高
      (colorKey?.let { ",colorkey=#${it.first}:${it.second / 100f}:0" } ?: "") + // 配置视频的抠图
      (",reverse").toEmptyStringIf { !reverse } + //  配置视频是否倒放
      "\" \"${MyConstants.VIDEO_TO_GIF_EXTRACTED_FRAMES_PATH}%06d.bmp\""

  // 第二步 生成调色板文件
  fun getCommandCreatePalette() =
    "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"${MyConstants.VIDEO_TO_GIF_EXTRACTED_FRAMES_PATH}%06d.bmp\" " + "-vf palettegen=max_colors=${colorQuality}:stats_mode=diff -y \"${MyConstants.PALETTE_PATH}\""

  // 第三步 使用调色板生成gif
  fun getCommandVideoToGif() =
    "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -framerate $outputFps -i \"${MyConstants.VIDEO_TO_GIF_EXTRACTED_FRAMES_PATH}%06d.bmp\" -i \"${MyConstants.PALETTE_PATH}\" " + "-filter_complex paletteuse=dither=bayer -final_delay $finalDelay -y \"$OUTPUT_GIF_TEMP_PATH\""
}