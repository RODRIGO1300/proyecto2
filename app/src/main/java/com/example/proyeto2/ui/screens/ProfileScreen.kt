package com.example.proyeto2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyeto2.ui.theme.GradientTotal
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

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    LaunchedEffect(user?.uid, user?.name) {
        if (!user?.name.isNullOrBlank()) {
            name = user?.name.orEmpty()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientTotal)
                .safeDrawingPadding()
                .imePadding()
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver")
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 430.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ProfilePhoto(photoUrl = user?.photoUrl.orEmpty())

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = name.ifBlank { "Usuario" },
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Cursive
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Nueva contrasena") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = repeatPassword,
                                onValueChange = { repeatPassword = it },
                                label = { Text("Repetir contrasena") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true
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

                            Spacer(modifier = Modifier.height(30.dp))

                            Button(
                                onClick = {
                                    authViewModel.updateProfile(
                                        name = name,
                                        password = password,
                                        repeatPassword = repeatPassword
                                    )
                                    password = ""
                                    repeatPassword = ""
                                },
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator()
                                } else {
                                    Text("Guardar cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilePhoto(photoUrl: String) {
    if (photoUrl.isBlank()) {
        Box(
            modifier = Modifier
                .size(94.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }
    } else {
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(94.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
