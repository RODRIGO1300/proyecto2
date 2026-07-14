package com.example.proyeto2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeto2.models.meal.MealUiState
import com.example.proyeto2.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(
    private val repository: MealRepository = MealRepository()
) : ViewModel() {
    var uiState by mutableStateOf(MealUiState())
        private set

    init {
        searchByLetter("a")
    }

    fun searchByLetter(letter: String) {
        val cleanLetter = letter.trim().take(1).lowercase()
        if (cleanLetter.isBlank()) {
            uiState = uiState.copy(errorMessage = "Elige una letra para buscar.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                selectedLetter = cleanLetter,
                errorMessage = null
            )
            runCatching {
                repository.searchByFirstLetter(cleanLetter)
            }.onSuccess { meals ->
                uiState = uiState.copy(
                    isLoading = false,
                    meals = meals,
                    errorMessage = if (meals.isEmpty()) "No se encontraron recetas con esa letra." else null
                )
            }.onFailure { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    meals = emptyList(),
                    errorMessage = exception.message ?: "No se pudo cargar la informacion de recetas."
                )
            }
        }
    }
}
