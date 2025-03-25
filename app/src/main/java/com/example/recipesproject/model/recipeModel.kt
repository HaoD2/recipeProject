package com.example.projectreceipt.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe",
    foreignKeys = [ForeignKey(
        entity = Ingredient::class,
        parentColumns = ["id"],
        childColumns = ["ingredientId"],  // Harus sama dengan @ColumnInfo di bawah
        onDelete = ForeignKey.CASCADE
    )]
)
data class RecipeModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredientId: Int,  // Ubah @ColumnInfo agar sama dengan childColumns
    val mealType: String,
    val mealCuisine: String,
    val recipeName: String
)
