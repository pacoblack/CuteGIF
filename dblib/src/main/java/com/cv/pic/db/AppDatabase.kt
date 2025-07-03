package com.cv.pic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cv.pic.db.meta.setting.Settings
import com.cv.pic.db.meta.setting.SettingsDao
import com.cv.pic.db.meta.user.User
import com.cv.pic.db.meta.user.UserDao

@Database(
  entities = [User::class,Settings::class],
  version = 3,
  exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun settingsDao(): SettingsDao
}