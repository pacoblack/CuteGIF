package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.arthenica.ffmpegkit.FFmpegKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityGifMergeBinding
import me.tasy5kg.cutegif.databinding.ItemMergePageBinding
import me.tasy5kg.cutegif.model.MyConstants
import me.tasy5kg.cutegif.model.MyConstants.OUTPUT_MERGE_DIR
import me.tasy5kg.cutegif.model.MySettings.MAX_FILE_SIZE
import me.tasy5kg.cutegif.toolbox.FileTools.copyToInputFileDir
import me.tasy5kg.cutegif.toolbox.FileTools.fileSize
import me.tasy5kg.cutegif.toolbox.FileTools.resetDirectory
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick
import me.tasy5kg.cutegif.toolbox.Toolbox.toast
import java.io.File

class GifMergeActivity : BaseActivity() {
  private val scope = lifecycleScope
  private val gifUris = mutableListOf<Uri>()
  private val binding by lazy { ActivityGifMergeBinding.inflate(layoutInflater) }
  private lateinit var viewPager: ViewPager2

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
    filterUri()

    //TODO:保存实现
//    binding.mbSave.onClick {
//      copyFile(
//        "$OUTPUT_MERGE_DIR${String.format("%06d", binding.slider.value.toInt())}.png",
//        createNewFile(inputGifPath, "png")
//      )
//      toast(R.string.saved_this_frame_to_gallery)
//      holder.binding.view.apply {
//        visibility = View.VISIBLE
//        postDelayed({
//          visibility = View.INVISIBLE
//        }, 50)
//      }
//    }
  }

  private fun onCheckIsDone(){
    if(gifUris.isEmpty()) {
      toast("无符合要求的文件，请重新选择！")
      finish()
      return
    }
    viewPager = binding.viewPager
    // 设置适配器
    viewPager.adapter = PageAdapter()
    viewPager.offscreenPageLimit = 1

    // 设置页面切换监听器
    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {

      }
    })
    binding.dotsIndicator.attachTo(viewPager)
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

      onCheckIsDone()
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
      context.startActivity(
        Intent(
          context, GifMergeActivity::class.java
        ).putParcelableArrayListExtra(MyConstants.EXTRA_GIF_PATH, parcelableArrayList)
      )
    }
  }

  inner class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    inner class PageViewHolder(val binding: ItemMergePageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
      val binding = ItemMergePageBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
      return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
      val uriItem = gifUris[position]
      val inputGifPath = uriItem.copyToInputFileDir()
      if (inputGifPath.isBlank()) {
        return
      }
      holder.binding.mbSliderMinus.onClick { if (holder.binding.slider.value > holder.binding.slider.valueFrom) holder.binding.slider.value-- }
      holder.binding.mbSliderPlus.onClick { if (holder.binding.slider.value < holder.binding.slider.valueTo) holder.binding.slider.value++ }
      resetDirectory(OUTPUT_MERGE_DIR)
      FFmpegKit.execute("${MyConstants.FFMPEG_COMMAND_PREFIX_FOR_ALL_AN} -i \"$inputGifPath\" \"$OUTPUT_MERGE_DIR%06d.png\"")
      val frameCount = File(OUTPUT_MERGE_DIR).listFiles()?.size
      if (frameCount == null) {
        toast(R.string.unable_to_load_gif)
        finish()
        return
      }
      val mlo = (1..frameCount).map { BitmapFactory.decodeFile(OUTPUT_MERGE_DIR + String.format("%06d", it) + ".png")!! }
      if (mlo.size == 1) {
        holder.binding.llcFrameSelector.visibility = GONE
      } else {
        holder.binding.slider.apply {
          valueTo = mlo.size.toFloat()
          setLabelFormatter { "${it.toInt()}/${valueTo.toInt()}" }
          addOnChangeListener { slider, value, _ ->
            slider.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
            holder.binding.aciv.setImageBitmap(mlo[value.toInt() - 1])
          }
        }
      }
      holder.binding.aciv.setImageBitmap(mlo[0])
//      binding.mbSave.onClick {
//        copyFile(
//          "$OUTPUT_MERGE_DIR${String.format("%06d", binding.slider.value.toInt())}.png",
//          createNewFile(inputGifPath, "png")
//        )
//        toast(R.string.saved_this_frame_to_gallery)
//        holder.binding.view.apply {
//          visibility = View.VISIBLE
//          postDelayed({
//            visibility = View.INVISIBLE
//          }, 50)
//        }
//      }
    }

    override fun getItemCount(): Int = gifUris.size
  }

}