package com.example.unitrack.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.unitrack.viewmodel.AuthState

@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegisterClick: (String, String, String, String) -> Unit,
    onBackToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profileType by remember { mutableStateOf("USER") }
    var adminCode by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val validAdminCode = "admin2026"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar conta")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                localError = null
                onClearError()
            },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                localError = null
                onClearError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                localError = null
                onClearError()
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tipo de perfil")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = profileType == "USER",
                onClick = {
                    profileType = "USER"
                    adminCode = ""
                    localError = null
                    onClearError()
                },
                label = { Text("Utilizador") }
            )

            FilterChip(
                selected = profileType == "ADMIN",
                onClick = {
                    profileType = "ADMIN"
                    localError = null
                    onClearError()
                },
                label = { Text("Administrador") }
            )
        }

        if (profileType == "ADMIN") {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = adminCode,
                onValueChange = {
                    adminCode = it
                    localError = null
                    onClearError()
                },
                label = { Text("Código de administrador") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        localError?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        authState.errorMessage?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (authState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    localError = null

                    if (name.isBlank()) {
                        localError = "O nome é obrigatório."
                        return@Button
                    }

                    if (email.isBlank()) {
                        localError = "O email é obrigatório."
                        return@Button
                    }

                    if (password.length < 6) {
                        localError = "A password deve ter pelo menos 6 caracteres."
                        return@Button
                    }

                    if (profileType == "ADMIN" && adminCode.trim() != validAdminCode) {
                        localError = "Código de administrador inválido."
                        return@Button
                    }

                    onRegisterClick(
                        name.trim(),
                        email.trim(),
                        password,
                        profileType
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBackToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar ao login")
            }
        }
    }
}