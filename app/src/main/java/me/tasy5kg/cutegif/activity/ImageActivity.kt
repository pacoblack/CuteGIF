package me.tasy5kg.cutegif.activity

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import me.tasy5kg.cutegif.databinding.ActivityImageBinding
import me.tasy5kg.cutegif.databinding.ItemImagePreviewBinding
import me.tasy5kg.cutegif.databinding.ItemMergePageBinding

class ImagePreviewActivity : AppCompatActivity() {
  private val binding by lazy { ActivityImageBinding.inflate(layoutInflater) }

  private lateinit var viewPager: ViewPager2
  private lateinit var adapter: ImageAdapter
  private lateinit var indicatorContainer: LinearLayout
  private lateinit var tvTitle: TextView

  // 指示器点
  private val indicators: MutableList<ImageView?> = ArrayList<ImageView?>()

  // 图片资源（实际项目中可以从Intent传递）
  private var imageUrls: MutableList<String?>? = ArrayList<String?>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)


    // 获取传递的图片列表和初始位置
    imageUrls = intent.getStringArrayListExtra("image_urls")
    val startPosition = intent.getIntExtra("start_position", 0)


    // 初始化视图
    viewPager = binding.viewPager
    tvTitle = binding.tvTitle

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
        updateIndicators(position)
      }
    })

    // 关闭按钮
    val btnClose = binding.btnClose
    btnClose.setOnClickListener(View.OnClickListener { v: View? -> finish() })
  }

  private fun updateTitle(position: Int) {
    tvTitle!!.setText(String.format("图片预览 (%d/%d)", position + 1, imageUrls!!.size))
  }

  private fun updateIndicators(position: Int) {
    for (i in indicators.indices) {
      indicators.get(i)!!.setSelected(i == position)
    }
  }

  // 图片适配器
  internal class ImageAdapter(val context:Context, val binding: ItemImagePreviewBinding, private val imageUrls: MutableList<String?>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder?>() {
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
        .load(imageUrls[position])
        .placeholder(R.drawable.ic_secure)
        .error(R.drawable.ic_secure)
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
    fun start(context: Context, imageUrls: ArrayList<String?>?, startPosition: Int) {
      val intent = Intent(context, ImagePreviewActivity::class.java)
      intent.putStringArrayListExtra("image_urls", imageUrls)
      intent.putExtra("start_position", startPosition)
      context.startActivity(intent)
    }
  }
}