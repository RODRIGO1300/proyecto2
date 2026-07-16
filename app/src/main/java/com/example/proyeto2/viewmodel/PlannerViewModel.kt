package com.example.proyeto2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyeto2.models.favorite.FavoriteMeal
import com.example.proyeto2.models.planner.PlannerUiState
import com.example.proyeto2.repository.PlannerRepository
import com.google.firebase.firestore.ListenerRegistration

class PlannerViewModel(
    private val repository: PlannerRepository = PlannerRepository()
) : ViewModel() {
    var uiState by mutableStateOf(PlannerUiState())
        private set
    private var plannerListener: ListenerRegistration? = null

    fun observePlanner() {
        if (plannerListener != null) return

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        plannerListener = repository.observePlanner { result ->
            uiState = result.fold(
                onSuccess = {
                    uiState.copy(isLoading = false, slots = it, errorMessage = null)
                },
                onFailure = {
                    uiState.copy(
                        isLoading = false,
                        slots = emptyList(),
                        errorMessage = it.message ?: "No se pudo cargar el planeador."
                    )
                }
            )
        }
    }

    fun assignMeal(day: String, mealTime: String, favorite: FavoriteMeal, comments: String = "") {
        repository.assignMeal(day, mealTime, favorite, comments) { result ->
            uiState = if (result.isSuccess) {
                uiState.copy(successMessage = "Platillo asignado.", errorMessage = null)
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message ?: "No se pudo asignar el platillo.")
            }
        }
    }

    fun updateComments(day: String, mealTime: String, comments: String) {
        repository.updateComments(day, mealTime, comments) { result ->
            uiState = if (result.isSuccess) {
                uiState.copy(successMessage = "Comentarios actualizados.", errorMessage = null)
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message ?: "No se pudieron actualizar comentarios.")
            }
        }
    }

    fun clearSlot(day: String, mealTime: String) {
        repository.clearSlot(day, mealTime) { result ->
            uiState = if (result.isSuccess) {
                uiState.copy(successMessage = "Espacio limpiado.", errorMessage = null)
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message ?: "No se pudo limpiar el espacio.")
            }
        }
    }

    fun slotFor(day: String, mealTime: String) =
        uiState.slots.firstOrNull { it.dia == day && it.tiempo == mealTime }

    override fun onCleared() {
        plannerListener?.remove()
        plannerListener = null
        super.onCleared()
    }
}
