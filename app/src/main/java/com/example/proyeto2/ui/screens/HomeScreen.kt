package com.example.proyeto2.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.R
import com.example.proyeto2.ui.components.AppConfirmDialog
import com.example.proyeto2.ui.theme.GradientOtono
import com.example.proyeto2.ui.theme.RecipeCream
import com.example.proyeto2.ui.theme.RecipeForest
import com.example.proyeto2.ui.theme.RecipeInk
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Usuario"
    val isLoggedIn = currentUser != null
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientOtono)
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAccountAction(
                    user = currentUser,
                    displayName = displayName,
                    onProfileClick = { navController.navigate("ProfileScreen") },
                    onLoginClick = { navController.navigate("LoginScreen") }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo RecipeBook",
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(3.dp, RecipeCream), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Recipe App",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Cursive,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(34.dp))

                MainMenuButton(
                    text = "Explorar recetas",
                    icon = Icons.Filled.Search,
                    onClick = { navController.navigate("ExploreMealsScreen") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                MainMenuButton(
                    text = "Favoritos",
                    icon = Icons.Filled.Favorite,
                    onClick = {
                        if (isLoggedIn) {
                            navController.navigate("FavoritesScreen")
                        } else {
                            navController.navigate("LoginScreen")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                MainMenuButton(
                    text = "Planeador",
                    icon = Icons.Filled.DateRange,
                    onClick = {
                        if (isLoggedIn) {
                            navController.navigate("PlannerScreen")
                        } else {
                            navController.navigate("LoginScreen")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                MainMenuButton(
                    text = "Creditos de API",
                    icon = Icons.Filled.Info,
                    onClick = { navController.navigate("ApiCreditsScreen") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Salir de la app", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    AppConfirmDialog(
        visible = showExitDialog,
        title = "Salir de la app",
        message = "Estas seguro de que deseas salir de RecipeBook?",
        confirmText = "Salir",
        dismissText = "Cancelar",
        onConfirm = {
            showExitDialog = false
            (context as? Activity)?.finish()
        },
        onDismiss = { showExitDialog = false }
    )
}

@Composable
private fun TopAccountAction(
    user: FirebaseUser?,
    displayName: String,
    onProfileClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user == null) {
            OutlinedButton(
                onClick = onLoginClick,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Iniciar sesion", fontWeight = FontWeight.Bold)
            }
        } else {
            ElevatedCard(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clickable(onClick = onProfileClick),
                shape = RoundedCornerShape(999.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photoUrl = user.photoUrl?.toString().orEmpty()
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RecipeForest)
                            .border(BorderStroke(2.dp, RecipeCream), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUrl.isBlank()) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = RecipeCream,
                                modifier = Modifier.size(26.dp)
                            )
                        } else {
                            Image(
                                painter = rememberAsyncImagePainter(photoUrl),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(text = displayName, fontSize = 15.sp, fontWeight = FontWeight.Black, color = RecipeInk)
                        Text(text = "Perfil", color = RecipeForest, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
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
