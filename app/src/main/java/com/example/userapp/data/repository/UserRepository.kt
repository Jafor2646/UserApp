package com.example.userapp.data.repository

import androidx.lifecycle.LiveData
import com.example.userapp.data.dao.UserDao
import com.example.userapp.data.entity.User

class UserRepository(private val userDao: UserDao) {

    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    fun getUser(userId: Int): LiveData<User> {
        return userDao.getUser(userId)
    }
}