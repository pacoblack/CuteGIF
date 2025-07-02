package com.cv.pic.ai.deepseek

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cv.pic.ai.databinding.ActivityDeepseekBinding
import com.cv.pic.ai.deepseek.viewmodel.DeepSeekViewModel
import com.cv.pic.ai.deepseek.viewmodel.Message
import com.cv.pic.mvvm.core.NetworkResult
import kotlinx.coroutines.launch

class DeepSeekActivity : AppCompatActivity() {
  private val viewModel: DeepSeekViewModel by viewModels()
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
      }
    }
    viewModelWatch()
  }

  private fun viewModelWatch(){
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.chatState.collect { result ->
          when (result) {
            is NetworkResult.Loading -> Log.e(TAG, "login is Loading")
            is NetworkResult.Success -> {
              Log.e(TAG, "登录成功")
            }
            is NetworkResult.Error -> {
              Log.e(TAG, "登录失败: ${result.message}")
            }
          }
        }
      }
    }

  }

  private fun addMessage(role: String, content: String) {
    messages.add(Message(role, content))
    adapter.submitList(messages.toList())
    binding.recyclerView.smoothScrollToPosition(messages.size - 1)
  }

  companion object {
    const val TAG = "DeepSeekActivity"
    fun start(context: Context) {
      context.startActivity(Intent(context, DeepSeekActivity::class.java))
    }
  }
}