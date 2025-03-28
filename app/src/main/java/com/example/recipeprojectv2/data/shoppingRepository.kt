package com.example.recipeprojectv2.data

import com.example.recipeprojectv2.model.ShoppingModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Repository untuk Shopping
@ViewModelScoped
class ShoppingRepository @Inject constructor(private val shoppingDao: ShoppingDao) {
    val allShopping: Flow<List<ShoppingModel>> = shoppingDao.getAllShopping()

    suspend fun insert(shopping: ShoppingModel) {
        shoppingDao.insertShopping(shopping)
    }

    suspend fun delete(shopping: ShoppingModel) {
        shoppingDao.deleteShopping(shopping)
    }
}