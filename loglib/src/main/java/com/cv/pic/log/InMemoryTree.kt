package com.cv.pic.log

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InMemoryTree : Timber.Tree() {
  private val logEntries = mutableListOf<LogEntry>()

  override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
    val logMessage = if (throwable == null) message else "$message\n${throwable.stackTraceToString()}"
    val entry = LogEntry(
      timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
      tag = tag ?: "NO_TAG",
      message = logMessage,
      priority = priority
    )
    synchronized(logEntries) {
      logEntries.add(entry)
      if (logEntries.size > 1000) logEntries.removeAt(0)
    }
  }

  fun getLogEntries(): List<LogEntry> = synchronized(logEntries) { logEntries.toList() }

  fun clearLogs() {
    synchronized(logEntries) {
      logEntries.clear()
    }
  }
}