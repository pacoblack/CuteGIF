package me.tasy5kg.cutegif.webview

import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class MyWebChromeClient(private val activity: Activity) : WebChromeClient() {

  // 处理 JavaScript 弹窗
  override fun onJsAlert(
    view: WebView,
    url: String,
    message: String,
    result: JsResult
  ): Boolean {
    AlertDialog.Builder(activity)
      .setTitle("提示")
      .setMessage(message)
      .setPositiveButton("确定") { _, _ -> result.confirm() }
      .setCancelable(false)
      .create()
      .show()
    return true
  }

  override fun onJsConfirm(
    view: WebView?,
    url: String?,
    message: String?,
    result: JsResult?
  ): Boolean {
    AlertDialog.Builder(activity)
      .setTitle("确认")
      .setMessage(message)
      .setPositiveButton("确定") { _, _ -> result?.confirm() }
      .setNegativeButton("取消") { _, _ -> result?.cancel() }
      .setCancelable(false)
      .create()
      .show()
    return true
  }

  override fun onJsPrompt(
    view: WebView?,
    url: String?,
    message: String?,
    defaultValue: String?,
    result: JsPromptResult?
  ): Boolean {
    val input = EditText(activity).apply {
      setText(defaultValue)
    }

    AlertDialog.Builder(activity)
      .setTitle(message)
      .setView(input)
      .setPositiveButton("确定") { _, _ ->
        result?.confirm(input.text.toString())
      }
      .setNegativeButton("取消") { _, _ -> result?.cancel() }
      .show()

    return true
  }

  // 处理文件上传
  override fun onShowFileChooser(
    webView: WebView,
    filePathCallback: ValueCallback<Array<Uri>>?,
    fileChooserParams: FileChooserParams?
  ): Boolean {
    val intent = fileChooserParams?.createIntent() ?: return false
    try {
      activity.startActivityForResult(intent, REQUEST_FILE_CHOOSER)
    } catch (e: ActivityNotFoundException) {
      return false
    }
    return true
  }

  // 显示自定义视图（全屏视频等）
  override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
    // 实现全屏处理
  }

  override fun onHideCustomView() {
    // 退出全屏
  }

  companion object {
    const val REQUEST_FILE_CHOOSER = 1001
  }
}