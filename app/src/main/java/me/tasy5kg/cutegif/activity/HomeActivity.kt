package me.tasy5kg.cutegif.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.components.FragmentAdapter
import me.tasy5kg.cutegif.databinding.ActivityHomeBinding
import me.tasy5kg.cutegif.fragment.InfoFragment
import me.tasy5kg.cutegif.fragment.MainFragment
import me.tasy5kg.cutegif.fragment.VideoFragment


class HomeActivity : BaseActivity() {
  private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    setSupportActionBar(binding.materialToolbar)
    binding.materialToolbar.subtitle = getString(R.string.version_X, BuildConfig.VERSION_NAME)

    setupViewPager();

    binding.navigation.setOnItemSelectedListener { item ->
      var position = 0
      when(item.itemId) {
        R.id.mainFragment -> position = 0
        R.id.videoFragment -> position = 1
        R.id.infoFragment -> position = 2
      }
      binding.viewPager.setCurrentItem(position, true)
      true
    }

  }
  private fun setupViewPager() {
    val fragments: MutableList<Fragment> = ArrayList<Fragment>()
    fragments.add(MainFragment())
    fragments.add(VideoFragment())
    fragments.add(InfoFragment())

    val adapter = FragmentAdapter(this, fragments)
    binding.viewPager.setAdapter(adapter)

    // 设置预加载页面数
    binding.viewPager.setOffscreenPageLimit(3)
    binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        binding.navigation.selectedItemId = when(position) {
          0 -> R.id.mainFragment
          1 ->  R.id.videoFragment
          2 -> R.id.infoFragment
          else -> R.id.mainFragment
        }
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.toolbar_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_item_about -> AboutActivity.start(this)
    }
    return true
  }

}