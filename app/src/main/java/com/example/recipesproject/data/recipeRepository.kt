package com.example.recipesproject.data

import com.example.projectreceipt.model.RecipeModel
import com.example.recipesproject.data.RecipeDao
import com.example.recipesproject.model.RecipeWithIngredient
import kotlinx.coroutines.flow.Flow

class RecipeRespository (private val recipeDao: RecipeDao) {

    suspend fun insertRecipe(recipeModel: RecipeModel) {
        recipeDao.insertRecipe(recipeModel)
    }

    fun getRecipeById(id: Int): Flow<RecipeModel?> {
        return recipeDao.getRecipeById(id)
    }

    fun getAllRecipesWithIngredient(): Flow<List<RecipeWithIngredient>> {
        return recipeDao.getAllRecipesWithIngredient()
    }

    fun getAllRecipes(): Flow<List<RecipeWithIngredient>> {
        return recipeDao.getAllRecipesWithIngredient()
    }

    suspend fun updateRecipe(recipeModel: RecipeModel) {
        recipeDao.updateRecipe(recipeModel)
    }

    suspend fun deleteRecipe(recipeModel: RecipeModel) {
        recipeDao.deleteRecipe(recipeModel)
    }


}