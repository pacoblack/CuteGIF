package com.cv.pic.ai.deepseek

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.cv.pic.ai.databinding.ActivityDeepseekBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// MainActivity.kt
class DeepSeekActivity : AppCompatActivity() {

  private lateinit var binding: ActivityDeepseekBinding
  private val adapter = ChatAdapter()
  private val messages = mutableListOf<Message>()
  private val apiKey = "your_deepseek_api_key" // 替换为你的API密钥

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDeepseekBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // 初始化Markdown渲染器
    val markwon = MarkdownUtils.createMarkwon(this)
    adapter.setMarkwon(markwon)

    // 设置RecyclerView
    binding.recyclerView.apply {
      layoutManager = LinearLayoutManager(this@DeepSeekActivity)
      adapter = this@DeepSeekActivity.adapter
    }

    // 发送按钮点击事件
    binding.btnSend.setOnClickListener {
      val userInput = binding.etInput.text.toString().trim()
      if (userInput.isNotEmpty()) {
        addMessage("user", userInput)
        binding.etInput.setText("")
        getDeepSeekResponse(userInput)
      }
    }
  }

  private fun addMessage(role: String, content: String) {
    messages.add(Message(role, content))
    adapter.submitList(messages.toList())
    binding.recyclerView.smoothScrollToPosition(messages.size - 1)
  }

  private fun getDeepSeekResponse(query: String) {
    lifecycleScope.launch(Dispatchers.IO) {
      try {
        val response = RetrofitClient.instance.chatCompletion(
          token = "Bearer $apiKey",
          request = ChatRequest(messages = listOf(
            Message("user", query)
          ))
        )

        withContext(Dispatchers.Main) {
          response.choices.firstOrNull()?.let {
            addMessage("assistant", it.message.content)
          }
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          addMessage("assistant", "❌ 请求失败: ${e.localizedMessage}")
        }
      }
    }
  }

  companion object {
    fun start(context: Context) {
      context.startActivity(Intent(context, DeepSeekActivity::class.java))
    }
  }
}