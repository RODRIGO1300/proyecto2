package com.example.proyeto2.models.favorite

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<FavoriteMeal> = emptyList(),
    val favoriteMealIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
