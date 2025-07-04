package me.tasy5kg.cutegif.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityVideoToGifPerformerBinding
import me.tasy5kg.cutegif.model.MyConstants.EXTRA_TASK_BUILDER_VIDEO_TO_GIF
import me.tasy5kg.cutegif.task.TaskBuilderVideoToGif
import me.tasy5kg.cutegif.toolbox.Toolbox
import me.tasy5kg.cutegif.toolbox.Toolbox.constraintBy
import me.tasy5kg.cutegif.toolbox.Toolbox.getExtra
import me.tasy5kg.cutegif.toolbox.Toolbox.keepScreenOn
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick
import kotlin.concurrent.thread

open class BaseVideoPerformActivity : BaseActivity() {
  protected val binding by lazy { ActivityVideoToGifPerformerBinding.inflate(layoutInflater) }
  protected var taskThread: Thread? = null
  protected var taskQuitOrFailed = false
  protected val taskBuilder by lazy { intent.getExtra<TaskBuilderVideoToGif>(EXTRA_TASK_BUILDER_VIDEO_TO_GIF) }

  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    setFinishOnTouchOutside(false)
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        quitOrFailed(getString(R.string.cancelled))
      }
    })
    binding.mbClose.onClick {
      quitOrFailed(getString(R.string.cancelled))
    }
    taskThread = thread { doPerformOnThread() }
  }

  protected fun quitOrFailed(toastText: String?) {
    runOnUiThread {
      taskQuitOrFailed = true
      toastText?.let { Toolbox.toast(it) }
      FFmpegKit.cancel()
      FFmpegKitConfig.clearSessions()
      taskThread?.interrupt()
      finish()
    }
  }

  protected fun putProgress(progress: Int?, text: String) {
    runOnUiThread {
      binding.linearProgressIndicator.apply {
        if (progress == null) {
          isIndeterminate = true
        } else {
          isIndeterminate = false
          setProgress(progress.constraintBy(0..100), true)
        }
      }
      binding.mtvTitle.text = text
    }
  }

  override fun onDestroy() {
    keepScreenOn(false)
    super.onDestroy()
  }

  open fun doPerformOnThread(){}

}