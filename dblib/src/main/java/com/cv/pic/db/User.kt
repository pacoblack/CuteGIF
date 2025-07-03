package com.cv.pic.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

/**
 * 用户实体
 */
@Entity(tableName = "users")
data class User(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val email: String,
  val createdAt: Long = System.currentTimeMillis(),
  val isActive: Boolean = true
)

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
  @Insert
  suspend fun insert(user: User): Long

  @Update
  suspend fun update(user: User)

  @Query("SELECT * FROM users WHERE id = :id")
  suspend fun getById(id: Long): User?

  @Query("SELECT * FROM users ORDER BY name ASC")
  suspend fun getAll(): List<User>

  @Query("SELECT * FROM users WHERE isActive = 1")
  suspend fun getActiveUsers(): List<User>

  @Query("DELETE FROM users WHERE id = :id")
  suspend fun delete(id: Long)
}