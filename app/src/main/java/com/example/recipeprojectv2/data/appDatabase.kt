package com.example.recipeprojectv2.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipeprojectv2.model.InventoryModel
import com.example.recipeprojectv2.model.ShoppingModel
import com.example.recipeprojectv2.model.UsersModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.random.Random
@Database(entities = [InventoryModel::class, ShoppingModel::class, UsersModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun usersDao(): UsersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    fun populateDatabase(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            Log.d("Database", "Populating database with sample data...")

            val inventoryDao = inventoryDao()
            val shoppingDao = shoppingDao()
            val usersDao = usersDao()

            val inventoryList = listOf(
                InventoryModel(name = "Tomato", category = "Vegetable", quantity = "2", expire = getRandomExpiration()),
                InventoryModel(name = "Chicken", category = "Meat", quantity = "1 kg", expire = getRandomExpiration()),
                InventoryModel(name = "Rice", category = "Grain", quantity = "5 kg", expire = getRandomExpiration()),
                InventoryModel(name = "Onion", category = "Vegetable", quantity = "3", expire = getRandomExpiration()),
                InventoryModel(name = "Carrot", category = "Vegetable", quantity = "4", expire = getRandomExpiration()),
                InventoryModel(name = "Potato", category = "Vegetable", quantity = "6", expire = getRandomExpiration()),
                InventoryModel(name = "Salt", category = "Spice", quantity = "1 pack", expire = getRandomExpiration()),
                InventoryModel(name = "Sugar", category = "Sweetener", quantity = "1 kg", expire = getRandomExpiration()),
                InventoryModel(name = "Butter", category = "Dairy", quantity = "200 grams", expire = getRandomExpiration()),
                InventoryModel(name = "Milk", category = "Dairy", quantity = "1 liter", expire = getRandomExpiration())
            )
            inventoryDao.insertInventoryList(inventoryList)

            val shoppingList = listOf(
                ShoppingModel(name = "Eggs", quantity = "12 pcs"),
                ShoppingModel(name = "Cheese", quantity = "200 grams"),
                ShoppingModel(name = "Bread", quantity = "1 loaf"),
                ShoppingModel(name = "Chicken Wings", quantity = "1 kg"),
                ShoppingModel(name = "Fish", quantity = "2 kg"),
                ShoppingModel(name = "Olive Oil", quantity = "500 ml"),
                ShoppingModel(name = "Soy Sauce", quantity = "300 ml"),
                ShoppingModel(name = "Yogurt", quantity = "500 grams"),
                ShoppingModel(name = "Mushrooms", quantity = "250 grams"),
                ShoppingModel(name = "Pasta", quantity = "500 grams")
            )
            shoppingDao.insertShoppingList(shoppingList)

            val userList = listOf(
                UsersModel(name = "Alice", pass = "alice123"),
                UsersModel(name = "Michael", pass = "michael789"),
                UsersModel(name = "User001", pass = "password001")
            )
            usersDao.insertUserList(userList)

            Log.d("Database", "Database populated successfully!")
        }
    }

    private fun getRandomExpiration(): Long {
        val now = System.currentTimeMillis()
        val threeYearsInMillis = TimeUnit.DAYS.toMillis(3 * 365)
        val randomOffset = Random.nextLong(0, threeYearsInMillis)
        return now + randomOffset
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("DatabaseCallback", "Database Created - Populating Data...")

            INSTANCE?.populateDatabase(scope)
        }
    }
}
