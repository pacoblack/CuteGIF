package com.cv.pic.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * 集中式数据库管理器
 * 所有实体和DAO都在App模块中统一注册
 */
object DatabaseManager {

  // 数据库实例
  private lateinit var appDatabase: AppDatabase

  // DAO缓存
  private val daoCache = ConcurrentHashMap<Class<*>, Any>()

  // 数据库配置
  data class Config(
    val name: String = "app_database.db",
    val version: Int = 1,
    val debug: Boolean = false,
    val migrations: List<Migration> = emptyList(),
    val onCreate: ((SupportSQLiteDatabase) -> Unit)? = null,
    val onOpen: ((SupportSQLiteDatabase) -> Unit)? = null
  )

  /**
   * 初始化数据库
   * @param context 应用上下文
   * @param config 数据库配置
   */
  fun initialize(context: Context, config: Config) {
    if (::appDatabase.isInitialized) return

    val builder = Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java,
      config.name
    )

    // 设置迁移
    config.migrations.forEach { builder.addMigrations(it) }

    // 设置回调
    builder.addCallback(object : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadExecutor().execute {
          config.onCreate?.invoke(db)
        }
      }

      override fun onOpen(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadExecutor().execute {
          config.onOpen?.invoke(db)
        }
      }
    })

    if (config.debug) {
      builder.allowMainThreadQueries()
    }

    appDatabase = builder.build()
  }

  /**
   * 获取DAO实例
   * @param daoClass DAO接口的Class对象
   * @return DAO实例
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> getDao(daoClass: Class<T>): T {
    checkInitialized()

    return daoCache.getOrPut(daoClass) {
      when (daoClass) {
        UserDao::class.java -> appDatabase.userDao()
        ProductDao::class.java -> appDatabase.productDao()
        // 添加其他DAO...
        else -> throw IllegalArgumentException("DAO not registered: $daoClass")
      }
    } as T
  }

  /**
   * 关闭数据库
   */
  fun close() {
    if (::appDatabase.isInitialized) {
      appDatabase.close()
      daoCache.clear()
    }
  }

  // ================= 内部方法 =================

  private fun checkInitialized() {
    if (!::appDatabase.isInitialized) {
      throw IllegalStateException("DatabaseManager not initialized! Call initialize() first.")
    }
  }
}