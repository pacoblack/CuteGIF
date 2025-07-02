package com.cv.pic.log

data class LogEntry(
  val timestamp: String,
  val tag: String,
  val message: String,
  val priority: Int
)