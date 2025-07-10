package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.lifecycleScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.components.preview.MediaExtensions.getMediaType
import me.tasy5kg.cutegif.components.preview.MediaItem
import me.tasy5kg.cutegif.databinding.ActivityGifMergeBinding
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL
import me.tasy5kg.cutegif.model.MyConstants.OUTPUT_MERGE_DIR
import me.tasy5kg.cutegif.model.MyConstants.PICTURE_TO_VIDEO_EXTRACTED_FRAMES_FILE
import me.tasy5kg.cutegif.model.MySettings.MAX_FILE_SIZE
import me.tasy5kg.cutegif.toolbox.FileTools
import me.tasy5kg.cutegif.toolbox.FileTools.copyFile
import me.tasy5kg.cutegif.toolbox.FileTools.copyToInputFileDir
import me.tasy5kg.cutegif.toolbox.FileTools.createNewFile
import me.tasy5kg.cutegif.toolbox.FileTools.fileSize
import me.tasy5kg.cutegif.toolbox.FileTools.resetDirectory
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick
import me.tasy5kg.cutegif.toolbox.Toolbox.toast
import java.io.File

class GifMergeActivity : BaseActivity() {
  private val scope = lifecycleScope
  private val gifUris = mutableListOf<Uri>()
  private val binding by lazy { ActivityGifMergeBinding.inflate(layoutInflater) }

  private val inputGifPaths by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableArrayListExtra(MyConstants.EXTRA_GIF_PATH, Uri::class.java)
    } else {
      intent.getParcelableArrayListExtra(MyConstants.EXTRA_GIF_PATH)
    }
  }

  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    binding.mbClose.onClick { finish() }
    binding.mbSave.onClick { makeVideo() }
    binding.mbSort.isEnabled = false
    initMediaGrid()
    filterUri()
    binding.mbSort.onClick { prepareInput() }
  }

  private fun initMediaGrid(){
    val list = inputGifPaths?.mapIndexed { index, uri-> MediaItem(uri.toString(), ""+index, uri.getMediaType(this))} ?: emptyList()
    binding.mediaGrid.setMediaItems(list)
  }

  private fun prepareInput(){
    scope.launch {
      withContext(Dispatchers.IO) {
        resetDirectory(MyConstants.INPUT_FILE_DIR)
        gifUris.onEachIndexed { index, uri -> uri.copyToInputFileDir(false, String.format("img_%04d.jpg", index+1))}
      }
    }
  }

  private fun makeVideo(){
    resetDirectory(MyConstants.PICTURE_TO_VIDEO_EXTRACTED_FRAMES_PATH)
    // 4. 构建FFmpeg命令
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

    // 5. 执行FFmpeg命令
    logRed("GifMergeActivity Command", cmd.joinToString(" "))
    val session = FFmpegKit.executeAsync(cmd.joinToString(" "), { completeCallback ->
      when {
        completeCallback.returnCode.isValueSuccess -> onTaskSuccess()
        completeCallback.returnCode.isValueError -> quitOrFailed(getString(R.string.an_error_occurred))
      }
    }, { logCallback ->
      logRed("logcallback", logCallback.message.toString())
    }, { statistics ->

    })
  }

  fun onTaskSuccess(){
      val outputUri = createNewFile(FileTools.FileName(inputGifPaths!![0]).nameWithoutExtension, "mp4")
      copyFile(PICTURE_TO_VIDEO_EXTRACTED_FRAMES_FILE, outputUri, true)
      FileSavedActivity.start(this@GifMergeActivity, outputUri)
  }

  protected fun quitOrFailed(toastText: String?) {
    runOnUiThread {
      toastText?.let { toast(it) }
      FFmpegKit.cancel()
      FFmpegKitConfig.clearSessions()
    }
  }

  private fun filterUri(){
    if ((inputGifPaths?.size ?: 0) == 0) return
    val jobs = inputGifPaths!!.map { uri->
      scope.launch {
        val fileSize = withContext(Dispatchers.IO) {
          uri.fileSize()
        }
        if (fileSize <= MAX_FILE_SIZE) {
          synchronized(gifUris) {
            gifUris.add(uri)
          }
          toast("处理文件个数 ${gifUris.size}")
        } else {
          toast("当前文件大小$fileSize, 不会处理！")
        }
      }
    }
    scope.launch {
      jobs.joinAll()
      binding.mbSort.isEnabled = true
      onCheckIsDone()
    }
  }

  private fun onCheckIsDone(){
    if(gifUris.isEmpty()) {
      toast("无符合要求的文件，请重新选择！")
      finish()
      return
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    resetDirectory(OUTPUT_MERGE_DIR)
    scope.cancel()
  }

  companion object {

    fun start(context: Context, uris: List<Uri>?) {
      val parcelableArrayList = uris?.let { uriList ->
        // 创建一个新的 ArrayList<Parcelable>
        val arrayList = ArrayList<Parcelable>(uriList.size)
        // 将所有的 Uri 添加到新的 ArrayList 中
        arrayList.addAll(uriList)
        arrayList
      }
      if (parcelableArrayList.isNullOrEmpty()) {
        toast("请选择至少一张符合要求的图片")
        return
      }
      context.startActivity(
        Intent(
          context, GifMergeActivity::class.java
        ).putParcelableArrayListExtra(MyConstants.EXTRA_GIF_PATH, parcelableArrayList)
      )
    }
  }

}