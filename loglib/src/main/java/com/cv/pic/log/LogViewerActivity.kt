package com.cv.pic.log

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cv.pic.log.databinding.ActivityLogViewerBinding
import java.io.File

class LogViewerActivity : AppCompatActivity() {
  private val binding by lazy { ActivityLogViewerBinding.inflate(layoutInflater) }
  private lateinit var adapter: LogAdapter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    adapter = LogAdapter()

    binding.logRecyclerView.apply {
      layoutManager = LinearLayoutManager(this@LogViewerActivity)
      this.adapter = adapter
    }

    binding.clearButton.setOnClickListener {
      InMemoryTree().clearLogs()
      val logFile = File(filesDir, "app_logs.txt")
      logFile.delete()
      adapter.submitList(emptyList())
    }

    refreshLogs()
  }

  private fun refreshLogs() {
    val logs = InMemoryTree().getLogEntries()
    adapter.submitList(logs)
  }

  companion object{
    fun start(context:Context) {
      context.startActivity(Intent(context, LogViewerActivity::class.java))
    }
  }
}
