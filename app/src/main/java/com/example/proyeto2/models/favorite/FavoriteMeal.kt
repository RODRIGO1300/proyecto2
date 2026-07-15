package com.example.proyeto2.models.favorite

data class FavoriteMeal(
    val idUsuario: String = "",
    val idMeal: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val categoria: String = "",
    val pais: String = "",
    val nota: String = "",
    val fecha: Long = 0L
)
