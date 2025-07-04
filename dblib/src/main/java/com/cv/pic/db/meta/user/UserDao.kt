package com.cv.pic.db.meta.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(user: User): Long

  @Update
  suspend fun update(user: User)

  @Delete
  suspend fun delete(user: User)

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<User>>

  @Query("SELECT * FROM users WHERE id = :userId")
  suspend fun getUserById(userId: Long): User?

  @Query("SELECT * FROM users WHERE name LIKE :query OR email LIKE :query")
  suspend fun searchUsers(query: String): List<User>

  @Transaction
  @Query("SELECT * FROM users WHERE lastLogin > :minDate")
  suspend fun getActiveUsers(minDate: Date): List<User>

  @Transaction
  suspend fun changeUserName(id: Long,  name:String){
    var user = getUserById(id)
    if (user?.email?.contains("aaa") == true){
      user.name = name
      update(user)
    }
  }
}