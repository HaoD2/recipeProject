package com.example.recipeprojectv2.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.recipeprojectv2.MainActivity
import com.example.recipeprojectv2.R
import com.example.recipeprojectv2.model.RecipeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

object JsonUtil {

    fun loadJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            val assetManager = context.assets
            val fileList = assetManager.list("") // List semua file di assets
            Log.d("Assets", "Available files: ${fileList?.joinToString()}") // Debug: tampilkan semua file di assets

            val json = assetManager.open(fileName).bufferedReader().use { it.readText() }
            Log.d("loadJsonFromAssets", "JSON Loaded: ${json.take(100)}") // Tampilkan sebagian JSON

            json
        } catch (e: Exception) {
            Log.e("loadJsonFromAssets", "Error loading JSON: $fileName", e)
            null
        }
    }

    fun getRecipes(context: Context): List<RecipeModel> {
        val json = loadJsonFromAssets(context, "recipe.json")

        if (json == null) {
            Log.e("getRecipes", "JSON file not found or empty!")
            return emptyList()
        } else {
            Log.d("getRecipes", "Loaded JSON: ${json.take(200)}") // Batasi agar tidak terlalu panjang
        }

        return try {
            val gson = Gson()
            val listType = object : TypeToken<List<RecipeModel>>() {}.type
            val recipes = gson.fromJson<List<RecipeModel>>(json, listType)

            Log.d("getRecipes", "Parsed recipes count: ${recipes.size}")
            recipes.forEach { Log.d("getRecipes", "Recipe: $it") }

            recipes
        } catch (e: Exception) {
            Log.e("getRecipes", "Error parsing JSON", e)
            emptyList()
        }
    }
    fun getRecipesByFilter(
        context: Context,
        ingredient: String? = null,
        cuisine: String? = null,
        mealType: String? = null
    ): List<RecipeModel> {
        val allRecipes = getRecipes(context)
        return allRecipes.filter { recipe ->
            (ingredient == null || recipe.ingredientsName.any { it.equals(ingredient, ignoreCase = true) }) &&
                    (cuisine == null || recipe.cuisine.equals(cuisine, ignoreCase = true)) &&
                    (mealType == null || recipe.mealType.equals(mealType, ignoreCase = true))
        }
    }



}
