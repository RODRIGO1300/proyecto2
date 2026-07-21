package com.example.proyeto2.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyeto2.ui.theme.RecipeBackgroundGradient
import com.example.proyeto2.ui.theme.RecipeCream
import com.example.proyeto2.ui.theme.RecipeForest
import com.example.proyeto2.ui.theme.RecipeInk
import com.example.proyeto2.ui.theme.RecipeMuted
import com.example.proyeto2.ui.theme.RecipeSurface

@Composable
fun AppGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RecipeBackgroundGradient)
            .safeDrawingPadding()
            .padding(18.dp)
    ) {
        content()
    }
}

@Composable
fun AppBackButton(
    navController: NavHostController,
    text: String = "Volver"
) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            val didPop = navController.popBackStack()
            if (!didPop) {
                (context as? Activity)?.finish()
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun AppPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = RecipeCream
        ),
        content = content
    )
}

@Composable
fun AppFormCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 430.dp),
        shape = RoundedCornerShape(18.dp),
        color = RecipeCream.copy(alpha = 0.94f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun AppTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = RecipeInk,
    unfocusedTextColor = RecipeInk,
    focusedLabelColor = RecipeForest,
    unfocusedLabelColor = RecipeMuted,
    focusedBorderColor = RecipeForest,
    unfocusedBorderColor = RecipeSurface
)

@Composable
fun AppScreenTitle(
    title: String,
    subtitle: String? = null
) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Black
    )
    subtitle?.let {
        Text(
            text = it,
            color = Color.White.copy(alpha = 0.86f),
            fontWeight = FontWeight.SemiBold
        )
    }
}
