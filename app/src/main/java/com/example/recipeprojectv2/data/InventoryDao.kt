package com.example.recipeprojectv2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeprojectv2.model.InventoryModel
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory")
    fun getAllInventory(): Flow<List<InventoryModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: InventoryModel)

    @Delete
    suspend fun deleteInventory(inventory: InventoryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryList(inventories: List<InventoryModel>)
}