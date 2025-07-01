package com.cv.pic.ai.deepseek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cv.pic.ai.databinding.ItemChatMessageBinding
import com.cv.pic.ai.deepseek.viewmodel.Message
import io.noties.markwon.Markwon

// ChatAdapter.kt
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

  private val items = mutableListOf<Message>()
  private lateinit var markwon: Markwon

  fun setMarkwon(markwon: Markwon) {
    this.markwon = markwon
  }

  fun submitList(newItems: List<Message>) {
    items.clear()
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount() = items.size

  inner class ViewHolder(viewBinding: ItemChatMessageBinding) : RecyclerView.ViewHolder(viewBinding.root) {
    private val tvContent = viewBinding.tvContent
    private val tvRole = viewBinding.tvRole

    fun bind(message: Message) {
      tvRole.text = when (message.role) {
        "user" -> "👤 用户"
        else -> "🤖 DeepSeek"
      }

      // Markdown 渲染
      markwon.setMarkdown(tvContent, message.content)
    }
  }
}