package com.cv.pic.ai.deepseek

import android.content.Context
import io.noties.markwon.Markwon

// MarkdownUtils.kt
object MarkdownUtils {
  fun createMarkwon(context: Context): Markwon {
    return Markwon.builder(context)
      .build()
  }
}