package com.example.unitrack.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unitrack.data.model.ServiceRequest
import com.example.unitrack.viewmodel.HistoryRequestsState
import com.example.unitrack.ui.components.StatusChip
import com.example.unitrack.ui.components.RequestPhoto

@Composable
fun RequestHistoryScreen(
    state: HistoryRequestsState,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Histórico de Pedidos")

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Text(state.errorMessage)

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onRefresh) {
                    Text("Tentar novamente")
                }
            }

            state.requests.isEmpty() -> {
                Text("Ainda não tens pedidos finalizados.")

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(onClick = onBack) {
                    Text("Voltar")
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.requests) { request ->
                        HistoryRequestCard(
                            request = request,
                            categoryName = state.categoryNames[request.categoryId]
                                ?: "Categoria desconhecida"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Voltar")
                    }

                    Button(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Atualizar")
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRequestCard(
    request: ServiceRequest,
    categoryName: String
) {
    val createdDate = formatDateOnly(request.createdAt)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Pedido #${request.id}")

            Spacer(modifier = Modifier.height(6.dp))

            StatusChip(status = request.status)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Categoria: $categoryName")

            Spacer(modifier = Modifier.height(4.dp))

            Text("Localização: ${request.location}")

            Spacer(modifier = Modifier.height(4.dp))

            Text("Descrição: ${request.description}")

            if (!request.photoUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                RequestPhoto(photoUrl = request.photoUrl)
            }

            if (createdDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Criado em: $createdDate")
            }
        }
    }
}

private fun formatDateOnly(dateTime: String?): String? {
    return dateTime?.take(10)
}