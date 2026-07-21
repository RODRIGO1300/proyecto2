package com.example.proyeto2.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.ui.components.AppBackButton
import com.example.proyeto2.ui.components.AppPrimaryButton
import com.example.proyeto2.ui.components.AppScreenTitle
import com.example.proyeto2.ui.components.AppTextFieldColors
import com.example.proyeto2.ui.theme.GradientSofisticado
import com.example.proyeto2.ui.theme.RecipeCream
import com.example.proyeto2.ui.theme.RecipeForest
import com.example.proyeto2.ui.theme.RecipeInk
import com.example.proyeto2.ui.theme.RecipeMuted
import com.example.proyeto2.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState = authViewModel.uiState
    val user = uiState.user
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPhotoUri = uri
    }

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    LaunchedEffect(user?.uid, user?.name) {
        if (!user?.name.isNullOrBlank()) {
            name = user?.name.orEmpty()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage?.contains("Perfil actualizado", ignoreCase = true) == true) {
            selectedPhotoUri = null
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientSofisticado)
                .safeDrawingPadding()
                .imePadding()
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
                        title = "Mi perfil",
                        subtitle = "Actualiza tu informacion y seguridad"
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
                            ProfilePhotoPicker(
                                photoModel = selectedPhotoUri ?: user?.photoUrl.orEmpty(),
                                onSelectPhoto = { photoPicker.launch("image/*") }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = name.ifBlank { "Usuario" },
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = RecipeInk
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ProfileInfoRow(
                                icon = Icons.Filled.Email,
                                text = user?.email.orEmpty().ifBlank { "Correo no disponible" }
                            )
                            ProfileInfoRow(
                                icon = Icons.Filled.Verified,
                                text = if (user?.emailVerified == true) "Correo verificado" else "Correo pendiente de verificar",
                                tint = if (user?.emailVerified == true) RecipeForest else MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = AppTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Nueva contrasena") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors = AppTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = repeatPassword,
                                onValueChange = { repeatPassword = it },
                                label = { Text("Confirmar contrasena") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors = AppTextFieldColors()
                            )

                            uiState.errorMessage?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            uiState.successMessage?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AppPrimaryButton(
                                onClick = {
                                    authViewModel.updateProfile(
                                        name = name,
                                        password = password,
                                        repeatPassword = repeatPassword,
                                        photoUri = selectedPhotoUri
                                    )
                                    password = ""
                                    repeatPassword = ""
                                },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(color = RecipeCream)
                                } else {
                                    Text("Guardar perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = { authViewModel.sendPasswordReset(user?.email.orEmpty()) },
                                enabled = !uiState.isLoading
                            ) {
                                Icon(Icons.Filled.LockReset, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviar link para recuperar cuenta")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilePhotoPicker(
    photoModel: Any,
    onSelectPhoto: () -> Unit
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        if (photoModel.toString().isBlank()) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(RecipeForest)
                    .border(BorderStroke(4.dp, RecipeCream), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = RecipeCream,
                    modifier = Modifier.size(72.dp)
                )
            }
        } else {
            Image(
                painter = rememberAsyncImagePainter(photoModel),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(4.dp, RecipeCream), CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        OutlinedButton(
            onClick = onSelectPhoto,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            contentPadding = ButtonDefaults.ContentPadding,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = RecipeCream,
                contentColor = RecipeForest
            ),
            border = BorderStroke(1.dp, RecipeForest)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Cambiar foto",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = RecipeMuted
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = RecipeMuted, fontWeight = FontWeight.SemiBold)
    }
}
