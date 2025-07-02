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
  private lateinit var recyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    recyclerView = binding.logRecyclerView
    setupRecyclerView()
  }

  private fun setupRecyclerView() {
    recyclerView.layoutManager = LinearLayoutManager(this)

    // 读取日志文件
    val logFile = File(filesDir, "network_logs/latest.log")
    val logs = if (logFile.exists()) logFile.readLines().reversed() else emptyList()

    recyclerView.adapter = LogAdapter(logs)
  }

  companion object{
    fun start(context:Context) {
      context.startActivity(Intent(context, LogViewerActivity::class.java))
    }
  }
}

class LogAdapter(private val logs: List<String>) : RecyclerView.Adapter<LogViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false))

  override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
    holder.bind(logs[position])
  }

  override fun getItemCount() = logs.size
}

class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  private val textView: TextView = view.findViewById(R.id.logText)

  fun bind(log: String) {
    textView.text = log
    // 高亮错误日志
    textView.setTextColor(if (log.contains("ERROR", true)) Color.RED else Color.BLACK)
  }
}