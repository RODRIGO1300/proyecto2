package com.example.proyeto2.models.meal

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("idCategory")
    val id: String? = null,

    @SerializedName("strCategory")
    val name: String? = null,

    @SerializedName("strCategoryThumb")
    val imageUrl: String? = null,

    @SerializedName("strCategoryDescription")
    val description: String? = null
)
