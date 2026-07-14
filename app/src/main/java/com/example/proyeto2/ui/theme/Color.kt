package com.example.proyeto2.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val coffe = Color(red = 44, green = 24, blue = 16)
val oliva = Color(red = 85, green = 107, blue = 47)
val terracota = Color(red = 224, green = 122, blue = 95)
val carbon = Color(red = 51, green = 51, blue = 51)
val mostasa = Color(red = 242, green = 204, blue = 143)
val oscuro = Color(red = 31, green = 41, blue = 55)
val medio = Color(red = 107, green = 114, blue = 128)
val neutro = Color(red = 34, green = 85, blue = 102)

val GradientTierra = Brush.linearGradient(listOf(coffe, terracota, mostasa))
val GradientNaturaleza = Brush.linearGradient(listOf(oscuro, neutro, oliva))
val GradientSofisticado = Brush.linearGradient(listOf(carbon, medio, oscuro))
val GradientOtono = Brush.linearGradient(listOf(terracota, mostasa))
val GradientProfundo = Brush.linearGradient(listOf(coffe, oliva))

val GradientAurora = Brush.linearGradient(listOf(oscuro, neutro, mostasa)) // De la oscuridad al amanecer
val GradientCrepusculo = Brush.linearGradient(listOf(oscuro, terracota, coffe)) // El cielo enfriándose tras el calor
val GradientEstatico = Brush.linearGradient(listOf(carbon, medio, neutro)) // Frialdad industrial y concreto
val GradientVital = Brush.linearGradient(listOf(oliva, mostasa, terracota)) // La energía de la vegetación y el sol
val GradientClaroOscuro = Brush.linearGradient(listOf(mostasa, carbon)) // Contraste máximo de luminosidad

val GradientTotal = Brush.linearGradient(
    listOf(oscuro, coffe, carbon, neutro, oliva, medio, terracota, mostasa)
)