package com.example.proyeto2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyeto2.MainActivity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "RecipeBook", style = MaterialTheme.typography.headlineMedium)
        Text(text = currentUser?.email.orEmpty())
        if (currentUser?.isEmailVerified == true) {
            Text(text = "Correo verificado", color = MaterialTheme.colorScheme.primary)
        }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Button(
            onClick = {
                val activity = context as MainActivity
                activity.finish()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EXIT", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
