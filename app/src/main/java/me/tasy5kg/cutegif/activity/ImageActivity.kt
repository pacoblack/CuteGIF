package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityImageBinding
import me.tasy5kg.cutegif.databinding.ItemImagePreviewBinding
import me.tasy5kg.cutegif.toolbox.Toolbox.toast

class ImagePreviewActivity : AppCompatActivity() {
  private val binding by lazy { ActivityImageBinding.inflate(layoutInflater) }

  private lateinit var viewPager: ViewPager2
  private lateinit var adapter: ImageAdapter

  private val imageUrls by lazy { intent.getStringArrayListExtra("image_urls")  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    val startPosition = intent.getIntExtra("start_position", 0)
    if (imageUrls.isNullOrEmpty()) {
      toast("图片列表为空，请重新选择")
      finish()
      return
    }
    // 关闭按钮
    binding.btnClose.setOnClickListener { finish() }

    // 初始化视图
    viewPager = binding.viewPager

    // 设置适配器
    adapter = ImageAdapter(this, imageUrls!!)
    viewPager.setAdapter(adapter)
    viewPager.setCurrentItem(startPosition, false)

    // 设置标题
    updateTitle(startPosition)

    // 初始化指示器
    binding.dotsIndicator.attachTo(viewPager)

    // 设置页面变化监听
    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        updateTitle(position)
      }
    })

  }

  private fun updateTitle(position: Int) {
    binding.tvTitle.text = String.format("图片预览 (%d/%d)", position + 1, imageUrls!!.size)
  }

  // 图片适配器
  internal class ImageAdapter(val context:Context, private val imageUrls: ArrayList<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
      return ImageViewHolder(ItemImagePreviewBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
      // 使用Glide加载图片（支持网络图片和本地资源）
      Glide.with(context)
        .load(imageUrls)
        .placeholder(R.drawable.media_placeholder)
        .error(R.drawable.media_placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(holder.photoView)
    }

    override fun getItemCount(): Int {
      return imageUrls.size
    }

    internal class ImageViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
      var photoView: ImageView = binding.photoView
    }
  }

  companion object {
    // 启动预览Activity的方法
    fun start(context: Context, imageUrls: ArrayList<String>, startPosition: Int) {
      val intent = Intent(context, ImagePreviewActivity::class.java)
      intent.putStringArrayListExtra("image_urls", imageUrls)
      intent.putExtra("start_position", startPosition)
      context.startActivity(intent)
    }
  }
}