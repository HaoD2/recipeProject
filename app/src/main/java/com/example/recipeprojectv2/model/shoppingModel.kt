package com.example.recipeprojectv2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping")
data class ShoppingModel(
    @PrimaryKey(autoGenerate = true) val shoppingId: Int = 0,
    val name: String,
    val quantity: String
)