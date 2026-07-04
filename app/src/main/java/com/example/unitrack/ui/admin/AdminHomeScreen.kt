package com.example.unitrack.ui.admin

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unitrack.viewmodel.AdminStatsState
import com.example.unitrack.ui.components.AppLogo

@Composable
fun AdminHomeScreen(
    adminStatsState: AdminStatsState,
    onRefreshStats: () -> Unit,
    onRequests: () -> Unit,
    onCategories: () -> Unit,
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
        AppLogo(width = 140.dp)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Painel do Administrador",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumo geral dos pedidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    adminStatsState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    adminStatsState.errorMessage != null -> {
                        Text(adminStatsState.errorMessage)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onRefreshStats,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tentar novamente")
                        }
                    }

                    else -> {
                        AdminStatsSection(adminStatsState)

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onRefreshStats,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Atualizar estatísticas")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Administração",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onRequests,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Todos os Pedidos")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCategories,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gerir Categorias")
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
private fun AdminStatsSection(
    state: AdminStatsState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AdminStatCard(
            title = "Total",
            value = state.totalRequests.toString(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminStatCard(
                title = "Submetidos",
                value = state.submittedRequests.toString(),
                modifier = Modifier.weight(1f)
            )

            AdminStatCard(
                title = "Em análise",
                value = state.inAnalysisRequests.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminStatCard(
                title = "Concluídos",
                value = state.completedRequests.toString(),
                modifier = Modifier.weight(1f)
            )

            AdminStatCard(
                title = "Rejeitados",
                value = state.rejectedRequests.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        AdminStatCard(
            title = "Cancelados",
            value = state.cancelledRequests.toString(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AdminStatCard(
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
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}