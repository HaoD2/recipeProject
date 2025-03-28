package com.example.recipeprojectv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeprojectv2.model.UsersModel


@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsersModel)

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUser(name: String): UsersModel?

    @Query("SELECT * FROM users WHERE name = :name AND pass = :password LIMIT 1")
    suspend fun verifyUser(name: String, password: String): UsersModel?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(userList: List<UsersModel>)

}