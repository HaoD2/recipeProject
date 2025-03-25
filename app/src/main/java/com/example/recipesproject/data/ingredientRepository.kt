package com.example.recipesproject.data

import com.example.projectreceipt.model.Ingredient
import kotlinx.coroutines.flow.Flow

class IngredientRepository(private val ingredientDao: IngredientDao) {

    suspend fun insertIngredient(ingredient: Ingredient) {
        ingredientDao.insertIngredient(ingredient)
    }

    fun getIngredientById(id: Int): Flow<Ingredient?> {
        return ingredientDao.getIngredientById(id)
    }

    fun getAllIngredients(): Flow<List<Ingredient>> {
        return ingredientDao.getAllIngredients()
    }


    suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.updateIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.deleteIngredient(ingredient)
    }
}