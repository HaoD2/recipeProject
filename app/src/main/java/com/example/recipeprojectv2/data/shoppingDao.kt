package com.example.recipeprojectv2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeprojectv2.model.ShoppingModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping")
    fun getAllShopping(): Flow<List<ShoppingModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopping(shopping: ShoppingModel)

    @Delete
    suspend fun deleteShopping(shopping: ShoppingModel)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: List<ShoppingModel>)

}
