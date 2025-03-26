package com.example.recipesproject.data

import com.example.projectreceipt.model.RecipeModel
import com.example.recipesproject.data.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRespository (private val recipeDao: RecipeDao) {

    suspend fun insertRecipe(recipeModel: RecipeModel) {
        recipeDao.insertRecipe(recipeModel)
    }

    fun getRecipeById(id: Int): Flow<RecipeModel?> {
        return recipeDao.getRecipeById(id)
    }

    fun getAllRecipes(): Flow<List<RecipeModel>> {
        return recipeDao.getAllRecipes()
    }

    suspend fun updateRecipe(recipeModel: RecipeModel) {
        recipeDao.updateRecipe(recipeModel)
    }

    suspend fun deleteRecipe(recipeModel: RecipeModel) {
        recipeDao.deleteRecipe(recipeModel)
    }


}