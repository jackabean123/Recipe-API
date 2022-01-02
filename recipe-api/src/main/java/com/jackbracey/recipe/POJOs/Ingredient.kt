package com.jackbracey.recipe.POJOs

import com.jackbracey.recipe.Domain.Ingredient.Ingredient
import java.io.Serializable
import java.math.BigDecimal

data class Ingredient (val id: Long?,
                       val name: String?,
                       val amount: BigDecimal?,
                       val method: String?): Serializable {
    constructor(): this(null, null, null, null)

    fun convertToDomain(): Ingredient {
        return Ingredient(name)
    }

    override fun toString(): String {
        return "Ingredient(name=$name, amount=$amount, method=$method)"
    }
}