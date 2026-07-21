package com.example.proyeto2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyeto2.ui.screens.ApiCreditsScreen
import com.example.proyeto2.ui.screens.ExploreMealsScreen
import com.example.proyeto2.ui.screens.FavoritesScreen
import com.example.proyeto2.ui.screens.HomeScreen
import com.example.proyeto2.ui.screens.LoginScreen
import com.example.proyeto2.ui.screens.MealDetailScreen
import com.example.proyeto2.ui.screens.PlannerScreen
import com.example.proyeto2.ui.screens.ProfileScreen
import com.example.proyeto2.ui.screens.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("LoginScreen") { LoginScreen(navController) }
        composable("RegisterScreen") { RegisterScreen(navController) }
        composable("HomeScreen") { HomeScreen(navController) }
        composable("ExploreMealsScreen") { ExploreMealsScreen(navController) }
        composable("MealDetailScreen/{mealId}") { backStackEntry ->
            MealDetailScreen(
                navController = navController,
                mealId = backStackEntry.arguments?.getString("mealId")
            )
        }
        composable("FavoritesScreen") { FavoritesScreen(navController) }
        composable("PlannerScreen") { PlannerScreen(navController) }
        composable("ProfileScreen") { ProfileScreen(navController) }
        composable("ApiCreditsScreen") { ApiCreditsScreen(navController) }
    }

}
