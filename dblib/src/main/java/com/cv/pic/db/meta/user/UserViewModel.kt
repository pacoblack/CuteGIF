package com.cv.pic.db.meta.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cv.pic.db.core.DatabaseManager
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
  private val userRepo = DatabaseManager.getRepository<UserRepository>(
    UserRepository::class.java
  )

  fun createUser(name: String, email: String) {
    viewModelScope.launch {
      val user = User(name = name, email = email)
      userRepo.insert(user)
    }
  }

  suspend fun transferUserData(fromUserId: Long, toUserId: Long) {
    val userRepo = DatabaseManager.getRepository<UserRepository>(
      UserRepository::class.java
    )

    userRepo.doSomething()
  }
}