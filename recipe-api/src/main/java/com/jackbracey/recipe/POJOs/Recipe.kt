package com.jackbracey.recipe.POJOs

import com.jackbracey.recipe.Domain.Recipe
import java.io.Serializable

data class Recipe (var id: Long?,
                   var name: String?,
                   var url: String?,
                   var serves: Int?,
                   var totalTime: Int?,
                   var description: String?,
                   var ingredients: List<Ingredient>?,
                   var steps: List<String>?): Serializable {

    constructor(): this(null, null, null, null, null, null, null, null)

    fun convertToRepoObject(recipeDataSource: RecipeDataSources): Recipe {
        return Recipe(name, url, recipeDataSource)
    }

    override fun toString(): String {
        return "Recipe(name='$name', url='$url', serves=$serves, totalTime=$totalTime, description='$description', ingredients=$ingredients, steps=$steps)"
    }
}