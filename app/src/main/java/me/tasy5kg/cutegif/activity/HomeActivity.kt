package me.tasy5kg.cutegif.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.tasy5kg.cutegif.R


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navView = findViewById<BottomNavigationView>(R.id.navigation)

    // 找到NavHostFragment并获取NavController
    val navHostFragment = supportFragmentManager
      .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
    val navController = navHostFragment!!.navController


    // 使用AppBarConfiguration来配置顶层目的地，如果有的话
    val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder(
      R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
    ).build()


    // 将BottomNavigationView与NavController连接
    setupWithNavController(navView, navController)
  }

}