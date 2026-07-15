package com.example.proyeto2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.ui.theme.GradientTotal
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Usuario"

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientTotal)
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    name = displayName,
                    photoUrl = currentUser?.photoUrl?.toString().orEmpty(),
                    isEmailVerified = currentUser?.isEmailVerified == true,
                    onClick = { navController.navigate("ProfileScreen") }
                )

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "Recipe App",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Cursive,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(44.dp))

                MainMenuButton(
                    text = "Explorar recetas",
                    icon = Icons.Filled.Search,
                    onClick = { navController.navigate("ExploreMealsScreen") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                MainMenuButton(
                    text = "Favoritos",
                    icon = Icons.Filled.Favorite,
                    onClick = { navController.navigate("FavoritesScreen") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                MainMenuButton(
                    text = "Planeador",
                    icon = Icons.Filled.DateRange,
                    onClick = { navController.navigate("PlannerScreen") }
                )

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = {
                        auth.signOut()
                        navController.navigate("LoginScreen") {
                            popUpTo("HomeScreen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(text = "Cerrar sesion", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    photoUrl: String,
    isEmailVerified: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (photoUrl.isBlank()) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(
                    text = if (isEmailVerified) "Perfil verificado" else "Perfil sin verificar",
                    color = if (isEmailVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(text = "Editar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun MainMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
