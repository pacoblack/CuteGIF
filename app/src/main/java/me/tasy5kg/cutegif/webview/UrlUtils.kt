package me.tasy5kg.cutegif.webview

object UrlUtils {
  /**
   * 规范化 URL，自动补充协议头
   *
   * @param url 原始 URL 字符串
   * @return 规范化的 URL（包含协议头）
   */
  fun normalizeUrl(url: String?): String {
    var url = url
    if (url == null || url.trim { it <= ' ' }.isEmpty()) {
      return ""
    }

    // 去除首尾空格
    url = url.trim { it <= ' ' }

    // 如果已包含协议头，直接返回
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url
    }

    // 如果以 "//" 开头，补充为 https:
    if (url.startsWith("//")) {
      return "https:$url"
    }

    // 处理没有协议头的域名
    if (url.contains(".") && !url.contains(" ")) {
      // 检查是否包含路径
      val hasPath = url.contains("/") && url.indexOf("/") < url.indexOf(".")

      // 优先使用 https
      return "https://$url"
    }

    // 其他情况默认添加 https://
    return "https://$url"
  }
}