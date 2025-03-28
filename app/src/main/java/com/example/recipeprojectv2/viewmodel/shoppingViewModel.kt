package com.example.recipeprojectv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeprojectv2.data.ShoppingRepository
import com.example.recipeprojectv2.model.ShoppingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(private val repository: ShoppingRepository) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    private val _allShopping: Flow<List<ShoppingModel>> = repository.allShopping

    val filteredShopping: StateFlow<List<ShoppingModel>> = combine(_allShopping, searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(shopping: ShoppingModel) {
        viewModelScope.launch {
            repository.insert(shopping)
        }
    }

    fun delete(shopping: ShoppingModel) {
        viewModelScope.launch {
            repository.delete(shopping)
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}