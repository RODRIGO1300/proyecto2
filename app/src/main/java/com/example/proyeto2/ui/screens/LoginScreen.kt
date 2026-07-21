package com.example.proyeto2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyeto2.R
import com.example.proyeto2.ui.components.AppBackButton
import com.example.proyeto2.ui.components.AppFormCard
import com.example.proyeto2.ui.components.AppPrimaryButton
import com.example.proyeto2.ui.components.AppTextFieldColors
import com.example.proyeto2.ui.theme.GradientTotal
import com.example.proyeto2.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var failedLoginAttempts by remember { mutableStateOf(0) }
    var waitingForLoginResult by remember { mutableStateOf(false) }
    val uiState = authViewModel.uiState

    LaunchedEffect(uiState.isAuthenticated, uiState.isEmailVerified) {
        if (uiState.isAuthenticated && uiState.isEmailVerified) {
            failedLoginAttempts = 0
            waitingForLoginResult = false
            navHostController.navigate("HomeScreen") {
                popUpTo("HomeScreen") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(uiState.isLoading, uiState.errorMessage) {
        if (waitingForLoginResult && !uiState.isLoading && uiState.errorMessage != null) {
            failedLoginAttempts += 1
            waitingForLoginResult = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientTotal)
                .safeDrawingPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.dp)
            ) {
                AppBackButton(navController = navHostController)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AppFormCard {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo de Recetario",
                            modifier = Modifier
                                .size(104.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "INICIAR SESION",
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Cursive,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(34.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo electronico") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = AppTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contrasena") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = AppTextFieldColors()
                        )

                        uiState.errorMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        uiState.successMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (failedLoginAttempts >= 3) {
                            Spacer(modifier = Modifier.height(12.dp))

                            TextButton(
                                onClick = { authViewModel.sendPasswordReset(email) },
                                enabled = !uiState.isLoading
                            ) {
                                Text("Recuperar cuenta por correo")
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        AppPrimaryButton(
                            onClick = {
                                waitingForLoginResult = true
                                authViewModel.login(
                                    email = email,
                                    password = password
                                )
                            },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TextButton(
                            onClick = {
                                authViewModel.clearMessage()
                                navHostController.navigate("RegisterScreen") {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text("No tienes cuenta? Registrate", fontSize = 16.sp)
                        }
                }
            }
        }
    }
}
