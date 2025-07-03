package com.cv.pic.db.core

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cv.pic.db.AppDatabase
import java.util.concurrent.ConcurrentHashMap
import net.sqlcipher.database.SupportFactory

object DatabaseManager {

  private val databaseCache = ConcurrentHashMap<String, RoomDatabase>()
  private val repositoryCache = ConcurrentHashMap<Class<*>, Any>()

  // 初始化数据库
  fun <T : RoomDatabase> initialize(
    context: Context,
    dbClass: Class<T>,
    dbName: String,
    passphrase: ByteArray,
    migrations: Array<Migration> = emptyArray(),
    version: Int = 1
  ) {
    val factory = SupportFactory(passphrase)

    val database = Room.databaseBuilder(
      context.applicationContext,
      dbClass,
      dbName
    )
      .openHelperFactory(factory) // 使用SQLCipher
      .addMigrations(*migrations)
      .addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
          super.onCreate(db)
          // 数据库创建时执行
          enableSecurityFeatures(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
          super.onOpen(db)
          // 数据库打开时执行
          enableSecurityFeatures(db)
        }
      })
      .fallbackToDestructiveMigrationOnDowngrade()
      .build()

    databaseCache[dbName] = database
  }

  // 启用安全特性
  private fun enableSecurityFeatures(db: SupportSQLiteDatabase) {
    db.execSQL("PRAGMA secure_delete = ON;")
    db.execSQL("PRAGMA auto_vacuum = FULL;")
    db.execSQL("PRAGMA journal_mode = WAL;")
    db.execSQL("PRAGMA cipher_page_size = 4096;")
    db.execSQL("PRAGMA kdf_iter = 64000;")
  }

  // 获取数据库实例
  @Suppress("UNCHECKED_CAST")
  fun <T : RoomDatabase> getDatabase(dbName: String): T {
    return databaseCache[dbName] as? T ?: throw IllegalStateException(
      "Database $dbName not initialized. Call initialize() first."
    )
  }

  // 注册仓库
  fun <T> registerRepository(clazz: Class<T>, repository: T) {
    repositoryCache[clazz] = repository as Any
  }

  // 获取仓库
  @Suppress("UNCHECKED_CAST")
  fun <T> getRepository(clazz: Class<T>): T {
    return repositoryCache[clazz] as? T ?: throw IllegalArgumentException(
      "Repository not registered: ${clazz.simpleName}"
    )
  }

  // 重新加密数据库（更改密码）
  fun rekeyDatabase(context: Context, dbName: String, oldPassphrase: ByteArray, newPassphrase: ByteArray) {
    val dbFile = context.getDatabasePath(dbName)
    if (!dbFile.exists()) return

    // 打开旧数据库
    val oldFactory = SupportFactory(oldPassphrase)
    val db = Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java,
      dbName
    )
      .openHelperFactory(oldFactory)
      .build()

    // 执行重新加密
    db.query("PRAGMA rekey = ?", arrayOf(newPassphrase))
    db.close()

    // 重新初始化新密码的数据库
    initialize(context, AppDatabase::class.java, dbName, newPassphrase)
  }
}