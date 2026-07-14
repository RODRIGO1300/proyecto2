package com.example.proyeto2.repository

import com.example.proyeto2.network.ApiService
import com.example.proyeto2.network.RetrofitClient

class MealRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun searchByName(name: String) = apiService.searchByName(name).meals.orEmpty()

    suspend fun searchByFirstLetter(letter: String) =
        apiService.searchByFirstLetter(letter.take(1)).meals.orEmpty()

    suspend fun getMealById(id: String) = apiService.getMealById(id).meals?.firstOrNull()

    suspend fun getRandomMeal() = apiService.getRandomMeal().meals?.firstOrNull()

    suspend fun getCategories() = apiService.getCategories().categories.orEmpty()

    suspend fun getAreas() = apiService.getAreaList().meals.orEmpty()

    suspend fun getIngredients() = apiService.getIngredientList().meals.orEmpty()

    suspend fun filterByIngredient(ingredient: String) =
        apiService.filterByIngredient(ingredient).meals.orEmpty()

    suspend fun filterByCategory(category: String) =
        apiService.filterByCategory(category).meals.orEmpty()

    suspend fun filterByArea(area: String) = apiService.filterByArea(area).meals.orEmpty()
}
