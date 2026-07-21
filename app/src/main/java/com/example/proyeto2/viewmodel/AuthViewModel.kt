package com.example.proyeto2.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyeto2.models.user.AuthResult
import com.example.proyeto2.models.user.AuthUiState
import com.example.proyeto2.repository.AuthRepository

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun loadCurrentUser() {
        val currentUser = repository.getCurrentUser()
        uiState = uiState.copy(
            user = currentUser,
            isAuthenticated = currentUser != null,
            isEmailVerified = currentUser?.emailVerified == true
        )
    }

    fun register(name: String, email: String, password: String) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()

        when {
            cleanName.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Ingresa tu nombre de usuario.")
                return
            }

            cleanEmail.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Ingresa tu correo electronico.")
                return
            }

            password.length < 6 -> {
                uiState = uiState.copy(errorMessage = "La contrasena debe tener al menos 6 caracteres.")
                return
            }
        }

        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            isAuthenticated = false,
            isEmailVerified = false,
            isEmailVerificationSent = false
        )

        repository.registerWithEmailVerification(
            name = cleanName,
            email = cleanEmail,
            password = password
        ) { result ->
            uiState = when (result) {
                is AuthResult.Success -> uiState.copy(
                    isLoading = false,
                    user = result.user,
                    errorMessage = null,
                    successMessage = "Cuenta creada. Revisa tu correo para verificar tu perfil.",
                    isAuthenticated = false,
                    isEmailVerified = result.user.emailVerified,
                    isEmailVerificationSent = true
                )

                is AuthResult.Error -> uiState.copy(
                    isLoading = false,
                    errorMessage = result.message,
                    successMessage = null,
                    isAuthenticated = false,
                    isEmailVerified = false,
                    isEmailVerificationSent = false
                )

                AuthResult.Cancelled -> uiState.copy(
                    isLoading = false,
                    errorMessage = "Registro cancelado.",
                    successMessage = null,
                    isAuthenticated = false,
                    isEmailVerified = false,
                    isEmailVerificationSent = false
                )
            }
        }
    }

    fun login(email: String, password: String) {
        val cleanEmail = email.trim()

        when {
            cleanEmail.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Ingresa tu correo electronico.")
                return
            }

            password.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Ingresa tu contrasena.")
                return
            }
        }

        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            isAuthenticated = false,
            isEmailVerified = false
        )

        repository.login(
            email = cleanEmail,
            password = password
        ) { result ->
            uiState = when (result) {
                is AuthResult.Success -> uiState.copy(
                    isLoading = false,
                    user = result.user,
                    errorMessage = null,
                    successMessage = "Correo verificado. Entrando...",
                    isAuthenticated = true,
                    isEmailVerified = result.user.emailVerified,
                    isEmailVerificationSent = false
                )

                is AuthResult.Error -> uiState.copy(
                    isLoading = false,
                    errorMessage = result.message,
                    successMessage = null,
                    isAuthenticated = false,
                    isEmailVerified = false,
                    isEmailVerificationSent = result.message.contains("verificacion", ignoreCase = true)
                )

                AuthResult.Cancelled -> uiState.copy(
                    isLoading = false,
                    errorMessage = "Inicio de sesion cancelado.",
                    successMessage = null,
                    isAuthenticated = false,
                    isEmailVerified = false
                )
            }
        }
    }

    fun clearMessage() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }

    fun updateProfile(name: String, password: String, repeatPassword: String, photoUri: Uri? = null) {
        val cleanName = name.trim()
        val cleanPassword = password.trim()
        val cleanRepeatPassword = repeatPassword.trim()

        when {
            cleanName.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Ingresa tu nombre.")
                return
            }

            cleanPassword.isNotBlank() && cleanPassword.length < 6 -> {
                uiState = uiState.copy(errorMessage = "La nueva contrasena debe tener al menos 6 caracteres.")
                return
            }

            cleanPassword != cleanRepeatPassword -> {
                uiState = uiState.copy(errorMessage = "Las contrasenas no coinciden.")
                return
            }
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)
        repository.updateProfile(
            name = cleanName,
            newPassword = cleanPassword.ifBlank { null },
            photoUri = photoUri
        ) { result ->
            uiState = when (result) {
                is AuthResult.Success -> uiState.copy(
                    isLoading = false,
                    user = result.user,
                    successMessage = "Perfil actualizado correctamente.",
                    errorMessage = null
                )

                is AuthResult.Error -> uiState.copy(
                    isLoading = false,
                    errorMessage = result.message,
                    successMessage = null
                )

                AuthResult.Cancelled -> uiState.copy(
                    isLoading = false,
                    errorMessage = "Actualizacion cancelada.",
                    successMessage = null
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            uiState = uiState.copy(errorMessage = "Ingresa tu correo para enviar el link.")
            return
        }

        uiState = uiState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            isPasswordResetSent = false
        )

        repository.sendPasswordResetEmail(cleanEmail) { result ->
            uiState = when (result) {
                is AuthResult.Success -> uiState.copy(
                    isLoading = false,
                    successMessage = "Te enviamos un link para recuperar la cuenta y cambiar la contrasena.",
                    errorMessage = null,
                    isPasswordResetSent = true
                )

                is AuthResult.Error -> uiState.copy(
                    isLoading = false,
                    errorMessage = result.message,
                    successMessage = null,
                    isPasswordResetSent = false
                )

                AuthResult.Cancelled -> uiState.copy(
                    isLoading = false,
                    errorMessage = "Recuperacion cancelada.",
                    successMessage = null,
                    isPasswordResetSent = false
                )
            }
        }
    }
}
