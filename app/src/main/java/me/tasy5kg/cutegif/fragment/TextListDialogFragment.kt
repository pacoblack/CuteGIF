package me.tasy5kg.cutegif.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import me.tasy5kg.cutegif.databinding.FragmentDialogHistoryBinding
import me.tasy5kg.cutegif.fragment.TextListAdapter.TextActionListener

class TextListDialogFragment : DialogFragment(), TextActionListener {
  private lateinit var binding: FragmentDialogHistoryBinding
  private var textItems: MutableList<String?>? = null
  private var adapter: TextListAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //TODO:加载数据
    // 确保有数据
    if (textItems == null) {
      textItems = ArrayList<String?>()
    }
    textItems?.add("123123123123")
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    // 请求一个无标题栏的窗口
    if (dialog.window != null) {
      dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
    }
    return dialog
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDialogHistoryBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // 设置RecyclerView
    val recyclerView = binding.textRecyclerView
    recyclerView.setLayoutManager(LinearLayoutManager(context))
    adapter = TextListAdapter(textItems!!, this)
    recyclerView.setAdapter(adapter)
  }

  override fun onStart() {
    super.onStart()
    // 设置对话框尺寸
    val dialog = getDialog()
    if (dialog != null && dialog.window != null) {
      val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
      val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
      dialog.window!!.setLayout(width, height)
    }
  }

  override fun onDeleteItem(position: Int) {
    // TODO:删除数据
    if (position >= 0 && position < textItems!!.size) {
      // 删除项目
      textItems!!.removeAt(position)
      adapter!!.notifyItemRemoved(position)

      // 如果没有项目了，关闭对话框
      if (textItems!!.isEmpty()) {
        dismiss()
      }
    }
  }

  companion object {

    fun show(activity: FragmentActivity){
      val dialog = TextListDialogFragment()

      val fragmentManager: FragmentManager = activity.supportFragmentManager
      dialog.show(fragmentManager, "TextListDialog")
    }

  }
}