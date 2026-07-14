package com.example.proyeto2.models.meal

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("idIngredient")
    val id: String? = null,

    @SerializedName("strIngredient")
    val name: String? = null,

    @SerializedName("strDescription")
    val description: String? = null,

    @SerializedName("strType")
    val type: String? = null
)
