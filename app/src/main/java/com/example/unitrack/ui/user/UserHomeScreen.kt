package com.example.unitrack.ui.user

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unitrack.viewmodel.UserStatsState

@Composable
fun UserHomeScreen(
    userStatsState: UserStatsState,
    onRefreshStats: () -> Unit,
    onCreateRequest: () -> Unit,
    onMyRequests: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UniTrack")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Painel do Utilizador")

        Spacer(modifier = Modifier.height(24.dp))

        when {
            userStatsState.isLoading -> {
                CircularProgressIndicator()
            }

            userStatsState.errorMessage != null -> {
                Text(userStatsState.errorMessage)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRefreshStats,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tentar novamente")
                }
            }

            else -> {
                UserStatsSection(userStatsState)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onRefreshStats,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Atualizar estatísticas")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateRequest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Pedido")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onMyRequests,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Meus Pedidos")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Histórico")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Perfil")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Terminar sessão")
        }
    }
}

@Composable
private fun UserStatsSection(
    state: UserStatsState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        UserStatCard(
            title = "Total dos meus pedidos",
            value = state.totalRequests.toString(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UserStatCard(
                title = "Submetidos",
                value = state.submittedRequests.toString(),
                modifier = Modifier.weight(1f)
            )

            UserStatCard(
                title = "Em análise",
                value = state.inAnalysisRequests.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UserStatCard(
                title = "Concluídos",
                value = state.completedRequests.toString(),
                modifier = Modifier.weight(1f)
            )

            UserStatCard(
                title = "Rejeitados",
                value = state.rejectedRequests.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        UserStatCard(
            title = "Cancelados",
            value = state.cancelledRequests.toString(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun UserStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title)

            Spacer(modifier = Modifier.height(8.dp))

            Text(value)
        }
    }
}