package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import com.arthenica.ffmpegkit.FFmpegKit
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL
import me.tasy5kg.cutegif.model.MyConstants.PICTURE_TO_VIDEO_EXTRACTED_FRAMES_FILE
import me.tasy5kg.cutegif.model.MyConstants.PICTURE_TO_VIDEO_EXTRACTED_FRAMES_PATH
import me.tasy5kg.cutegif.toolbox.FileTools
import me.tasy5kg.cutegif.toolbox.FileTools.copyFile
import me.tasy5kg.cutegif.toolbox.FileTools.createNewFile
import me.tasy5kg.cutegif.toolbox.Toolbox.constraintBy
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed
import java.io.File

class PicToVideoPerformActivity: BaseVideoPerformActivity() {
  override fun doPerformOnThread() {
    putProgress(0, getString(R.string.exporting_gif_))
    FileTools.resetDirectory(PICTURE_TO_VIDEO_EXTRACTED_FRAMES_PATH)
    val command = makeCommand()
    logRed("CommandPicToVideo", command)
    FFmpegKit.executeAsync(command, { completeCallback ->
      when {
        completeCallback.returnCode.isValueSuccess -> onTaskSuccess()
        completeCallback.returnCode.isValueError -> quitOrFailed(getString(R.string.an_error_occurred))
      }
    }, { logCallback ->
      logRed("logcallback", logCallback.message.toString())
    }, { statistics ->
      putProgress(
        (statistics.videoFrameNumber * 40 / taskBuilder.getOutputFramesEstimated()).constraintBy(0..40), getString(R.string.exporting_gif_)
      )
    })
  }

  private fun makeCommand(): String {
    val cmd: MutableList<String> = ArrayList()
    cmd.add(FFMPEG_COMMAND_PREFIX_FOR_ALL)
    cmd.add("-y") // 覆盖输出
    cmd.add("-framerate")
    cmd.add(java.lang.String.valueOf(1.0 / 1)) // 每张图显示秒数
    cmd.add("-i")
    cmd.add(File(MyConstants.INPUT_FILE_DIR, "img_%04d.jpg").absolutePath)
    cmd.add("-c:v")
    cmd.add("libx264")
    cmd.add("-r")
    cmd.add("30") // 输出帧率
    cmd.add("-pix_fmt")
    cmd.add("yuv420p") // 兼容格式
    cmd.add("-vf")
    cmd.add("scale=1280:-2") // 缩放为1280宽度，高度自动保持比例
    cmd.add(PICTURE_TO_VIDEO_EXTRACTED_FRAMES_FILE)
    return cmd.joinToString(" ")
  }

  fun onTaskSuccess(){
    with(taskBuilder) {
      val outputUri = createNewFile(FileTools.FileName(inputVideoPath).nameWithoutExtension, "mp4")
      copyFile(PICTURE_TO_VIDEO_EXTRACTED_FRAMES_FILE, outputUri, true)
      finish()
      FileSavedActivity.start(this@PicToVideoPerformActivity, outputUri)
    }
  }

  companion object {
    fun start(context: Context) =
      context.startActivity(Intent(context, PicToVideoPerformActivity::class.java))
  }
}