package com.example.proyeto2.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyeto2.ui.screens.LoginScreen
import com.example.proyeto2.ui.screens.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "LoginScreen") {
        composable("LoginScreen") { LoginScreen(navController) }
        composable("RegisterScreen") { RegisterScreen(navController) }
    }

}