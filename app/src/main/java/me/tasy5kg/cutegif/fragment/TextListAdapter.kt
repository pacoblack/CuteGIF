package me.tasy5kg.cutegif.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import me.tasy5kg.cutegif.databinding.ItemHistoryLineBinding

class TextListAdapter(private val textItems: MutableList<String?>, private val listener: TextActionListener?) :
  RecyclerView.Adapter<TextListAdapter.ViewHolder?>() {
    private lateinit var binding: ItemHistoryLineBinding
  interface TextActionListener {
    fun onDeleteItem(position: Int)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    binding = ItemHistoryLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val text = textItems[position]
    holder.textView.text = text

    // 复制按钮点击事件
    holder.copyButton.setOnClickListener(View.OnClickListener { v: View? ->
      val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("Text", text)
      clipboard.setPrimaryClip(clip)
      Toast.makeText(
        holder.itemView.context,
        "已复制: $text", Toast.LENGTH_SHORT
      ).show()
    })

    // 删除按钮点击事件
    holder.deleteButton.setOnClickListener(View.OnClickListener { v: View? ->
      listener?.onDeleteItem(position)
    })
  }

  override fun getItemCount(): Int {
    return textItems.size
  }

  class ViewHolder(binding: ItemHistoryLineBinding) : RecyclerView.ViewHolder(binding.root) {
    var textView: TextView = binding.textContent
    var copyButton: ImageButton = binding.copyButton
    var deleteButton: ImageButton = binding.deleteButton
  }
}