package com.example.proyeto2.models.meal

import com.google.gson.annotations.SerializedName

data class CategoryListItem(
    @SerializedName("strCategory")
    val name: String? = null
)
