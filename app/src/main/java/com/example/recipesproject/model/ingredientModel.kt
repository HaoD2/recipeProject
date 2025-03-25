package com.example.projectreceipt.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Huruf kecil "id"
    val name: String,
    val category: String,
    val quantity: String
)

