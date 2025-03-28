package com.example.recipeprojectv2.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.example.recipeprojectv2.data.AppDatabase
import com.example.recipeprojectv2.data.InventoryDao
import com.example.recipeprojectv2.data.ShoppingDao
import com.example.recipeprojectv2.data.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO) // ✅ Gunakan Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope // ✅ Inject CoroutineScope ke Database
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideInventoryDao(database: AppDatabase): InventoryDao {
        return database.inventoryDao()
    }

    @Provides
    @Singleton
    fun provideShoppingDao(database: AppDatabase): ShoppingDao {
        return database.shoppingDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UsersDao {
        return database.usersDao()
    }
}
