package com.example.unitrack.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserHomeScreen(
    onCreateRequest: () -> Unit,
    onMyRequests: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Área do Utilizador")

        Button(onClick = onCreateRequest) {
            Text("Criar Pedido")
        }

        Button(onClick = onMyRequests) {
            Text("Meus Pedidos")
        }

        Button(onClick = onHistory) {
            Text("Histórico")
        }

        Button(onClick = onProfile) {
            Text("Perfil")
        }

        Button(onClick = onLogout) {
            Text("Terminar sessão")
        }
    }
}