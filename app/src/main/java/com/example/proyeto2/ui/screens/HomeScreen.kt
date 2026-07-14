package com.example.proyeto2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "RecipeBook", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { navController.navigate("ExploreMealsScreen") }) {
            Text(text = "Explorar recetas")
        }
        Button(onClick = { navController.navigate("FavoritesScreen") }) {
            Text(text = "Favoritos")
        }
        Button(onClick = { navController.navigate("PlannerScreen") }) {
            Text(text = "Planeador")
        }
        Button(onClick = { navController.navigate("ProfileScreen") }) {
            Text(text = "Perfil")
        }
    }
}
