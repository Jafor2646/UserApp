package com.example.userapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.userapp.data.database.UserDatabase
import com.example.userapp.data.entity.User
import com.example.userapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    val allUsers: LiveData<List<User>>

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.allUsers
    }

    fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(user)
    }

    fun getUser(userId: Int): LiveData<User> {
        return repository.getUser(userId)
    }
}