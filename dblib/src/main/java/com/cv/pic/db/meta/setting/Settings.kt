package com.cv.pic.db.meta.setting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings (
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val key:String,
  val value:String?
)