package me.tasy5kg.cutegif.components.preview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.MediaGridItemBinding


internal class MediaGridAdapter(
  private val context: Context, private val mediaItems: MutableList<MediaItem>,
  private val itemSize: Int, private val labelHeight: Int, private val labelTextSize: Int, private val playButtonSize: Int
) : RecyclerView.Adapter<MediaGridAdapter.MediaViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
    return MediaViewHolder(MediaGridItemBinding.inflate(LayoutInflater.from(context), parent, false))
  }

  override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
    val item = mediaItems[position]

    // 设置视图尺寸
    val layoutParams = holder.itemView.layoutParams
    layoutParams.width = itemSize
    layoutParams.height = itemSize
    holder.itemView.setLayoutParams(layoutParams)

    // 设置标签高度
    val labelParams = holder.typeContainer.layoutParams
    labelParams.height = labelHeight
    holder.typeContainer.setLayoutParams(labelParams)

    // 设置标签文本大小
    holder.typeLabel.textSize = labelTextSize.toFloat()

    // 设置播放按钮尺寸
    val playParams = holder.playButton.layoutParams
    playParams.width = playButtonSize
    playParams.height = playButtonSize
    holder.playButton.setLayoutParams(playParams)

    // 加载媒体内容
    Glide.with(context)
      .load(item.url)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(holder.mediaImage)

    // 设置类型标签
    holder.typeLabel.text = item.title

    // 根据类型设置标签颜色和图标
    when (item.type) {
      MediaItem.TYPE_IMAGE -> {
        holder.typeContainer.setBackgroundColor("#4CAF50".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_image)
        holder.playButton.setVisibility(View.GONE)
      }

      MediaItem.TYPE_GIF -> {
        holder.typeContainer.setBackgroundColor("#FFC107".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_gif)
        holder.playButton.setVisibility(View.GONE)
      }

      MediaItem.TYPE_VIDEO -> {
        holder.typeContainer.setBackgroundColor("#2196F3".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_video)
        holder.playButton.setVisibility(View.VISIBLE)
      }

      MediaItem.TYPE_MOTION_PHOTO -> {
        holder.typeContainer.setBackgroundColor("#9C27B0".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_motion_photo)
        holder.playButton.setVisibility(View.VISIBLE)
      }
    }
  }

  override fun getItemCount(): Int {
    return mediaItems.size
  }

  internal class MediaViewHolder(binding: MediaGridItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var mediaImage: ImageView = binding.mediaImage
    var typeContainer: View = binding.typeContainer
    var typeIcon: ImageView = binding.typeIcon
    var typeLabel: TextView = binding.typeLabel
    var playButton: ImageView = binding.playButton
  }
}