package com.example.proyeto2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.proyeto2.navigation.NavGraph
import com.example.proyeto2.ui.screens.LoginScreen
import com.example.proyeto2.ui.screens.RegisterScreen
import com.example.proyeto2.ui.theme.Proyeto2Theme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyeto2Theme {
                Surface(color= MaterialTheme.colorScheme.background){
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

/*
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "firebase_auth_prefs"
    private val KEY_PENDING_EMAIL = "pending_email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Manejar intent cuando se abre desde el enlace
        handleSignInIntent(intent)

        setContent {
            LoginScreen()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSignInIntent(intent)
    }

    private fun handleSignInIntent(intent: Intent?) {
        intent?.data?.let { data ->
            val emailLink = data.toString()
            val savedEmail = prefs.getString(KEY_PENDING_EMAIL, null)

            if (savedEmail != null && auth.isSignInWithEmailLink(emailLink)) {
                signInWithEmailLink(savedEmail, emailLink)
            }
        }
    }

    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        sendSignInLinkToEmail(email) { success ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "Revisa tu correo (incluida Spam)", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Enviar enlace de acceso")
                    }
                }
            }
        }
    }

    private fun sendSignInLinkToEmail(email: String, onResult: (Boolean) -> Unit) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://proyecto2-e24ec.firebaseapp.com/signin")   // ← Cambia por tu dominio real
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                packageName,
                true,
                null
            )
            .build()

        prefs.edit().putString(KEY_PENDING_EMAIL, email).apply()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Enlace enviado a: $email")
                    onResult(true)
                } else {
                    Log.e("FirebaseAuth", "Error enviando enlace", task.exception)
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    prefs.edit().remove(KEY_PENDING_EMAIL).apply()
                    onResult(false)
                }
            }
    }

    private fun signInWithEmailLink(email: String, emailLink: String) {
        auth.signInWithEmailLink(email, emailLink)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Inicio de sesión exitoso!")
                    prefs.edit().remove(KEY_PENDING_EMAIL).apply()

                    val user = task.result?.user
                    Toast.makeText(this, "¡Bienvenido ${user?.email}!", Toast.LENGTH_LONG).show()

                    // TODO: Navegar a la pantalla principal
                    // startActivity(Intent(this, HomeActivity::class.java))
                    // finish()

                } else {
                    Log.e("FirebaseAuth", "Error al iniciar sesión", task.exception)
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, "El enlace expiró o ya fue usado.", Toast.LENGTH_LONG).show()
                        }
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }
}
 */