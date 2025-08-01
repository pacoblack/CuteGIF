package me.tasy5kg.cutegif.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.cv.pic.ai.deepseek.DeepSeekActivity
import com.cv.pic.face.generation.ImageGenerationActivity
import com.cv.pic.log.LogViewerActivity
import me.tasy5kg.cutegif.activity.GifMergeActivity
import me.tasy5kg.cutegif.databinding.FragmentOtherBinding
import me.tasy5kg.cutegif.model.MySettings
import me.tasy5kg.cutegif.model.MySettings.INT_FILE_OPEN_WAY_13
import me.tasy5kg.cutegif.model.MySettings.INT_FILE_OPEN_WAY_DOCUMENT
import me.tasy5kg.cutegif.model.MySettings.MAX_SELECT_FILE
import me.tasy5kg.cutegif.toolbox.Toolbox.enableDropFiles
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick

class InfoFragment: BaseFragment()  {
  private lateinit var binding: FragmentOtherBinding

  private val arlImportGifMergeDocument = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
      uris: List<Uri>? -> importFileTryCatch { activity?.let { GifMergeActivity.start(it, uris) } }
  }
  private val arlImportGifMerge13 = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_SELECT_FILE)) {
      uris: List<Uri>? ->importFileTryCatch { activity?.let { GifMergeActivity.start(it, uris) } }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentOtherBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding?.mcvGifMerge?.apply {
      onClick { importForGifMerge() }
      enableDropFiles(requireActivity(), "image/gif") {
        GifMergeActivity.start(requireActivity(), it)
      }
    }
    binding.mcvImageGen.onClick { ImageGenerationActivity.start(requireActivity()) }
    binding.mcvDeepSeek.onClick { DeepSeekActivity.start(requireActivity()) }
    binding.mcvLog.onClick { LogViewerActivity.start(requireActivity()) }

  }

  private fun importForGifMerge(intFileOpenWay: Int = MySettings.fileOpenWay){
    when (intFileOpenWay) {
      INT_FILE_OPEN_WAY_DOCUMENT -> arlImportGifMergeDocument.launch("*/*")
      INT_FILE_OPEN_WAY_13 -> arlImportGifMerge13.launch(
        PickVisualMediaRequest(
        mediaType = ActivityResultContracts.PickVisualMedia.SingleMimeType("*/*")
      )
      )

      else -> importForGifMerge(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) INT_FILE_OPEN_WAY_13 else INT_FILE_OPEN_WAY_DOCUMENT
      )
    }
  }


  override fun getFragmentPosition(): Int {
    return 2
  }

}