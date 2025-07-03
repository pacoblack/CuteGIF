package com.cv.pic.db.core

import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

abstract class BaseRepository<T>(protected val dao: T) {

  // 通用事务操作
  suspend fun <R> transaction(block: suspend () -> R): R {
    return if (dao is RoomDatabase) {
      (dao as RoomDatabase).runInTransaction(block)
    } else {
      block()
    }
  }

  // 安全执行数据库操作
  suspend fun <R> safeDatabaseOperation(
    operation: suspend () -> R,
    onError: (Exception) -> Unit = { it.printStackTrace() }
  ): R? {
    return try {
      operation()
    } catch (e: Exception) {
      onError(e)
      null
    }
  }
}

interface RepositoryOperations<T> {
  suspend fun insert(item: T): Long
  suspend fun update(item: T)
  suspend fun delete(item: T)
  suspend fun getById(id: Long): T?
  fun getAll(): Flow<List<T>>
  suspend fun search(query: String): List<T>
}