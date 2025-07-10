package me.tasy5kg.cutegif.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv.pic.exo.video.VideoPlayerActivity
import me.tasy5kg.cutegif.activity.WebActivity
import me.tasy5kg.cutegif.databinding.FragmentVideoBinding
import me.tasy5kg.cutegif.toolbox.Toolbox.toast

class VideoFragment: BaseFragment() {
  private lateinit var binding: FragmentVideoBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentVideoBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnHistory.setOnClickListener{ TextListDialogFragment.show(requireActivity()) }

    binding.btnGo.setOnClickListener {
      if(binding.chipGotoWeb.isChecked) {
        WebActivity.start(requireActivity(), binding.urlInput.text.toString())
      } else if (binding.chipGotoVideo.isChecked){
        VideoPlayerActivity.start(requireActivity(), binding.urlInput.text.toString())
      } else {
        toast("请选择其他两项")
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
  }

  override fun getFragmentPosition(): Int {
    return 1
  }
}