package me.tasy5kg.cutegif.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv.pic.ai.deepseek.DeepSeekActivity
import com.cv.pic.log.LogViewerActivity
import me.tasy5kg.cutegif.databinding.FragmentOtherBinding
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick

class InfoFragment: BaseFragment()  {
  private lateinit var binding: FragmentOtherBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentOtherBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.mcvDeepSeek.onClick { DeepSeekActivity.start(requireActivity()) }
    binding.mcvLog.onClick { LogViewerActivity.start(requireActivity()) }
  }


  override fun getFragmentPosition(): Int {
    return 2
  }

}