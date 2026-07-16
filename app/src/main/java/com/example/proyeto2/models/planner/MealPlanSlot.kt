package com.example.proyeto2.models.planner

data class MealPlanSlot(
    val idUsuario: String = "",
    val dia: String = "",
    val tiempo: String = "",
    val idMeal: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val comentarios: String = "",
    val fecha: Long = 0L
)
