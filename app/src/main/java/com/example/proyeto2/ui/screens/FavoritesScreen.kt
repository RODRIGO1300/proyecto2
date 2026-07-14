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
import com.example.proyeto2.utils.PdfUtils

@Composable
fun FavoritesScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Favoritos", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Aqui ira el CRUD de recetas favoritas.")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EXIT", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                PdfUtils.crearYGuardarPdfEnDescargas(
                    context = context,
                    nombreArchivo = "Receta_Favorita_Prueba",
                    titulo = "Receta de Prueba",
                    subtitulo = "Desde Favoritos",
                    contenido = """
                Ingredientes:
                • 200g de pasta
                • 100g de queso parmesano
                • 2 dientes de ajo
                • Aceite de oliva
                • Sal y pimienta
                
                Preparación:
                1. Cocina la pasta en agua con sal.
                2. Sofríe el ajo en aceite.
                3. Mezcla todo y agrega el queso.
                
                ¡Buen provecho!
            """.trimIndent()
                )
            },
            modifier = Modifier.align(Alignment.BottomStart),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Generar PDF", style = MaterialTheme.typography.labelSmall)
        }
    }
}
