package com.example.proyeto2.repository

import android.net.Uri
import com.example.proyeto2.models.user.AuthResult
import com.example.proyeto2.models.user.AuthUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
                        saveUserProfile(firebaseUser.toAuthUser(name)) { saveError ->
                            if (saveError != null) {
                                onResult(AuthResult.Error(saveError))
                                return@saveUserProfile
                            }

                            firebaseUser.sendEmailVerification()
                                .addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        onResult(AuthResult.Success(firebaseUser.toAuthUser(name)))
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
        photoUrl: String?,
        onResult: (AuthResult) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onResult(AuthResult.Error("No hay usuario autenticado."))
            return
        }

        val cleanPhotoUrl = photoUrl?.trim().orEmpty()
        val photoUri = cleanPhotoUrl.takeIf { it.isNotBlank() }?.let { Uri.parse(it) }

        fun updateAuthProfile() {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUri)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { profileTask ->
                    if (!profileTask.isSuccessful) {
                        onResult(AuthResult.Error(profileTask.exception?.message ?: "No se pudo actualizar el perfil."))
                        return@addOnCompleteListener
                    }

                    val updatedUser = user.toAuthUser(name, cleanPhotoUrl)
                    saveUserProfile(updatedUser) { saveError ->
                        if (saveError != null) {
                            onResult(AuthResult.Error(saveError))
                            return@saveUserProfile
                        }
                        onResult(AuthResult.Success(updatedUser))
                    }
                }
        }

        updateAuthProfile()
    }

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (AuthResult) -> Unit
    ) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            onResult(AuthResult.Error("No hay usuario autenticado para cambiar la contrasena."))
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (!reauthTask.isSuccessful) {
                    onResult(
                        AuthResult.Error(
                            reauthTask.exception?.message
                                ?: "La contrasena actual no es correcta."
                        )
                    )
                    return@addOnCompleteListener
                }

                user.updatePassword(newPassword)
                    .addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            onResult(AuthResult.Success(user.toAuthUser()))
                        } else {
                            onResult(
                                AuthResult.Error(
                                    passwordTask.exception?.message
                                        ?: "No se pudo actualizar la contrasena."
                                )
                            )
                        }
                    }
            }
    }

    fun sendPasswordResetEmail(email: String, onResult: (AuthResult) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthResult.Success(auth.currentUser?.toAuthUser() ?: AuthUser()))
                } else {
                    onResult(
                        AuthResult.Error(
                            task.exception?.message
                                ?: "No se pudo enviar el link de recuperacion."
                        )
                    )
                }
            }
    }

    private fun saveUserProfile(user: AuthUser, onResult: (String?) -> Unit) {
        val data = hashMapOf(
            "uid" to user.uid,
            "name" to user.name,
            "email" to user.email,
            "photoUrl" to user.photoUrl,
            "emailVerified" to user.emailVerified,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("Users")
            .document(user.uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { exception ->
                onResult(
                    exception.message
                        ?: "No se pudo guardar el perfil en Firestore."
                )
            }
    }

    private fun com.google.firebase.auth.FirebaseUser.toAuthUser(
        name: String? = null,
        photoUrlOverride: String? = null
    ): AuthUser {
        return AuthUser(
            uid = uid,
            name = name ?: displayName.orEmpty(),
            email = email.orEmpty(),
            photoUrl = photoUrlOverride ?: photoUrl?.toString().orEmpty(),
            emailVerified = isEmailVerified
        )
    }
}
