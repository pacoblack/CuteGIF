package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.View.GONE
import androidx.lifecycle.lifecycleScope
import com.arthenica.ffmpegkit.FFmpegKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityGifSplitBinding
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MyConstants.OUTPUT_SPLIT_DIR
import me.tasy5kg.cutegif.toolbox.Callback
import me.tasy5kg.cutegif.toolbox.FileTools.copyToExternalDir
import me.tasy5kg.cutegif.toolbox.FileTools.getExternalDir
import me.tasy5kg.cutegif.toolbox.FileTools.resetDirectory
import me.tasy5kg.cutegif.toolbox.PermissionTools
import me.tasy5kg.cutegif.toolbox.Toolbox.getExtra
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick
import me.tasy5kg.cutegif.toolbox.Toolbox.toast
import java.io.File

class GifSplitActivity : BaseActivity() {
  private val exportDir by lazy { getExternalDir(this, "split") }
  private val binding by lazy { ActivityGifSplitBinding.inflate(layoutInflater) }
  private val inputGifPath by lazy { intent.getExtra<String>(MyConstants.EXTRA_GIF_PATH) }

  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    binding.mbClose.onClick { finish() }
    binding.mbSliderMinus.onClick { if (binding.slider.value > binding.slider.valueFrom) binding.slider.value-- }
    binding.mbSliderPlus.onClick { if (binding.slider.value < binding.slider.valueTo) binding.slider.value++ }
    requestPermission()
    addBackgroundTask(
      task = {runOnBackgroundInitTask()},
      callback = {onBackgroundIsDone()},
    )
  }

  fun requestPermission(){
    PermissionTools.requestStorage(this,object : Callback<Void, Void> {
      override fun call(p: Void?): Void? {
        return null
      }
    }, object : Callback<List<String>, Void> {
      override fun call(p: List<String>?): Void? {
        finish()
        return null
      }
    })
  }

  fun runOnBackgroundInitTask(){
    resetDirectory(OUTPUT_SPLIT_DIR)
    FFmpegKit.execute("${MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL_AN} -i \"$inputGifPath\" \"$OUTPUT_SPLIT_DIR%06d.png\"")
  }

  fun onBackgroundIsDone(){
    val frameCount = File(OUTPUT_SPLIT_DIR).listFiles()?.size
    if (frameCount == null) {
      toast(R.string.unable_to_load_gif)
      finish()
      return
    }
    val mlo = (1..frameCount).map { BitmapFactory.decodeFile(OUTPUT_SPLIT_DIR + String.format("%06d", it) + ".png")!! }
    if (mlo.size == 1) {
      binding.llcFrameSelector.visibility = GONE
    } else {
      binding.slider.apply {
        valueTo = mlo.size.toFloat()
        setLabelFormatter { "${it.toInt()}/${valueTo.toInt()}" }
        addOnChangeListener { slider, value, _ ->
          slider.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
          binding.aciv.setImageBitmap(mlo[value.toInt() - 1])
        }
      }
    }
    binding.aciv.setImageBitmap(mlo[0])
    binding.mbSave.onClick {
      onSave()
    }
  }

  fun onSave(){
    lifecycleScope.launch {
      val job1 = async {
        resetDirectory(exportDir.toString())
        copyToExternalDir("$OUTPUT_SPLIT_DIR${String.format("%06d", binding.slider.value.toInt())}.png",
          exportDir, "${String.format("%06d", binding.slider.value.toInt())}.png")
      }
      withContext(Dispatchers.Main) {
        val results = awaitAll(job1)
        if (results.all { it }) {
          toast(R.string.saved_this_frame_to_gallery)
          binding.view.apply {
            visibility = View.VISIBLE
            postDelayed({
              visibility = View.INVISIBLE
            }, 50)
          }
        } else {
          toast("拷贝文件失败！")
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    resetDirectory(OUTPUT_SPLIT_DIR)
  }

  companion object {
    fun start(context: Context, gifPath: String) = context.startActivity(
      Intent(
        context, GifSplitActivity::class.java
      ).putExtra(MyConstants.EXTRA_GIF_PATH, gifPath)
    )
  }
}