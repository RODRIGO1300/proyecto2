package com.example.proyeto2.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val RecipeInk = Color(0xFF17231F)
val RecipeForest = Color(0xFF2F6F4E)
val RecipeLeaf = Color(0xFF76A96B)
val RecipeCoral = Color(0xFFE27A5F)
val RecipeHoney = Color(0xFFF1C36D)
val RecipeCream = Color(0xFFFFF8ED)
val RecipeSurface = Color(0xFFF7EBDD)
val RecipeMuted = Color(0xFF6E7C72)
val RecipeError = Color(0xFFB9473A)
val RecipeSky = Color(0xFF2F80ED)
val RecipeOcean = Color(0xFF00A8A8)
val RecipeBerry = Color(0xFFC0397D)
val RecipeTomato = Color(0xFFE94B35)
val RecipeMint = Color(0xFF4ECDC4)
val RecipeGrape = Color(0xFF5142A6)
val RecipeDarkSurface = Color(0xFF22302A)
val RecipeDarkSurfaceVariant = Color(0xFF314139)
val RecipeLightSurfaceVariant = Color(0xFFEADCCC)

val RecipeBackgroundGradient = Brush.linearGradient(
    listOf(RecipeInk, RecipeForest, RecipeCoral, RecipeHoney)
)

val RecipePanelGradient = Brush.linearGradient(
    listOf(RecipeForest, RecipeLeaf, RecipeHoney)
)

val RecipeCalmGradient = Brush.linearGradient(
    listOf(RecipeInk, RecipeForest, RecipeLeaf)
)

val LoginGradient = Brush.linearGradient(
    listOf(RecipeSky, RecipeOcean, RecipeInk)
)

val RegisterGradient = Brush.linearGradient(
    listOf(RecipeCoral, RecipeHoney, RecipeForest)
)

val MenuGradient = Brush.linearGradient(
    listOf(RecipeForest, RecipeMint, RecipeHoney)
)

val RecipesGradient = Brush.linearGradient(
    listOf(RecipeTomato, RecipeHoney, RecipeMint, RecipeSky)
)

val RecipeDetailGradient = Brush.linearGradient(
    listOf(RecipeBerry, RecipeTomato, RecipeHoney)
)

val FavoritesGradient = Brush.linearGradient(
    listOf(RecipeBerry, RecipeCoral, RecipeCream)
)

val PlannerGradient = Brush.linearGradient(
    listOf(RecipeOcean, RecipeForest, RecipeLeaf)
)

val ProfileGradient = Brush.linearGradient(
    listOf(RecipeGrape, RecipeSky, RecipeOcean)
)

val ApiCreditsGradient = Brush.linearGradient(
    listOf(RecipeInk, RecipeGrape, RecipeBerry, RecipeHoney)
)

val Purple80 = RecipeSurface
val PurpleGrey80 = RecipeLeaf
val Pink80 = RecipeCoral

val Purple40 = RecipeForest
val PurpleGrey40 = RecipeMuted
val Pink40 = RecipeCoral

val coffe = RecipeInk
val oliva = RecipeForest
val terracota = RecipeCoral
val carbon = RecipeInk
val mostasa = RecipeHoney
val oscuro = RecipeInk
val medio = RecipeMuted
val neutro = RecipeForest

val GradientTierra = RecipeDetailGradient
val GradientNaturaleza = PlannerGradient
val GradientSofisticado = ProfileGradient
val GradientOtono = MenuGradient
val GradientProfundo = RecipeCalmGradient
val GradientAurora = FavoritesGradient
val GradientCrepusculo = RecipeBackgroundGradient
val GradientEstatico = RecipeCalmGradient
val GradientVital = RegisterGradient
val GradientClaroOscuro = RecipesGradient
val GradientTotal = LoginGradient
