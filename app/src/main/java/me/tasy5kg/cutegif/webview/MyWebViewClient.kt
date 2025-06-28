package me.tasy5kg.cutegif.webview

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {

  override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
    request?.url?.let { url ->
      // 处理特定 URL 方案
      if (url.scheme == "myapp") {
        // 处理自定义协议
        handleCustomScheme(url.toString())
        return true
      }

      // 阻止外部链接在 WebView 中打开
      url.host?.contains("yourdomain.com")?.let {
        if (!it == true) {
          view?.context?.startActivity(Intent(Intent.ACTION_VIEW, url))
          return true
        }
      }
    }
    return false
  }

  override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
    super.onPageStarted(view, url, favicon)
    // 显示加载进度
  }

  override fun onPageFinished(view: WebView?, url: String?) {
    super.onPageFinished(view, url)
    // 页面加载完成
  }

  override fun onReceivedError(
    view: WebView?,
    request: WebResourceRequest?,
    error: WebResourceError?
  ) {
    super.onReceivedError(view, request, error)
    // 处理错误
    view?.loadUrl("about:blank")
    view?.loadDataWithBaseURL(
      null,
      "<h1>加载失败</h1><p>${error?.description}</p>",
      "text/html",
      "UTF-8",
      null
    )
  }

  private fun handleCustomScheme(url: String) {
    // 解析自定义协议
    // myapp://action?param1=value1&param2=value2
  }
}