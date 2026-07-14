package com.example.proyeto2.network

import com.example.proyeto2.models.meal.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
        // Buscar recetas por nombre
        @GET("search.php")
        suspend fun searchByName(
            @Query("s") name: String
        ): MealResponse

        // Buscar recetas por la primera letra
        @GET("search.php")
        suspend fun searchByFirstLetter(
            @Query("f") letter: String
        ): MealResponse

        // Obtener todos los detalles de una receta
        @GET("lookup.php")
        suspend fun getMealById(
            @Query("i") id: String
        ): MealResponse

        // Obtener una receta aleatoria
        @GET("random.php")
        suspend fun getRandomMeal(): MealResponse

        // Obtener categorías con descripción e imagen
        @GET("categories.php")
        suspend fun getCategories(): CategoryResponse

        // Obtener nombres de categorías
        @GET("list.php")
        suspend fun getCategoryList(
            @Query("c") value: String = "list"
        ): MealResponse

        // Obtener lista de países o regiones
        @GET("list.php")
        suspend fun getAreaList(
            @Query("a") value: String = "list"
        ): MealResponse

        // Obtener lista de ingredientes
        @GET("list.php")
        suspend fun getIngredientList(
            @Query("i") value: String = "list"
        ): MealResponse

        // Filtrar por un ingrediente
        @GET("filter.php")
        suspend fun filterByIngredient(
            @Query("i") ingredient: String
        ): MealResponse

        // Filtrar por categoría
        @GET("filter.php")
        suspend fun filterByCategory(
            @Query("c") category: String
        ): MealResponse

        // Filtrar por país o región
        @GET("filter.php")
        suspend fun filterByArea(
            @Query("a") area: String
        ): MealResponse
}