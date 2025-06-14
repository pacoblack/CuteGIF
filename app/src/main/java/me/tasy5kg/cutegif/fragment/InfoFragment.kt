package me.tasy5kg.cutegif.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.tasy5kg.cutegif.databinding.FragmentInfoBinding

class InfoFragment: Fragment() {
  private var binding: FragmentInfoBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
    return binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (arguments != null) {

    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}