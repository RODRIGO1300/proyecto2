package com.example.proyecto2.models.User

import com.example.proyeto2.models.user.AuthUser

sealed class AuthResult {

    data class Success(
        val user: AuthUser
    ) : AuthResult()

    data class Error(
        val message: String
    ) : AuthResult()

    data object Cancelled : AuthResult()
}