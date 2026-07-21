package com.example.proyeto2.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.proyeto2.ui.components.AppConfirmDialog
import com.example.proyeto2.ui.components.AppPrimaryButton
import com.example.proyeto2.ui.components.AppScreenTitle
import com.example.proyeto2.ui.components.AppTextFieldColors
import com.example.proyeto2.ui.theme.GradientSofisticado
import com.example.proyeto2.ui.theme.RecipeCream
import com.example.proyeto2.ui.theme.RecipeForest
import com.example.proyeto2.ui.theme.RecipeMuted
import com.example.proyeto2.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState = authViewModel.uiState
    val user = uiState.user
    var name by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var showPasswordEditor by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val hasSession = auth.currentUser != null

    LaunchedEffect(Unit) {
        if (hasSession) {
            authViewModel.loadCurrentUser()
        } else {
            navController.navigate("LoginScreen")
        }
    }

    LaunchedEffect(user?.uid, user?.name, user?.photoUrl) {
        if (!user?.name.isNullOrBlank()) {
            name = user?.name.orEmpty()
        }
        photoUrl = user?.photoUrl.orEmpty()
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage?.contains("Contrasena actualizada", ignoreCase = true) == true) {
            currentPassword = ""
            newPassword = ""
            repeatPassword = ""
            showPasswordEditor = false
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
                            ProfilePhotoPreview(photoUrl = photoUrl)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = name.ifBlank { "Usuario" },
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
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
                                value = photoUrl,
                                onValueChange = { photoUrl = it },
                                label = { Text("URL de foto de perfil") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
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
                                        photoUrl = photoUrl
                                    )
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

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { showPasswordEditor = !showPasswordEditor },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RecipeForest),
                                border = BorderStroke(1.dp, RecipeForest)
                            ) {
                                Icon(Icons.Filled.LockReset, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Actualizar contrasena", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = { authViewModel.sendPasswordReset(user?.email.orEmpty()) },
                                enabled = !uiState.isLoading
                            ) {
                                Icon(Icons.Filled.LockReset, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviar link para recuperar cuenta")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cerrar sesion", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (showPasswordEditor) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PasswordUpdateCard(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            repeatPassword = repeatPassword,
                            isLoading = uiState.isLoading,
                            onCurrentPasswordChange = { currentPassword = it },
                            onNewPasswordChange = { newPassword = it },
                            onRepeatPasswordChange = { repeatPassword = it },
                            onCancel = {
                                currentPassword = ""
                                newPassword = ""
                                repeatPassword = ""
                                showPasswordEditor = false
                            },
                            onSave = {
                                authViewModel.updatePassword(
                                    currentPassword = currentPassword,
                                    newPassword = newPassword,
                                    repeatPassword = repeatPassword
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    AppConfirmDialog(
        visible = showLogoutDialog,
        title = "Cerrar sesion",
        message = "Estas seguro de que deseas cerrar tu sesion?",
        confirmText = "Cerrar sesion",
        dismissText = "Cancelar",
        onConfirm = {
            showLogoutDialog = false
            auth.signOut()
            navController.navigate("HomeScreen") {
                popUpTo("HomeScreen") { inclusive = false }
                launchSingleTop = true
            }
        },
        onDismiss = { showLogoutDialog = false }
    )
}

@Composable
private fun PasswordUpdateCard(
    currentPassword: String,
    newPassword: String,
    repeatPassword: String,
    isLoading: Boolean,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Text(
                text = "Actualizar contrasena",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Confirma tu contrasena actual para proteger tu cuenta.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            PasswordField(
                value = currentPassword,
                onValueChange = onCurrentPasswordChange,
                label = "Contrasena actual"
            )

            Spacer(modifier = Modifier.height(14.dp))

            PasswordField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = "Nueva contrasena"
            )

            Spacer(modifier = Modifier.height(14.dp))

            PasswordField(
                value = repeatPassword,
                onValueChange = onRepeatPasswordChange,
                label = "Confirmar nueva contrasena"
            )

            Spacer(modifier = Modifier.height(22.dp))

            AppPrimaryButton(
                onClick = onSave,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = RecipeCream)
                } else {
                    Text("Guardar nueva contrasena", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        colors = AppTextFieldColors()
    )
}

@Composable
private fun ProfilePhotoPreview(
    photoUrl: String
) {
    if (photoUrl.isBlank()) {
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
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .border(BorderStroke(4.dp, RecipeCream), CircleShape),
            contentScale = ContentScale.Crop
        )
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
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
    }
}
