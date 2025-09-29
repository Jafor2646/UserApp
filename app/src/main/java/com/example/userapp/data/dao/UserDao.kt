package com.example.userapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.userapp.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user_table ORDER BY name ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUser(userId: Int): LiveData<User>

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()
}