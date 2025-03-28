package com.example.recipesproject.di

import android.content.Context
import androidx.room.Room
import com.example.recipesproject.data.AppDatabase
import com.example.recipesproject.data.IngredientDao
import com.example.recipesproject.data.IngredientRepository
import com.example.recipesproject.data.RecipeDao
import com.example.recipesproject.data.RecipeRespository
import com.example.recipesproject.data.UsersDao
import com.example.recipesproject.data.UsersRepository


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_database"
        )
            .fallbackToDestructiveMigration() // 🔥 Tambahkan ini!
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeDao: RecipeDao): RecipeRespository {
        return RecipeRespository(recipeDao)
    }

    // 🆕 Tambahkan UserDao dan UsersRepository
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UsersDao {
        return database.usersDao() // Sesuai dengan nama fungsi di AppDatabase
    }

    @Provides
    @Singleton
    fun provideUserRepository(usersDao: UsersDao): UsersRepository {
        return UsersRepository(usersDao)
    }

    @Provides
    @Singleton
    fun provideIngredientRepository(ingredientDao: IngredientDao): IngredientRepository {
        return IngredientRepository(ingredientDao)
    }
    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }
}