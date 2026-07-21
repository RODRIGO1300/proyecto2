package com.example.proyeto2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.models.favorite.FavoriteMeal
import com.example.proyeto2.models.planner.MealPlanSlot
import com.example.proyeto2.ui.components.AppBackButton
import com.example.proyeto2.ui.components.AppScreenTitle
import com.example.proyeto2.ui.components.AppTextFieldColors
import com.example.proyeto2.ui.theme.GradientNaturaleza
import com.example.proyeto2.utils.PdfUtils
import com.example.proyeto2.viewmodel.FavoriteViewModel
import com.example.proyeto2.viewmodel.PlannerViewModel
import com.google.firebase.auth.FirebaseAuth

private val weekDays = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
private val mealTimes = listOf("Almuerzo", "Comida", "Cena")

@Composable
fun PlannerScreen(navController: NavHostController,
    plannerViewModel: PlannerViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val context = LocalContext.current
    val plannerState = plannerViewModel.uiState
    val favoriteState = favoriteViewModel.uiState
    var selectedDay by remember { mutableStateOf<String?>(null) }
    var selectedMealTime by remember { mutableStateOf<String?>(null) }
    var commentSlot by remember { mutableStateOf<MealPlanSlot?>(null) }
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            plannerViewModel.observePlanner()
            favoriteViewModel.observeFavorites()
        } else {
            navController.navigate("LoginScreen")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientNaturaleza)
            .safeDrawingPadding()
            .padding(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AppBackButton(navController = navController)
        }

        Spacer(modifier = Modifier.height(18.dp))

        AppScreenTitle(
            title = "Planeador semanal",
            subtitle = "Asigna platillos favoritos para almuerzo, comida y cena"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                PdfUtils.crearReportePlaneadorSemanal(
                    context = context,
                    nombreArchivo = "Plan_Semanal_De_Comidas",
                    slots = plannerState.slots
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Descargar reporte PDF", fontWeight = FontWeight.Bold)
        }

        plannerState.errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (plannerState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Cargando planeador...", color = Color.White)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(weekDays) { day ->
                    DayPlannerCard(
                        day = day,
                        plannerViewModel = plannerViewModel,
                        onSelect = { mealTime ->
                            selectedDay = day
                            selectedMealTime = mealTime
                        },
                        onComment = { slot -> commentSlot = slot }
                    )
                }
            }
        }
    }

    if (selectedDay != null && selectedMealTime != null) {
        FavoritePickerDialog(
            favorites = favoriteState.favorites,
            day = selectedDay.orEmpty(),
            mealTime = selectedMealTime.orEmpty(),
            onDismiss = {
                selectedDay = null
                selectedMealTime = null
            },
            onSelect = { favorite ->
                plannerViewModel.assignMeal(selectedDay.orEmpty(), selectedMealTime.orEmpty(), favorite)
                selectedDay = null
                selectedMealTime = null
            }
        )
    }

    commentSlot?.let { slot ->
        PlannerCommentDialog(
            slot = slot,
            onDismiss = { commentSlot = null },
            onSave = { comments ->
                plannerViewModel.updateComments(slot.dia, slot.tiempo, comments)
                commentSlot = null
            }
        )
    }
}

@Composable
private fun DayPlannerCard(
    day: String,
    plannerViewModel: PlannerViewModel,
    onSelect: (String) -> Unit,
    onComment: (MealPlanSlot) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = day, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(10.dp))

            mealTimes.forEach { mealTime ->
                val slot = plannerViewModel.slotFor(day, mealTime)
                MealTimeSlot(
                    title = mealTime,
                    slot = slot,
                    onSelect = { onSelect(mealTime) },
                    onComment = { slot?.let(onComment) },
                    onClear = { plannerViewModel.clearSlot(day, mealTime) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun MealTimeSlot(
    title: String,
    slot: MealPlanSlot?,
    onSelect: () -> Unit,
    onComment: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .clickable(onClick = onSelect)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (slot?.imagen.isNullOrBlank()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Black)
                Text(text = "Toca para elegir platillo")
            }
        } else {
            Image(
                painter = rememberAsyncImagePainter(slot?.imagen),
                contentDescription = slot?.nombre,
                modifier = Modifier
                    .size(62.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Black)
                Text(
                    text = slot?.nombre.orEmpty(),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!slot?.comentarios.isNullOrBlank()) {
                    Text(
                        text = "Comentario: ${slot?.comentarios.orEmpty()}",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onComment) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar comentario")
            }
            IconButton(onClick = onClear) {
                Icon(Icons.Filled.Close, contentDescription = "Limpiar")
            }
        }
    }
}

@Composable
private fun FavoritePickerDialog(
    favorites: List<FavoriteMeal>,
    day: String,
    mealTime: String,
    onDismiss: () -> Unit,
    onSelect: (FavoriteMeal) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$day - $mealTime") },
        text = {
            if (favorites.isEmpty()) {
                Text("Primero agrega recetas a favoritos para poder asignarlas.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(favorites) { favorite ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelect(favorite) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(favorite.imagen),
                                contentDescription = favorite.nombre,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = favorite.nombre,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun PlannerCommentDialog(
    slot: MealPlanSlot,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var comments by remember(slot.dia, slot.tiempo, slot.idMeal) { mutableStateOf(slot.comentarios) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${slot.dia} - ${slot.tiempo}") },
        text = {
            Column {
                Text(text = slot.nombre, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppTextFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(comments) }) {
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
