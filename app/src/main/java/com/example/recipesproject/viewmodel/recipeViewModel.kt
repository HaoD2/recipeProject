package com.example.recipesproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectreceipt.model.RecipeModel
import com.example.recipesproject.data.IngredientRepository
import com.example.recipesproject.data.RecipeRespository
import com.example.recipesproject.model.RecipeWithIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRespository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {

    private val _recipesWithIngredients = MutableStateFlow<List<RecipeWithIngredient>>(emptyList())
    val recipesWithIngredients: StateFlow<List<RecipeWithIngredient>> = _recipesWithIngredients

    init {
        getAllRecipes()
    }

    fun getAllRecipes() {
        viewModelScope.launch {
            repository.getAllRecipesWithIngredient().collect {
                _recipesWithIngredients.value = it
            }
        }
        fun insertRecipe(recipe: RecipeModel) {
            viewModelScope.launch {
                try {
                    repository.insertRecipe(recipe)
                    getAllRecipes() // Refresh list setelah insert
                } catch (e: Exception) {
                    Log.e("RecipeViewModel", "Error inserting recipe", e)
                }
            }
        }

        fun deleteRecipe(recipe: RecipeModel) {
            viewModelScope.launch {
                try {
                    repository.deleteRecipe(recipe)
                    getAllRecipes() // Refresh list setelah delete
                } catch (e: Exception) {
                    Log.e("RecipeViewModel", "Error deleting recipe", e)
                }
            }
        }
    }
}