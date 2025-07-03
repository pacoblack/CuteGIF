package com.cv.pic.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
  entities = [
    User::class,
    Product::class,
    // 添加其他实体...
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun productDao(): ProductDao
  // 添加其他DAO访问方法...

  fun getDao(){

  }
}