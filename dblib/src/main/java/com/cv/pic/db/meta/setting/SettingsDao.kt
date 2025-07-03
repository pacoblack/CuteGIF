package com.cv.pic.db.meta.setting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cv.pic.db.meta.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(setting: Settings): Long

  @Update
  suspend fun update(setting: Settings)

  @Delete
  suspend fun delete(setting: Settings)

  @Query("SELECT * FROM settings")
  fun getAllSettings(): Flow<List<Settings>>

  @Query("SELECT * FROM settings WHERE id = :id")
  suspend fun getSettingsById(id: Long): Settings

  @Query("SELECT * FROM settings WHERE `key` LIKE :query")
  suspend fun searchSettings(query: String): List<Settings>
}