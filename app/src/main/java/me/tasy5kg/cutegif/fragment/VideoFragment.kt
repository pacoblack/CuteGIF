package me.tasy5kg.cutegif.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
    binding.Spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {
      }
    }
    binding.btnGo.setOnClickListener {
      when (binding.Spinner.selectedItemPosition) {
        0 -> WebActivity.start(requireActivity(), binding.urlInput.text.toString())
        1 -> VideoPlayerActivity.start(requireActivity(), binding.urlInput.text.toString())
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