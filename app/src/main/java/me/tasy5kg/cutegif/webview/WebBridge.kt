package me.tasy5kg.cutegif.webview

interface WebBridge {
  fun receiveMessageFromWeb(message: String)
}