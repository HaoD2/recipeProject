package com.example.recipesproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class UsersModel (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Huruf kecil "id"
    val name: String,
    val pass: String,
    )