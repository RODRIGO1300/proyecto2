package com.example.proyeto2.models.planner

data class PlannerUiState(
    val isLoading: Boolean = false,
    val slots: List<MealPlanSlot> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
