package com.cv.pic.db.meta

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

  // 获取所有迁移
  fun getAllMigrations(): Array<Migration> {
    return arrayOf(
      MIGRATION_1_2,
      MIGRATION_2_3,
      MIGRATION_3_2 // 降级示例
    )
  }

  // 版本1到2的迁移
  val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      // 添加新表
      db.execSQL("""
                CREATE TABLE IF NOT EXISTS `settings` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                    `key` TEXT NOT NULL,
                    `value` TEXT
                )
            """.trimIndent())

      // 修改用户表
      db.execSQL("ALTER TABLE users ADD COLUMN avatar_url TEXT")
    }
  }

  // 版本2到3的迁移
  val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
      // 创建临时表
      db.execSQL("""
                CREATE TABLE users_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    created_at INTEGER,
                    avatar_url TEXT,
                    last_login INTEGER
                )
            """.trimIndent())

      // 复制数据
      db.execSQL("""
                INSERT INTO users_new (id, name, email, created_at, avatar_url)
                SELECT id, name, email, created_at, avatar_url FROM users
            """.trimIndent())

      // 删除旧表
      db.execSQL("DROP TABLE users")

      // 重命名新表
      db.execSQL("ALTER TABLE users_new RENAME TO users")
    }
  }

  // 降级处理示例（版本3到2）
  val MIGRATION_3_2 = object : Migration(3, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      // 创建临时表（降级版本）
      db.execSQL("""
                CREATE TABLE users_old (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    created_at INTEGER,
                    avatar_url TEXT
                )
            """.trimIndent())

      // 复制数据（忽略新字段）
      db.execSQL("""
                INSERT INTO users_old (id, name, email, created_at, avatar_url)
                SELECT id, name, email, created_at, avatar_url FROM users
            """.trimIndent())

      // 删除新表
      db.execSQL("DROP TABLE users")

      // 重命名表
      db.execSQL("ALTER TABLE users_old RENAME TO users")

      // 删除设置表
      db.execSQL("DROP TABLE IF EXISTS settings")
    }
  }
}