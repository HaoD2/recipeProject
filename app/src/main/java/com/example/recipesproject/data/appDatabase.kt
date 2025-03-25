package com.example.recipesproject.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projectreceipt.model.Ingredient
import com.example.projectreceipt.model.RecipeModel
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Ingredient::class, RecipeModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(RecipeDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class RecipeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.recipeDao())
                }
            }
        }

        suspend fun populateDatabase(recipeDao: RecipeDao) {
            Log.d("RecipeDatabaseCallback", "Populating initial data...") // 🔥 Log awal
            // Hapus semua data sebelumnya
            recipeDao.insertIngredient(
                Ingredient(
                    name = "Garlic",
                    category = "Spices",
                    quantity = "2 cloves"
                )
            )
            recipeDao.insertIngredient(
                Ingredient(
                    name = "Onion",
                    category = "Vegetables",
                    quantity = "1 large"
                )
            )
            recipeDao.insertIngredient(
                Ingredient(
                    name = "Chicken",
                    category = "Meat",
                    quantity = "200g"
                )
            )


            recipeDao.insertRecipe(
                RecipeModel(
                    recipeName = "Garlic Chicken",
                    mealType = "Dinner",
                    mealCuisine = "Western",
                    ingredientId = 1
                )
            )
            recipeDao.insertRecipe(
                RecipeModel(
                    recipeName = "Onion Soup",
                    mealType = "Lunch",
                    mealCuisine = "French",
                    ingredientId = 2
                )
            )

            Log.d("RecipeDatabaseCallback", "Initial data population complete!") // 🔥 Log akhir
        }
    }
}