package com.example.recipesproject.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.projectreceipt.model.Ingredient
import com.example.projectreceipt.model.RecipeModel

data class RecipeWithIngredient(
    @Embedded val recipe: RecipeModel,

    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredients: List<Ingredient>
)