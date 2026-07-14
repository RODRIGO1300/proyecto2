package com.example.proyeto2.models.user

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
    val isEmailVerificationSent: Boolean = false,
    val isPasswordResetSent: Boolean = false
)
