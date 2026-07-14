package com.example.proyeto2.models.meal

data class MealUiState(
    val isLoading: Boolean = false,
    val meals: List<Meal> = emptyList(),
    val selectedMeal: Meal? = null,
    val categories: List<Category> = emptyList(),
    val categoryNames: List<CategoryListItem> = emptyList(),
    val areas: List<Area> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val errorMessage: String? = null
)
