package me.tasy5kg.cutegif.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.R
import me.tasy5kg.cutegif.databinding.ActivityHomeBinding


class HomeActivity : BaseActivity() {
  private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

  override fun onCreateIfEulaAccepted(savedInstanceState: Bundle?) {
    setContentView(binding.root)
    setSupportActionBar(binding.materialToolbar)
    binding.materialToolbar.subtitle = getString(R.string.version_X, BuildConfig.VERSION_NAME)
    // 找到NavHostFragment并获取NavController
    val navHostFragment = supportFragmentManager
      .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
    val navController = navHostFragment!!.navController


    // 使用AppBarConfiguration来配置顶层目的地，如果有的话
    val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder(
      R.id.mainFragment, R.id.videoFragment, R.id.infoFragment
    ).build()


    // 将BottomNavigationView与NavController连接
    setupWithNavController(binding.navigation, navController)
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