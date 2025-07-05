package me.tasy5kg.cutegif.components.preview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.activity.ImagePreviewActivity
import me.tasy5kg.cutegif.databinding.MediaGridItemBinding
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed
import java.util.Collections

internal class MediaGridAdapter(
  private val context: Context, private val mediaItems: MutableList<MediaItem>,
  private val itemSize: Int, private val labelHeight: Int, private val labelTextSize: Int, private val playButtonSize: Int
) : RecyclerView.Adapter<MediaGridAdapter.MediaViewHolder>() {
  interface OnItemMoveListener {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDropped()
  }

  internal var moveListener: OnItemMoveListener? = null
  internal var isSwapMode = true // 默认为交换模式

  fun setMoveListener(l: OnItemMoveListener){
    this.moveListener = l
  }

  fun setSwapMode(value: Boolean){
    this.isSwapMode = value
  }

  fun printItems(){
    this.mediaItems.forEach {item -> logRed(TAG, "title: ${item.title}") }
  }

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

    holder.bindClickListener { ImagePreviewActivity.start(context, mediaItems, position) }
    // 加载媒体内容
    when (item.type) {
      MediaItem.TYPE_IMAGE -> setupImageView(holder, item)
      MediaItem.TYPE_GIF -> setupGifView(holder, item)
      MediaItem.TYPE_VIDEO -> setupVideoView(holder, item)
      MediaItem.TYPE_MOTION_PHOTO -> setupMotionPhotoView(holder, item)
    }

    // 设置类型标签
    holder.typeLabel.text = item.title

    // 根据类型设置标签颜色和图标
    when (item.type) {
      MediaItem.TYPE_IMAGE -> {
        holder.typeContainer.setBackgroundColor("#4CAF50".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_image)
      }

      MediaItem.TYPE_GIF -> {
        holder.typeContainer.setBackgroundColor("#FFC107".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_gif)
      }

      MediaItem.TYPE_VIDEO -> {
        holder.typeContainer.setBackgroundColor("#2196F3".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_video)
      }

      MediaItem.TYPE_MOTION_PHOTO -> {
        holder.typeContainer.setBackgroundColor("#9C27B0".toColorInt())
        holder.typeIcon.setImageResource(R.drawable.ic_media_motion_photo)
      }
    }

    holder.itemView.setOnLongClickListener {
      if (moveListener != null) {
        moveListener!!.onItemMove(holder.absoluteAdapterPosition, -1);
      }
      true;
    }
  }

  private fun setupImageView(holder: MediaViewHolder, item: MediaItem) {
    // 显示图片视图，隐藏视频视图
    holder.mediaImage.setVisibility(VISIBLE)
    holder.videoPlayer.setVisibility(GONE)
    holder.playButton.setVisibility(GONE)

    // 加载图片
    Glide.with(context)
      .load(item.url)
      .placeholder(R.drawable.media_placeholder)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(holder.mediaImage)
  }

  private fun setupGifView(holder: MediaViewHolder, item: MediaItem) {
    // 显示图片视图，隐藏视频视图
    holder.mediaImage.setVisibility(VISIBLE)
    holder.videoPlayer.setVisibility(GONE)
    holder.playButton.setVisibility(GONE)

    // 加载GIF
    Glide.with(context)
      .asGif()
      .load(item.url)
      .placeholder(R.drawable.media_placeholder)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(holder.mediaImage)
  }

  private fun setupVideoView(holder: MediaViewHolder, item: MediaItem) {
    // 显示视频视图，隐藏图片视图
    holder.mediaImage.setVisibility(GONE)
    holder.videoPlayer.setVisibility(VISIBLE)
    holder.playButton.setVisibility(GONE) // 视频有自己的播放按钮

    // 设置视频URL
    holder.videoPlayer.setVideoUrl(item.url)

    holder.bindClickListener { holder.videoPlayer.startPlayback() }
  }

  private fun setupMotionPhotoView(holder: MediaViewHolder, item: MediaItem) {
    // 显示图片视图，隐藏视频视图
    holder.mediaImage.setVisibility(VISIBLE)
    holder.videoPlayer.setVisibility(GONE)
    holder.playButton.setVisibility(GONE) // 显示播放按钮

    // 加载动态照片
    Glide.with(context)
      .load(item.url)
      .placeholder(R.drawable.media_placeholder)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(holder.mediaImage)
  }

  override fun getItemCount(): Int {
    return mediaItems.size
  }

  // 交换两个项目的位置
  fun swapItems(fromPosition: Int, toPosition: Int) {
    Collections.swap(mediaItems, fromPosition, toPosition)
    notifyItemMoved(fromPosition, toPosition)
    updatePositions()
  }

  // 将项目插入到新位置
  fun insertItem(fromPosition: Int, toPosition: Int) {
    if (fromPosition < toPosition) {
      for (i in fromPosition..<toPosition) {
        Collections.swap(mediaItems, i, i + 1)
      }
    } else {
      for (i in fromPosition downTo toPosition + 1) {
        Collections.swap(mediaItems, i, i - 1)
      }
    }
    notifyItemMoved(fromPosition, toPosition)
    updatePositions()
  }

  // 更新所有项目的位置信息
  private fun updatePositions() {
    notifyItemRangeChanged(0, mediaItems.size)
  }

  internal class MediaViewHolder(binding: MediaGridItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var videoPlayer: VideoPlayerView = binding.videoPlayer
    var mediaImage: ImageView = binding.mediaImage
    var typeContainer: View = binding.typeContainer
    var typeIcon: ImageView = binding.typeIcon
    var typeLabel: TextView = binding.typeLabel
    var playButton: ImageView = binding.playButton

    fun bindClickListener(clickListener: () -> Unit) {
      itemView.setOnClickListener {
        clickListener()
      }
    }
  }

  companion object{
    const val TAG = "MediaGridAdapter"
  }
}