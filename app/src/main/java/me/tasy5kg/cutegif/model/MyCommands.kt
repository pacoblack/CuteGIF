package me.tasy5kg.cutegif.model

import me.tasy5kg.cutegif.model.MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL
import me.tasy5kg.cutegif.model.MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL_AN
import me.tasy5kg.cutegif.model.MyConstants.VIDSTABDETECT_RESULT_PATH


object MyCommands {
  val COMMAND_VERSION ="-version"

  fun COMMAND_SPLIT_PNG(inputGifPath:String, OUTPUT_SPLIT_DIR:String) : String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"$inputGifPath\" \"$OUTPUT_SPLIT_DIR%06d.png\""
  }

  fun COMMAND_TO_VIDEO(inputGifPath: String):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"$inputGifPath\" " + "-c:v libx264 -crf 23 -preset veryslow -pix_fmt yuv420p " + "-vf pad=\"width=ceil(iw/2)*2:height=ceil(ih/2)*2\" -movflags +faststart "
  }

  fun COMMAND_3(extractedVideoPath:String):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i $extractedVideoPath -vf vidstabdetect=shakiness=10:accuracy=15:tripod=1:result=\"$VIDSTABDETECT_RESULT_PATH\" -f null -"
  }
  fun COMMAND_4(extractedVideoPath:String, extractedVideoStabilizedPath: String):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL -i $extractedVideoPath -c:a copy -c:v libx264 -crf 17 -preset:v veryfast -vf vidstabtransform=input=\"$VIDSTABDETECT_RESULT_PATH\":tripod=1 -y $extractedVideoStabilizedPath"
  }
  fun COMMAND_5(colorKey:Pair<String, Int>?, catchShortLength: String?, catchShortLengthColorKey: String?):String{
    var command = ""
    colorKey?.let {
      command = "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"$catchShortLength\" -vf colorkey=#${it.first}:${it.second / 100f}:0 -y \"$catchShortLengthColorKey\""
    }
    return command
  }
  fun COMMAND_6(catchShortLengthColorKey: String?, colorQuality:String, catchShortLengthColorKeyPalettegen: String?):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"$catchShortLengthColorKey\" -filter_complex palettegen=max_colors=$colorQuality:stats_mode=diff -y \"$catchShortLengthColorKeyPalettegen\""
  }
  fun COMMAND_7(catchShortLengthColorKey: String?, catchShortLengthColorKeyPalettegen: String?,catchShortLengthColorKeyPaletteuse: String?):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL_AN -i \"$catchShortLengthColorKey\" -i $catchShortLengthColorKeyPalettegen -filter_complex \"[0:v][1:v] paletteuse=dither=bayer\" -y \"$catchShortLengthColorKeyPaletteuse\""
  }
  fun COMMAND_8(inputVideoPath:String, fallbackMp4Path:String):String{
    return "$FFMPEG_COMMAND_PREFIX_FOR_ALL -i \"$inputVideoPath\" -c:v libx264 -preset:v veryfast -crf 17 -pix_fmt yuv420p -c:a aac -b:a 128k -y \"$fallbackMp4Path\""
  }

}