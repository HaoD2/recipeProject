package com.example.recipeprojectv2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipeprojectv2.data.InventoryRepository
import com.example.recipeprojectv2.data.InventoryWorker
import com.example.recipeprojectv2.model.InventoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// ViewModel untuk Inventory
@HiltViewModel
class InventoryViewModel @Inject constructor(private val repository: InventoryRepository) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    private val _allInventory: Flow<List<InventoryModel>> = repository.allInventory

    val filteredInventory: StateFlow<List<InventoryModel>> = combine(_allInventory, searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(inventory: InventoryModel) {
        viewModelScope.launch {
            repository.insert(inventory)
        }
    }

    fun delete(inventory: InventoryModel) {
        viewModelScope.launch {
            repository.delete(inventory)
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }


}