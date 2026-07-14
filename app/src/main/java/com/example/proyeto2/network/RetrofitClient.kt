package com.example.proyeto2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val RECEPS_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val RecipesRetrofit = Retrofit.Builder()
        .baseUrl(RECEPS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}