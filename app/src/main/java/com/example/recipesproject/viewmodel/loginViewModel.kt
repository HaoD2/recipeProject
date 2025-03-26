package com.example.recipesproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesproject.data.AppDatabase
import com.example.recipesproject.data.UsersDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    fun login(username: String, password: String, onLoginSuccess: () -> Unit, onLoginFailed: () -> Unit) {
        viewModelScope.launch {
            val user = database.usersDao().verifyUser(username, password)
            if (user != null) {
                Log.d("LoginViewModel", "Login berhasil: ${user.name}")
                onLoginSuccess()
            } else {
                Log.d("LoginViewModel", "Login gagal")
                onLoginFailed()
            }
        }
    }
}