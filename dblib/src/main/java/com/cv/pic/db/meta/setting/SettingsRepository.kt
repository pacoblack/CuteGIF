package com.cv.pic.db.meta.setting

import com.cv.pic.db.core.BaseRepository
import com.cv.pic.db.core.RepositoryOperations
import kotlinx.coroutines.flow.Flow

class SettingsRepository(dao: SettingsDao) : BaseRepository<SettingsDao>(dao), RepositoryOperations<Settings>{
  override suspend fun insert(item: Settings): Long {
    return dao.insert(item)
  }

  override suspend fun update(item: Settings) {
    dao.update(item)
  }

  override suspend fun delete(item: Settings) {
    dao.delete(item)
  }

  override suspend fun getById(id: Long): Settings {
    return dao.getSettingsById(id)
  }

  override fun getAll(): Flow<List<Settings>> {
    return dao.getAllSettings()
  }

  override suspend fun search(query: String): List<Settings> {
    return dao.searchSettings(query)
  }
}