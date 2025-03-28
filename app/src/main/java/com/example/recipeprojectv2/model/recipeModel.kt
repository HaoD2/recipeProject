package com.example.recipeprojectv2.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class RecipeModel(
    @SerializedName("recipeMeal") val recipeName: String,
    @SerializedName("recipeCuisine") val cuisine: String,
    @SerializedName("mealType") val mealType: String, // Tambahkan ini ke JSON
    @SerializedName("ingredientName") val ingredientsName: List<String>
) {
    companion object {
        private val gson = Gson()

        fun fromJson(json: String): RecipeModel {
            return gson.fromJson(json, RecipeModel::class.java)
        }

        fun toJson(recipe: RecipeModel): String {
            return gson.toJson(recipe)
        }
    }
}
