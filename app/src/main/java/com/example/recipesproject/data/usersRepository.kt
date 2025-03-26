package com.example.recipesproject.data
import com.example.recipesproject.model.UsersModel

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val usersDao: UsersDao) {

    suspend fun insertUser(user: UsersModel) {
        usersDao.insertUser(user)
    }

    suspend fun getUser(name: String): UsersModel? {
        return usersDao.getUser(name)
    }

    suspend fun verifyUser(name: String, password: String): UsersModel? {
        return usersDao.verifyUser(name, password)
    }
}