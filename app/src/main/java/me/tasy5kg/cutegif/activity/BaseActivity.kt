package me.tasy5kg.cutegif.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arthenica.ffmpegkit.FFmpegKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.model.ActivityCollector
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MySettings
import me.tasy5kg.cutegif.toolbox.FileTools
import kotlin.system.exitProcess

abstract class BaseActivity : AppCompatActivity() {
  private val scope = lifecycleScope
  abstract fun onCreateIfEulaAccepted(savedInstanceState: Bundle?)

  final override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ActivityCollector.addActivity(this)
    if (!MySettings.eulaAccepted) {
      /** Ask user to accept EULA */
      EulaActivity.start(this)
      finish()
    } else if (BetaEndedActivity.testVersionRemainingDays() < 0) {
      BetaEndedActivity.start(this)
      finish()
    } else {
      if (BuildConfig.DEBUG) {
        /** Show a dialog with logs when app crashed */
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
          AppCrashedActivity.start(this, e.stackTraceToString())
          exitProcess(1)
        }
      }
      onCreateIfEulaAccepted(savedInstanceState)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    ActivityCollector.removeActivity(this)
    if (ActivityCollector.isEmpty()) {
      FFmpegKit.cancel()
      FileTools.resetDirectory(MyConstants.CACHE_DIR_PATH)
    }
    scope.cancel()
  }

  open fun addBackgroundTask(task: Runnable, callback: Runnable){
    scope.launch {
      withContext(Dispatchers.IO) {
        task.run()
      }
    }.invokeOnCompletion { callback.run() }
  }


}