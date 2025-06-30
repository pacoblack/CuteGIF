package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityWebBinding
import me.tasy5kg.cutegif.fragment.WebFragment
import me.tasy5kg.cutegif.fragment.WebFragment.Companion.FRAGMENT_WEB_URL
import me.tasy5kg.cutegif.toolbox.Toolbox.getExtra
import me.tasy5kg.cutegif.toolbox.Toolbox.logRed

class WebActivity: BaseActivity() {
  private val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
  private val webUrl by lazy { intent.getExtra<String>(FRAGMENT_WEB_URL) }
  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)

    // 显示返回按钮
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    // 添加 Fragment
    var fragment = WebFragment()
    fragment.arguments = Bundle().apply { putString(FRAGMENT_WEB_URL, webUrl) }
    supportFragmentManager.commit {
      add(R.id.fragment_container, fragment)
      setReorderingAllowed(true)
    }

    toolbar.setNavigationOnClickListener {
      onBackPressedDispatcher.onBackPressed()
    }
  }

  companion object {
    fun start(context: Context, url: String){
      context.startActivity(Intent(context, WebActivity::class.java).putExtra(
        FRAGMENT_WEB_URL, url
      ))
    }
  }
}