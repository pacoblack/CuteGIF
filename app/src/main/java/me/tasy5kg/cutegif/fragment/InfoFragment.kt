package me.tasy5kg.cutegif.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.tasy5kg.cutegif.databinding.FragmentInfoBinding
import me.tasy5kg.cutegif.webview.MyWebChromeClient
import me.tasy5kg.cutegif.webview.MyWebViewClient
import me.tasy5kg.cutegif.webview.WebAppInterface
import me.tasy5kg.cutegif.webview.WebBridge
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InfoFragment: Fragment() , WebBridge {
  private lateinit var binding: FragmentInfoBinding
  private lateinit var webView: WebView
  private lateinit var webViewClient: MyWebViewClient
  private lateinit var webChromeClient: MyWebChromeClient

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initWebView()
    setupControls()
  }

  private fun initWebView() {
    webView = binding.webView
    webViewClient = MyWebViewClient()
    webChromeClient = MyWebChromeClient(requireActivity())

    // WebView 基本设置
    webView.settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      databaseEnabled = true
      allowFileAccess = true
      allowContentAccess = true
      allowUniversalAccessFromFileURLs = true
      allowFileAccessFromFileURLs = true
      javaScriptCanOpenWindowsAutomatically = true
      setSupportZoom(true)
      builtInZoomControls = true
      displayZoomControls = false
      loadWithOverviewMode = true
      useWideViewPort = true
      mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      cacheMode = WebSettings.LOAD_DEFAULT
    }

    // 添加 JavaScript 接口
    webView.addJavascriptInterface(WebAppInterface(this, this), "Android")

    // 设置客户端
    webView.webViewClient = webViewClient
    webView.webChromeClient = webChromeClient

    // 加载页面
    webView.loadUrl("file:///android_asset/index.html")
  }

  private fun setupControls() {
    binding.btnSendToWeb.setOnClickListener {
      val message = binding.etMessage.text.toString().trim()
      if (message.isNotEmpty()) {
        sendMessageToWebView(message)
        binding.etMessage.setText("")
      }
    }

    binding.btnExecuteJs.setOnClickListener {
      val jsCode = binding.etJsCode.text.toString().trim()
      if (jsCode.isNotEmpty()) {
        executeJavaScript(jsCode)
        binding.etJsCode.setText("")
      }
    }
  }

  // 发送消息到 WebView
  private fun sendMessageToWebView(message: String) {
    // 使用 JSON 编码确保特殊字符正确处理
    val jsonMessage = JSONObject().apply {
      put("message", message)
      put("timestamp", System.currentTimeMillis())
    }.toString()

    webView.evaluateJavascript("receiveMessageFromApp(`${escapeJsString(jsonMessage)}`)", null)

    // 在应用界面显示发送的消息
    addMessageToLog("App → WebView: $message")
  }

  private fun escapeJsString(input: String): String {
    return input
      .replace("\\", "\\\\")  // 反斜杠
      .replace("'", "\\'")     // 单引号
      .replace("\"", "\\\"")   // 双引号
      .replace("\n", "\\n")    // 换行符
      .replace("\r", "\\r")    // 回车符
  }

  // 执行任意 JavaScript 代码
  private fun executeJavaScript(code: String) {
    webView.evaluateJavascript(code) { result ->
      addMessageToLog("JS Result: $result")
    }
  }

  // 接收来自 WebView 的消息
  override fun receiveMessageFromWeb(message: String) {
    activity?.runOnUiThread {
      addMessageToLog("WebView → App: $message")
    }
  }

  // 在日志视图中添加消息
  private fun addMessageToLog(message: String) {
    AlertDialog.Builder(requireActivity())
      .setTitle("弹窗")
      .setMessage(message)
      .setPositiveButton("OK", null)
      .show()
  }

  override fun onDestroy() {
    webView.apply {
      stopLoading()
      webChromeClient = null
      removeJavascriptInterface("Android")
      destroy()
    }
    super.onDestroy()
  }

}