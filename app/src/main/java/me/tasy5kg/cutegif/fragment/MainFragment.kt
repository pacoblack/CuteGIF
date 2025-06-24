package me.tasy5kg.cutegif.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import me.tasy5kg.cutegif.MainActivity
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.activity.BetaEndedActivity
import me.tasy5kg.cutegif.activity.GifSplitActivity
import me.tasy5kg.cutegif.activity.GifMergeActivity
import me.tasy5kg.cutegif.activity.GifToVideoActivity
import me.tasy5kg.cutegif.activity.ImportMvimgActivity
import me.tasy5kg.cutegif.activity.VideoToGifActivity
import me.tasy5kg.cutegif.activity.WhatsNewActivity
import me.tasy5kg.cutegif.databinding.FragmentMainBinding
import me.tasy5kg.cutegif.model.MySettings
import me.tasy5kg.cutegif.model.MySettings.INT_FILE_OPEN_WAY_13
import me.tasy5kg.cutegif.model.MySettings.INT_FILE_OPEN_WAY_DOCUMENT
import me.tasy5kg.cutegif.model.MySettings.INT_FILE_OPEN_WAY_GALLERY
import me.tasy5kg.cutegif.toolbox.FileTools.copyToInputFileDir
import me.tasy5kg.cutegif.toolbox.Toolbox.enableDropFile
import me.tasy5kg.cutegif.toolbox.Toolbox.enableDropFiles
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick
import me.tasy5kg.cutegif.toolbox.Toolbox.toast
import java.util.ArrayList

class MainFragment:Fragment() {
  private var binding:FragmentMainBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentMainBinding.inflate(layoutInflater, container, false)
    return binding!!.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }

  private fun importFileTryCatch(function: () -> Unit) {
    try {
      function.invoke()
    } catch (e: Exception) {
      logRed("importFileFailed", e)
      e.printStackTrace()
      activity?.runOnUiThread { toast(R.string.import_file_failed_please_try) }
    }
  }

  private val arlImportVideoToGifDocument = registerForActivityResult(ActivityResultContracts.GetContent()) {
    it?.let { _ -> importFileTryCatch { activity?.let {a-> VideoToGifActivity.start(a, it.copyToInputFileDir()) } } }
  }
  private val arlImportVideoToGifElse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    it?.data?.data?.let { uri -> importFileTryCatch { activity?.let {a->VideoToGifActivity.start(a, uri.copyToInputFileDir()) } } }
  }

  private val arlImportGifSplitDocument = registerForActivityResult(ActivityResultContracts.GetContent()) {
    it?.let { _ -> importFileTryCatch { activity?.let {a->GifSplitActivity.start(a, it.copyToInputFileDir()) } } }
  }
  private val arlImportGifSplit13 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    it?.data?.data?.let { uri -> importFileTryCatch { activity?.let {a->GifSplitActivity.start(a, uri.copyToInputFileDir()) } } }
  }
  private val arlImportGifMergeDocument = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
      uris: List<Uri>? -> importFileTryCatch { activity?.let { GifMergeActivity.start(it, uris) } }
  }
  private val arlImportGifMerge13 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    when{
      it?.data?.clipData != null -> { // 多选
        it.data?.clipData.let { uris ->
          val uriList = ArrayList<Uri>(uris!!.itemCount)
          for (i in 0 until uris.itemCount) {
            uriList.add(uris.getItemAt(i).uri)
          }
          importFileTryCatch { activity?.let { a->GifMergeActivity.start(a, uriList) } } }
      }
      it?.data?.data != null -> { // 单选
        it.data?.data?.let { uri -> {
          val arrayList = ArrayList<Uri>(1)
          arrayList.add(uri)
          importFileTryCatch { activity?.let { a->GifMergeActivity.start(a, arrayList) }}
        } }
      }
    }
  }
  private val arlImportGifToVideoDocument = registerForActivityResult(ActivityResultContracts.GetContent()) {
    it?.let { _ -> importFileTryCatch { activity?.let {a->GifToVideoActivity.start(a, it.copyToInputFileDir()) } } }
  }
  private val arlImportGifToVideo13 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    it?.data?.data?.let { uri -> importFileTryCatch {activity?.let {a-> GifToVideoActivity.start(a, uri.copyToInputFileDir()) } } }
  }
  private val arlImportMvimgToGifDocument = registerForActivityResult(ActivityResultContracts.GetContent()) {
    it?.let { _ -> importFileTryCatch { activity?.let {a->ImportMvimgActivity.start(a, it.copyToInputFileDir()) } } }
  }
  private val arlImportMvimgToGif13 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    it?.data?.data?.let { uri -> importFileTryCatch {activity?.let {a-> ImportMvimgActivity.start(a, uri.copyToInputFileDir()) } } }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding?.mtvBetaInfo?.text = getString(R.string.beta_info, BetaEndedActivity.testVersionRemainingDays())
    activity?.let { a->
      binding?.mcvVideoToGif?.apply {
          onClick { importVideoToGif() }
          enableDropFile(a, "video/*") {
            VideoToGifActivity.start(
              a, it.copyToInputFileDir()
            )
          }
      }
      binding?.mcvGifSplit?.apply {
        onClick { importForGifSplit() }
        enableDropFile(a, "image/gif") {
          GifSplitActivity.start(
            a, it.copyToInputFileDir()
          )
        }
      }

      binding?.mcvGifMerge?.apply {
        onClick { importForGifMerge() }
        enableDropFiles(a, "image/gif") {
          GifMergeActivity.start(a, it)
        }
      }
      binding?.mcvGifToVideo?.apply {
        onClick { importForGifToVideo() }
        enableDropFile(a, "image/gif") {
          GifToVideoActivity.start(
            a, it.copyToInputFileDir()
          )
        }
      }
      binding?.mcvMvimgToVideo?.apply {
        onClick { importForMvimgToGif() }
        enableDropFile(a, "image/jpeg") {
          ImportMvimgActivity.start(
            a, it.copyToInputFileDir()
          )
        }
      }

      @Suppress("DEPRECATION")
      val uriFromActionViewOrSend = activity?.intent?.extras?.getParcelable(Intent.EXTRA_STREAM) ?: activity?.intent?.data
      if (uriFromActionViewOrSend != null) {
        VideoToGifActivity.start(a, uriFromActionViewOrSend.copyToInputFileDir())
      }
      if (!MySettings.whatsNewRead) WhatsNewActivity.start(a)
    }
  }

  private fun importVideoToGif() {
    when (MySettings.fileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportVideoToGifDocument.launch("video/*")
      INT_FILE_OPEN_WAY_GALLERY -> arlImportVideoToGifElse.launch(
        Intent(
        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      ).apply {
        type = "video/*"
      })

      INT_FILE_OPEN_WAY_13 -> arlImportVideoToGifElse.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "video/*"
      })
    }
  }

  private fun importForGifSplit(intFileOpenWay: Int = MySettings.fileOpenWay) {
    when (intFileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportGifSplitDocument.launch("image/gif")
      INT_FILE_OPEN_WAY_13 -> arlImportGifSplit13.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/gif"
      })

      else -> importForGifSplit(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) INT_FILE_OPEN_WAY_13 else INT_FILE_OPEN_WAY_DOCUMENT
      )
    }
  }

  private fun importForGifMerge(intFileOpenWay: Int = MySettings.fileOpenWay){
    when (intFileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportGifMergeDocument.launch("image/gif")
      INT_FILE_OPEN_WAY_13 -> arlImportGifMerge13.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/gif"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
      })

      else -> importForGifMerge(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) INT_FILE_OPEN_WAY_13 else INT_FILE_OPEN_WAY_DOCUMENT
      )
    }
  }

  private fun importForGifToVideo(intFileOpenWay: Int = MySettings.fileOpenWay) {
    when (intFileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportGifToVideoDocument.launch("image/gif")
      INT_FILE_OPEN_WAY_13 -> arlImportGifToVideo13.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/gif"
      })

      else -> importForGifToVideo(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) INT_FILE_OPEN_WAY_13 else INT_FILE_OPEN_WAY_DOCUMENT
      )
    }
  }

  private fun importForMvimgToGif(intFileOpenWay: Int = MySettings.fileOpenWay) {
    when (intFileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportMvimgToGifDocument.launch("image/jpeg")
      INT_FILE_OPEN_WAY_13 -> arlImportMvimgToGif13.launch(Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/jpeg"
      })

      else -> importForMvimgToGif(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) INT_FILE_OPEN_WAY_13 else INT_FILE_OPEN_WAY_DOCUMENT
      )
    }
  }

  companion object {
    fun start(context: Context) {
      context.startActivity(Intent(context, MainActivity::class.java))
    }
  }
}