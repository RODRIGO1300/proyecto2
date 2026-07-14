package com.example.proyeto2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val RECIPES_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val recipesRetrofit = Retrofit.Builder()
        .baseUrl(RECIPES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = recipesRetrofit.create(ApiService::class.java)
}
