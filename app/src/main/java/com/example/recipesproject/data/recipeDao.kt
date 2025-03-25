package com.example.recipesproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projectreceipt.model.Ingredient
import com.example.projectreceipt.model.RecipeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipeModel: RecipeModel)

    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeById(id: Int): Flow<RecipeModel?>

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<RecipeModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Update
    suspend fun updateRecipe(recipeModel: RecipeModel)

    @Delete
    suspend fun deleteRecipe(recipeModel: RecipeModel)
}