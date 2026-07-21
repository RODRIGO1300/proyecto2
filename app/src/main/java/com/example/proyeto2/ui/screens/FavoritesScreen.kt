package com.example.proyeto2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.models.favorite.FavoriteMeal
import com.example.proyeto2.ui.components.AppBackButton
import com.example.proyeto2.ui.components.AppScreenTitle
import com.example.proyeto2.ui.theme.GradientAurora
import com.example.proyeto2.viewmodel.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val uiState = favoriteViewModel.uiState
    var editingFavorite by remember { mutableStateOf<FavoriteMeal?>(null) }
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            favoriteViewModel.observeFavorites()
        } else {
            navController.navigate("LoginScreen")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientAurora)
            .safeDrawingPadding()
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBackButton(navController = navController)
        }

        Spacer(modifier = Modifier.height(18.dp))

        AppScreenTitle(
            title = "Favoritos",
            subtitle = "Recetas guardadas por tu usuario"
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Cargando favoritos...", color = Color.White)
                }
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            uiState.favorites.isEmpty() -> {
                Text(
                    text = "Todavia no tienes recetas favoritas.",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.favorites) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onEdit = { editingFavorite = favorite },
                            onDelete = { favoriteViewModel.removeFavorite(favorite.idMeal) }
                        )
                    }
                }
            }
        }
    }

    editingFavorite?.let { favorite ->
        FavoriteNoteDialog(
            favorite = favorite,
            onDismiss = { editingFavorite = null },
            onSave = { note ->
                favoriteViewModel.updateFavoriteNote(favorite.idMeal, note)
                editingFavorite = null
            }
        )
    }
}

@Composable
private fun FavoriteCard(
    favorite: FavoriteMeal,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(favorite.imagen),
                contentDescription = favorite.nombre,
                modifier = Modifier
                    .size(92.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Categoria: ${favorite.categoria.ifBlank { "Sin dato" }}")
                Text(text = "Pais: ${favorite.pais.ifBlank { "Sin dato" }}")
                Text(text = "Nota: ${favorite.nota.ifBlank { "Sin nota" }}")
                Text(text = "Fecha: ${favorite.fecha.toShortDate()}")
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar nota")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar favorito")
            }
        }
    }
}

@Composable
private fun FavoriteNoteDialog(
    favorite: FavoriteMeal,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember(favorite.idMeal) { mutableStateOf(favorite.nota) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nota") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Nota personal") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(note) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun Long.toShortDate(): String {
    if (this <= 0L) return "Sin fecha"
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
}
