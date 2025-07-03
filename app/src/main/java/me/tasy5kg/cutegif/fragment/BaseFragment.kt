package me.tasy5kg.cutegif.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.components.OnSelectFragment
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed
import me.tasy5kg.cutegif.toolbox.Toolbox.toast


abstract class BaseFragment: Fragment() {
  private val sharedViewModel: SharedViewModel by lazy { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
  private var selectFragment: OnSelectFragment? = null
  fun setSelectFragment(select: OnSelectFragment) {
    this.selectFragment = select
  }
  fun callFragment(page:Int, data:String){
    this.selectFragment?.goto(page, data)
  }
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // 观察数据变化
    sharedViewModel.getPositionData().observe(viewLifecycleOwner) { data ->
      if (data != null) {
        onDataChange(data.get(getFragmentPosition()).toString())
      }
    }
  }
  abstract fun getFragmentPosition():Int
  open fun onDataChange(data:String){}

  open fun importFileTryCatch(function: () -> Unit) {
    try {
      function.invoke()
    } catch (e: Exception) {
      logRed("importFileFailed", e)
      e.printStackTrace()
      activity?.runOnUiThread { toast(R.string.import_file_failed_please_try) }
    }
  }

}