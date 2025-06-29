package me.tasy5kg.cutegif.components.preview

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cv.pic.exo.video.VideoPlayerActivity
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.MediaVideoPlayerLayoutBinding


class VideoPlayerView : FrameLayout {
  private lateinit var binding: MediaVideoPlayerLayoutBinding
  private lateinit var videoView: VideoView
  private lateinit var thumbnailView: ImageView
  private lateinit var progressBar: ProgressBar
  private lateinit var playButton: ImageView
  private lateinit var muteButton: ImageView

  private var isMuted = true
  private var isPrepared = false
  private var videoUrl: String? = null

  constructor(context: Context) : super(context) {
    init(context)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  private fun init(context: Context) {
    binding = MediaVideoPlayerLayoutBinding.inflate(LayoutInflater.from(context),this, true)

    videoView = binding.videoView
    thumbnailView = binding.videoThumbnail
    progressBar = binding.videoProgress
    playButton = binding.playButton
    muteButton = binding.muteButton


    // 设置初始UI状态
    videoView.setVisibility(GONE)
    thumbnailView.setVisibility(VISIBLE)
    progressBar.setVisibility(GONE)
    playButton.setVisibility(VISIBLE)
    muteButton.setVisibility(GONE)


    // 设置监听器
    playButton.setOnClickListener(OnClickListener { v: View? -> startPlayback() })
    muteButton.setOnClickListener(OnClickListener { v: View? -> toggleMute() })

    videoView.setOnPreparedListener(OnPreparedListener { mp: MediaPlayer? ->
      isPrepared = true
      progressBar.setVisibility(GONE)
      videoView.setVisibility(VISIBLE)
      thumbnailView.setVisibility(GONE)
      playButton.setVisibility(GONE)
      muteButton.setVisibility(VISIBLE)
      setMuteState(isMuted)
    })

    videoView.setOnCompletionListener(OnCompletionListener { mp: MediaPlayer? -> resetPlayer() })
  }

  fun setVideoUrl(url: String?) {
    this.videoUrl = url
    // 加载视频缩略图
    loadThumbnail(url)
  }

  private fun loadThumbnail(url: String?) {
    Glide.with(context)
      .load(url)
      .apply(
        RequestOptions()
          .frame(1000) // 获取第一秒的帧作为缩略图
          .centerCrop()
      )
      .into(thumbnailView)
  }

  fun startPlayback() {
//    progressBar.setVisibility(VISIBLE)
    playButton.setVisibility(GONE)
    VideoPlayerActivity.start(context, videoUrl!!)

//    videoView.setVideoURI(videoUrl!!.toUri())
//    videoView.requestFocus()
  }

  private fun resetPlayer() {
    videoView.stopPlayback()
    videoView.setVisibility(GONE)
    thumbnailView.setVisibility(VISIBLE)
    playButton.setVisibility(VISIBLE)
    muteButton.setVisibility(GONE)
  }

  private fun toggleMute() {
    isMuted = !isMuted
    setMuteState(isMuted)
  }

  private fun setMuteState(muted: Boolean) {
    if (isPrepared) {
      if (muted) {
        setVolume(0f, 0f)
        muteButton.setImageResource(R.drawable.ic_volume_off)
      } else {
        setVolume(1f, 1f)
        muteButton.setImageResource(R.drawable.ic_volume_on)
      }
    }
  }

  private fun setVolume(leftVolume: Float, rightVolume: Float){
    try {
      val mediaPlayerField = VideoView::class.java.getDeclaredField("mMediaPlayer")
      mediaPlayerField.isAccessible = true
      val mediaPlayer = mediaPlayerField.get(videoView) as MediaPlayer

      mediaPlayer.setVolume(leftVolume, rightVolume)
    } catch (e: NoSuchFieldException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    }
  }

  fun releasePlayer() {
      videoView.stopPlayback()
  }
}