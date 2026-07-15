package com.example.proyeto2.repository

import com.example.proyeto2.models.user.AuthResult
import com.example.proyeto2.models.user.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun registerWithEmailVerification(
        name: String,
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { registerTask ->
                if (!registerTask.isSuccessful) {
                    onResult(AuthResult.Error(registerTask.exception?.message ?: "No se pudo crear la cuenta."))
                    return@addOnCompleteListener
                }

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    onResult(AuthResult.Error("No se encontro el usuario creado."))
                    return@addOnCompleteListener
                }

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener {
                        firebaseUser.sendEmailVerification()
                            .addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    onResult(
                                        AuthResult.Success(
                                            AuthUser(
                                                uid = firebaseUser.uid,
                                                name = firebaseUser.displayName.orEmpty().ifBlank { name },
                                                email = firebaseUser.email.orEmpty(),
                                                photoUrl = firebaseUser.photoUrl?.toString().orEmpty(),
                                                emailVerified = firebaseUser.isEmailVerified
                                            )
                                        )
                                    )
                                } else {
                                    onResult(
                                        AuthResult.Error(
                                            verificationTask.exception?.message
                                                ?: "La cuenta se creo, pero no se pudo enviar el correo de verificacion."
                                        )
                                    )
                                }
                            }
                    }
            }
    }

    fun login(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { loginTask ->
                if (!loginTask.isSuccessful) {
                    onResult(AuthResult.Error(loginTask.exception?.message ?: "No se pudo iniciar sesion."))
                    return@addOnCompleteListener
                }

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    onResult(AuthResult.Error("No se encontro el usuario autenticado."))
                    return@addOnCompleteListener
                }

                firebaseUser.reload()
                    .addOnCompleteListener {
                        val refreshedUser = auth.currentUser ?: firebaseUser
                        if (!refreshedUser.isEmailVerified) {
                            refreshedUser.sendEmailVerification()
                            onResult(AuthResult.Error("Tu correo aun no esta verificado. Te enviamos otro correo de verificacion."))
                            return@addOnCompleteListener
                        }

                        onResult(
                            AuthResult.Success(
                                AuthUser(
                                    uid = refreshedUser.uid,
                                    name = refreshedUser.displayName.orEmpty(),
                                    email = refreshedUser.email.orEmpty(),
                                    photoUrl = refreshedUser.photoUrl?.toString().orEmpty(),
                                    emailVerified = refreshedUser.isEmailVerified
                                )
                            )
                        )
                    }
            }
    }

    fun getCurrentUser(): AuthUser? {
        val user = auth.currentUser ?: return null
        return AuthUser(
            uid = user.uid,
            name = user.displayName.orEmpty(),
            email = user.email.orEmpty(),
            photoUrl = user.photoUrl?.toString().orEmpty(),
            emailVerified = user.isEmailVerified
        )
    }

    fun updateProfile(
        name: String,
        newPassword: String?,
        onResult: (AuthResult) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onResult(AuthResult.Error("No hay usuario autenticado."))
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { profileTask ->
                if (!profileTask.isSuccessful) {
                    onResult(AuthResult.Error(profileTask.exception?.message ?: "No se pudo actualizar el nombre."))
                    return@addOnCompleteListener
                }

                if (newPassword.isNullOrBlank()) {
                    onResult(AuthResult.Success(user.toAuthUser(name)))
                    return@addOnCompleteListener
                }

                user.updatePassword(newPassword)
                    .addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            onResult(AuthResult.Success(user.toAuthUser(name)))
                        } else {
                            onResult(
                                AuthResult.Error(
                                    passwordTask.exception?.message
                                        ?: "Nombre actualizado, pero no se pudo cambiar la contrasena."
                                )
                            )
                        }
                    }
            }
    }

    private fun com.google.firebase.auth.FirebaseUser.toAuthUser(name: String? = null): AuthUser {
        return AuthUser(
            uid = uid,
            name = name ?: displayName.orEmpty(),
            email = email.orEmpty(),
            photoUrl = photoUrl?.toString().orEmpty(),
            emailVerified = isEmailVerified
        )
    }
}
