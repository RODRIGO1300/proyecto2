package com.example.proyeto2.models.user

data class AuthUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val emailVerified: Boolean = false
)
