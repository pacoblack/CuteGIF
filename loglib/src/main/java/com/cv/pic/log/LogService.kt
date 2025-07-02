package com.cv.pic.log

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.an.deviceinfo.device.model.Device
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogService : Service() {

  override fun onCreate() {
    super.onCreate()
    initLogger()
    logDeviceInfo()
  }

  private fun initLogger() {
    // 创建日志目录
    val logDir = File(filesDir, "network_logs")
    if (!logDir.exists()) logDir.mkdirs()

    // 按日期分日志文件
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val logFile = File(logDir, "log_${dateFormat.format(Date())}.log")

    // 配置Timber
    Timber.plant(object : Timber.DebugTree() {
      override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        logToFile("[$tag] $message")
      }
    })
  }

  private fun logDeviceInfo() {
    Device(this).apply {
      Timber.d("Device: ${toJSON()} | OS: Android ${Build.VERSION.SDK_INT}")
    }
  }

  private fun logToFile(message: String) {
    try {
      File(filesDir, "network_logs/latest.log").appendText(
        "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())} $message\n"
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}