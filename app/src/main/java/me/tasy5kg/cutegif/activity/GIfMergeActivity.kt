package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.tasy5kg.cutegif.model.MyConstants

class GIfMergeActivity : BaseActivity() {
  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    TODO("Not yet implemented")
  }

  companion object {
    fun start(context: Context, gifPath: String) = context.startActivity(
      Intent(
        context, GIfMergeActivity::class.java
      ).putExtra(MyConstants.EXTRA_GIF_PATH, gifPath)
    )
  }
}