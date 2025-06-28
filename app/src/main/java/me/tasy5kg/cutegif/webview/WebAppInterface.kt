package me.tasy5kg.cutegif.webview

import android.os.Build
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.json.JSONObject

class WebAppInterface(private val fragment: Fragment, private val bridge: WebBridge) {

  // 暴露给 JavaScript 的方法（接收消息）
  @JavascriptInterface
  fun sendMessageToApp(message: String) {
    bridge.receiveMessageFromWeb(message)
  }

  // 获取设备信息
  @JavascriptInterface
  fun getDeviceInfo(): String {
    return JSONObject().apply {
      put("model", Build.MODEL)
      put("manufacturer", Build.MANUFACTURER)
      put("osVersion", Build.VERSION.RELEASE)
      put("appVersion", fragment.activity?.packageManager?.getPackageInfo(fragment.requireActivity().packageName, 0)?.versionName)
    }.toString()
  }

  // 显示原生 Toast
  @JavascriptInterface
  fun showToast(message: String) {
    Toast.makeText(fragment.activity, message, Toast.LENGTH_SHORT).show()
  }

  // 打开原生对话框
  @JavascriptInterface
  fun openDialog(title: String, message: String) {
    fragment.activity?.runOnUiThread {
      AlertDialog.Builder(fragment.requireActivity())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
    }
  }
}