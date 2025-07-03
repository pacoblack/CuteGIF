package com.cv.pic.db

import android.content.Context
import com.cv.pic.db.core.DatabaseManager
import com.cv.pic.db.core.KeyManager
import com.cv.pic.db.meta.DatabaseMigrations
import com.cv.pic.db.meta.setting.SettingsRepository
import com.cv.pic.db.meta.user.UserRepository

class Demo {
  private fun initDatabase(context:Context) {
    // 获取数据库密钥
    val passphrase = KeyManager.getOrCreateDatabasePassphrase(context)

    // 初始化数据库
    DatabaseManager.initialize(
      context = context,
      dbClass = AppDatabase::class.java,
      dbName = "secure_database.db",
      passphrase = passphrase,
      migrations = DatabaseMigrations.getAllMigrations(),
      version = 3
    )

    // 获取数据库实例
    val database = DatabaseManager.getDatabase<AppDatabase>("secure_database.db")

    // 注册仓库
    DatabaseManager.registerRepository(
      UserRepository::class.java,
      UserRepository(database.userDao())
    )

    DatabaseManager.registerRepository(
      SettingsRepository::class.java,
      SettingsRepository(database.settingsDao())
    )
  }
}