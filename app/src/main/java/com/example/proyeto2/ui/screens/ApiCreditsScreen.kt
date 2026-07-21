package com.example.proyeto2.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyeto2.ui.components.AppBackButton
import com.example.proyeto2.ui.components.AppPrimaryButton
import com.example.proyeto2.ui.components.AppScreenTitle
import com.example.proyeto2.ui.theme.ApiCreditsGradient
import com.example.proyeto2.ui.theme.RecipeCream
import com.example.proyeto2.ui.theme.RecipeForest

private const val THE_MEAL_DB_URL = "https://www.themealdb.com/"

@Composable
fun ApiCreditsScreen(navController: NavHostController) {
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ApiCreditsGradient)
                .safeDrawingPadding()
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppBackButton(navController = navController)

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppScreenTitle(
                        title = "Creditos de la API",
                        subtitle = "Uso responsable de informacion publica"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 520.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Restaurant,
                                contentDescription = null,
                                tint = RecipeForest,
                                modifier = Modifier.size(72.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Gracias a TheMealDB",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Recetario utiliza informacion, imagenes y datos de recetas proporcionados por TheMealDB mediante su API publica. Este proyecto reconoce y agradece a sus creadores por mantener una fuente abierta para aprender, explorar y construir aplicaciones educativas.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 21.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            CreditPoint("Se muestra atribucion a la fuente de datos.")
                            CreditPoint("La informacion se consume respetando la capa gratuita.")
                            CreditPoint("El sitio oficial queda disponible para consulta directa.")

                            Spacer(modifier = Modifier.height(24.dp))

                            AppPrimaryButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(THE_MEAL_DB_URL))
                                    runCatching { context.startActivity(intent) }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Visitar TheMealDB", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = THE_MEAL_DB_URL,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Verified,
            contentDescription = null,
            tint = RecipeForest,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
    }
}
