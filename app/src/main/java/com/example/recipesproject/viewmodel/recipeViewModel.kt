package com.example.recipesproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectreceipt.model.RecipeModel
import com.example.recipesproject.data.RecipeRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRespository,

) : ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    init {
        getAllRecipes()
    }



    fun getAllRecipes() {
        viewModelScope.launch {
            try {
                repository.getAllRecipes().collect {
                    _recipes.value = it
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error getting recipes", e)
            }
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

    class Factory(private val repository: RecipeRespository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecipeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
