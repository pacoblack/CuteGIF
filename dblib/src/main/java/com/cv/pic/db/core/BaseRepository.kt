package com.cv.pic.db.core

import kotlinx.coroutines.flow.Flow

interface RepositoryOperations<T> {
  suspend fun insert(item: T): Long
  suspend fun update(item: T)
  suspend fun delete(item: T)
  suspend fun getById(id: Long): T?
  fun getAll(): Flow<List<T>>
  suspend fun search(query: String): List<T>
}