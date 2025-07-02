package com.cv.pic.log

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cv.pic.log.databinding.ActivityLogViewerBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class LogViewerActivity : AppCompatActivity() {
  private val binding by lazy { ActivityLogViewerBinding.inflate(layoutInflater) }
  private lateinit var adapter: LogAdapter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    adapter = LogAdapter()

    binding.logRecyclerView.apply {
      layoutManager = LinearLayoutManager(this@LogViewerActivity)
      adapter = this@LogViewerActivity.adapter
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
    val logFile = File(filesDir, "app_logs.txt")
    if (!logFile.exists()) {
      adapter.submitList(emptyList())
      return
    }

    // 在后台线程中读取文件，避免阻塞主线程
    Thread {
      val lines = mutableListOf<String>()
      try {
        FileReader(logFile).use { reader ->
          BufferedReader(reader).use { bufferedReader ->
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
              lines.add(line!!)
            }
          }
        }

        // 将日志行解析为 LogEntry
        val logs = lines.mapNotNull { line ->
          parseLogLine(line)
        }

        runOnUiThread {
          adapter.submitList(logs)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        runOnUiThread {
          adapter.submitList(emptyList())
        }
      }
    }.start()
  }

  private fun parseLogLine(line: String): LogEntry? {
    // 示例格式：[2024-11-01 14:30:00] [TAG] This is a log message
    val pattern = """^\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})\] \[(\w+)\] (.+)$""".toRegex()
    val matchResult = pattern.matchEntire(line) ?: return null

    val timestamp = matchResult.groupValues[1]
    val tag = matchResult.groupValues[2]
    val message = matchResult.groupValues[3]

    return LogEntry(
      timestamp = timestamp,
      tag = tag,
      message = message,
      priority = Log.INFO // 可根据实际情况扩展
    )
  }

  companion object{
    fun start(context:Context) {
      context.startActivity(Intent(context, LogViewerActivity::class.java))
    }
  }
}
