package com.cv.pic.db.meta.user

import com.cv.pic.db.core.RepositoryOperations
import kotlinx.coroutines.flow.Flow
import java.util.Date

class UserRepository(private val dao: UserDao) : RepositoryOperations<User> {

  override suspend fun insert(item: User): Long {
    return dao.insert(item)
  }

  override suspend fun update(item: User) {
    dao.update(item)
  }

  override suspend fun delete(item: User) {
    dao.delete(item)
  }

  override suspend fun getById(id: Long): User? {
    return dao.getUserById(id)
  }

  override fun getAll(): Flow<List<User>> {
    return dao.getAllUsers()
  }

  override suspend fun search(query: String): List<User> {
    return dao.searchUsers("%$query%")
  }

  // 自定义业务方法
  suspend fun getActiveUsers(days: Int = 30): List<User> {
    val minDate = Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L)
    return dao.getActiveUsers(minDate)
  }

  suspend fun doSomething(){
    dao.changeUserName(123, "newName")
  }
}