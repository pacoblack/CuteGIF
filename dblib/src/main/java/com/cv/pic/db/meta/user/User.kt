package com.cv.pic.db.meta.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val email: String,
  val createdAt: Date = Date(),
  val lastLogin: Date? = null
)
