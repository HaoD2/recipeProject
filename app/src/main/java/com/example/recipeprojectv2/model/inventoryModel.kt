package com.example.recipeprojectv2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "inventory")
data class InventoryModel(
    @PrimaryKey(autoGenerate = true) val inventoryId: Int = 0,
    val name: String,
    val category: String,
    val quantity: String,
    val expire: Long
)