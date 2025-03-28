package com.example.recipeprojectv2.data

import com.example.recipeprojectv2.model.InventoryModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class InventoryRepository @Inject constructor(private val inventoryDao: InventoryDao) {
    val allInventory: Flow<List<InventoryModel>> = inventoryDao.getAllInventory()

    suspend fun insert(inventory: InventoryModel) {
        inventoryDao.insertInventory(inventory)
    }

    suspend fun delete(inventory: InventoryModel) {
        inventoryDao.deleteInventory(inventory)
    }
}