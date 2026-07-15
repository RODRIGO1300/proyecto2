package com.example.proyeto2.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.R
import com.example.proyeto2.models.meal.Meal
import com.example.proyeto2.models.meal.ingredientMeasures
import com.example.proyeto2.ui.theme.GradientTierra
import com.example.proyeto2.viewmodel.FavoriteViewModel
import com.example.proyeto2.viewmodel.MealViewModel

@Composable
fun MealDetailScreen(
    navController: NavHostController,
    mealId: String?,
    mealViewModel: MealViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val uiState = mealViewModel.uiState
    val favoriteState = favoriteViewModel.uiState

    LaunchedEffect(mealId) {
        mealViewModel.loadMealDetail(mealId)
        favoriteViewModel.observeFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientTierra)
            .safeDrawingPadding()
            .padding(18.dp)
    ) {
        OutlinedButton(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(14.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Cargando detalle...", color = Color.White)
                    }
                }
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            uiState.selectedMeal != null -> {
                MealDetailContent(
                    meal = uiState.selectedMeal,
                    isFavorite = favoriteViewModel.isFavorite(uiState.selectedMeal.idMeal),
                    favoriteMessage = favoriteState.successMessage ?: favoriteState.errorMessage,
                    isFavoriteError = favoriteState.errorMessage != null,
                    onFavoriteClick = { favoriteViewModel.toggleFavorite(uiState.selectedMeal) }
                )
            }
        }
    }
}

@Composable
private fun MealDetailContent(
    meal: Meal,
    isFavorite: Boolean,
    favoriteMessage: String?,
    isFavoriteError: Boolean,
    onFavoriteClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = meal.name.orEmpty(),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "${meal.category.orEmpty().ifBlank { "Sin categoria" }} - ${meal.area.orEmpty().ifBlank { "Sin pais" }}",
                color = Color.White.copy(alpha = 0.88f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onFavoriteClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
                    contentColor = if (isFavorite) Color.White else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isFavorite) "En favoritos" else "Agregar a favoritos")
            }
            favoriteMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = if (isFavoriteError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Image(
                painter = rememberAsyncImagePainter(meal.imageUrl),
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        item {
            DetailSection(title = "Ingredientes") {
                val ingredients = meal.ingredientMeasures()
                if (ingredients.isEmpty()) {
                    Text(text = "Sin ingredientes disponibles.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ingredients.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = item.measure.ifBlank { "-" })
                            }
                        }
                    }
                }
            }
        }

        item {
            DetailSection(title = "Indicaciones") {
                Text(text = meal.instructions.orEmpty().ifBlank { "Sin indicaciones disponibles." })
            }
        }

        item {
            DetailSection(title = "Tutorial") {
                val embedUrl = meal.youtubeUrl.toYouTubeEmbedUrl()
                if (embedUrl == null) {
                    Image(
                        painter = painterResource(id = R.drawable.nodisponible),
                        contentDescription = "Tutorial no disponible",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                loadUrl(embedUrl)
                            }
                        },
                        update = { webView ->
                            if (webView.url != embedUrl) {
                                webView.loadUrl(embedUrl)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

private fun String?.toYouTubeEmbedUrl(): String? {
    val url = this?.trim().orEmpty()
    if (url.isBlank()) return null

    val videoId = when {
        "watch?v=" in url -> url.substringAfter("watch?v=").substringBefore("&")
        "youtu.be/" in url -> url.substringAfter("youtu.be/").substringBefore("?")
        "embed/" in url -> url.substringAfter("embed/").substringBefore("?")
        else -> null
    }

    return videoId?.takeIf { it.isNotBlank() }?.let { "https://www.youtube.com/embed/$it" }
}
