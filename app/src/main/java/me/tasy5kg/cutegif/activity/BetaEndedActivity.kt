package me.tasy5kg.cutegif.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityBetaEndedBinding
import me.tasy5kg.cutegif.toolbox.Toolbox.onClick

class BetaEndedActivity : AppCompatActivity() {
  private val binding by lazy { ActivityBetaEndedBinding.inflate(layoutInflater) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.mtvVersionInfo.text = getString(R.string.version_X, BuildConfig.VERSION_NAME)
    binding.mbClose.onClick { finish() }
    binding.mbStartFollowing.onClick {
      FollowWechatActivity.start(this@BetaEndedActivity)
    }
  }

  companion object {

    fun start(context: Context) = context.startActivity(Intent(context, BetaEndedActivity::class.java))

    fun testVersionRemainingDays() = Int.MAX_VALUE /*
      28 - ((System.currentTimeMillis() -
        SimpleDateFormat("yyyyMMdd", Locale.CHINA).parse("20240224")!!.time) / 86400000) */
  }
}