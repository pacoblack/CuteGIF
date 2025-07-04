package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import com.arthenica.ffmpegkit.FFmpegKit
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.model.MyConstants.EXTRA_TASK_BUILDER_VIDEO_TO_GIF
import me.tasy5kg.cutegif.model.MyConstants.VIDEO_TO_VIDEO_EXTRACTED_FRAMES_FILE
import me.tasy5kg.cutegif.model.MyConstants.VIDEO_TO_VIDEO_EXTRACTED_FRAMES_PATH
import me.tasy5kg.cutegif.task.TaskBuilderVideoToGif
import me.tasy5kg.cutegif.toolbox.FileTools
import me.tasy5kg.cutegif.toolbox.FileTools.copyFile
import me.tasy5kg.cutegif.toolbox.FileTools.createNewFile
import me.tasy5kg.cutegif.toolbox.Toolbox.constraintBy
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed

class EditVideoPerformActivity : BaseVideoPerformActivity() {

  override fun doPerformOnThread() {
    putProgress(0, getString(R.string.exporting_gif_))
    FileTools.resetDirectory(VIDEO_TO_VIDEO_EXTRACTED_FRAMES_PATH)
    val command = taskBuilder.getCommandExportVideo()
    logRed("CommandExtractFrame", command)
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

  fun onTaskSuccess(){
    with(taskBuilder) {
      val outputUri = createNewFile(FileTools.FileName(inputVideoPath).nameWithoutExtension, "mp4")
      copyFile(VIDEO_TO_VIDEO_EXTRACTED_FRAMES_FILE, outputUri, true)
      finish()
      FileSavedActivity.start(this@EditVideoPerformActivity, outputUri)
    }
  }

  companion object {
    fun start(context: Context, taskBuilderVideoToGif: TaskBuilderVideoToGif) =
      context.startActivity(Intent(context, EditVideoPerformActivity::class.java).apply {
        putExtra(EXTRA_TASK_BUILDER_VIDEO_TO_GIF, taskBuilderVideoToGif)
      })
  }
}