package com.example.proyeto2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyeto2.models.favorite.FavoriteUiState
import com.example.proyeto2.models.meal.Meal
import com.example.proyeto2.repository.FavoriteRepository
import com.google.firebase.firestore.ListenerRegistration

class FavoriteViewModel(
    private val repository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {
    var uiState by mutableStateOf(FavoriteUiState())
        private set
    private var favoritesListener: ListenerRegistration? = null

    fun addFavorite(meal: Meal) {
        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)
        repository.addFavorite(meal) { result ->
            uiState = result.fold(
                onSuccess = {
                    uiState.copy(
                        isLoading = false,
                        successMessage = "Receta agregada a favoritos.",
                        errorMessage = null
                    )
                },
                onFailure = {
                    uiState.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "No se pudo agregar a favoritos.",
                        successMessage = null
                    )
                }
            )
        }
    }

    fun loadFavorites() {
        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)
        repository.loadFavorites { result ->
            uiState = result.fold(
                onSuccess = {
                    uiState.copy(
                        isLoading = false,
                        favorites = it,
                        favoriteMealIds = it.map { favorite -> favorite.idMeal }.toSet(),
                        errorMessage = null
                    )
                },
                onFailure = {
                    uiState.copy(
                        isLoading = false,
                        favorites = emptyList(),
                        favoriteMealIds = emptySet(),
                        errorMessage = it.message ?: "No se pudieron cargar tus favoritos."
                    )
                }
            )
        }
    }

    fun observeFavorites() {
        if (favoritesListener != null) return

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        favoritesListener = repository.observeFavorites { result ->
            uiState = result.fold(
                onSuccess = { favorites ->
                    uiState.copy(
                        isLoading = false,
                        favorites = favorites,
                        favoriteMealIds = favorites.map { it.idMeal }.toSet(),
                        errorMessage = null
                    )
                },
                onFailure = {
                    uiState.copy(
                        isLoading = false,
                        favorites = emptyList(),
                        favoriteMealIds = emptySet(),
                        errorMessage = it.message ?: "No se pudieron escuchar tus favoritos."
                    )
                }
            )
        }
    }

    fun isFavorite(idMeal: String?): Boolean {
        return !idMeal.isNullOrBlank() && idMeal in uiState.favoriteMealIds
    }

    fun toggleFavorite(meal: Meal) {
        val mealId = meal.idMeal
        if (mealId.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "No se pudo identificar la receta.")
            return
        }

        if (isFavorite(mealId)) {
            removeFavorite(mealId)
        } else {
            addFavorite(meal)
        }
    }

    fun removeFavorite(idMeal: String) {
        repository.removeFavorite(idMeal) { result ->
            uiState = if (result.isSuccess) {
                uiState.copy(successMessage = "Receta eliminada de favoritos.", errorMessage = null)
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message ?: "No se pudo eliminar el favorito.")
            }
        }
    }

    override fun onCleared() {
        favoritesListener?.remove()
        favoritesListener = null
        super.onCleared()
    }
}
