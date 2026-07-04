package com.example.unitrack.ui.admin

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unitrack.data.model.RequestStatus
import com.example.unitrack.data.model.ServiceRequest
import com.example.unitrack.viewmodel.AdminRequestsState
import com.example.unitrack.ui.components.StatusChip
import com.example.unitrack.ui.components.RequestPhoto
import com.example.unitrack.ui.components.requestMatchesSearch

@Composable
fun AdminRequestsScreen(
    state: AdminRequestsState,
    onRefresh: () -> Unit,
    onUpdateStatus: (Long, String) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onClearMessages: () -> Unit,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredByStatus = if (selectedFilter == "ALL") {
        state.requests
    } else {
        state.requests.filter { it.status == selectedFilter }
    }

    val filteredRequests = filteredByStatus.filter { request ->
        val categoryName = state.categoryNames[request.categoryId] ?: "Categoria desconhecida"

        requestMatchesSearch(
            request = request,
            categoryName = categoryName,
            query = searchQuery
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Todos os Pedidos")

        Spacer(modifier = Modifier.height(12.dp))

        StatusFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Pesquisar pedidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        state.errorMessage?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        state.successMessage?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

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

            filteredRequests.isEmpty() -> {
                Text("Não existem pedidos para o filtro ou pesquisa selecionados.")

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

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredRequests) { request ->
                        AdminRequestCard(
                            request = request,
                            categoryName = state.categoryNames[request.categoryId]
                                ?: "Categoria desconhecida",
                            onUpdateStatus = {
                                onClearMessages()
                                onUpdateStatus(request.id, it)
                            },
                            onDeleteRequest = {
                                onClearMessages()
                                onDeleteRequest(request.id)
                            }
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
private fun StatusFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Column {
        Text("Filtrar por estado")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { onFilterSelected("ALL") },
                label = { Text("Todos") }
            )

            FilterChip(
                selected = selectedFilter == "SUBMITTED",
                onClick = { onFilterSelected("SUBMITTED") },
                label = { Text("Submetido") }
            )

            FilterChip(
                selected = selectedFilter == "IN_ANALYSIS",
                onClick = { onFilterSelected("IN_ANALYSIS") },
                label = { Text("Em análise") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "COMPLETED",
                onClick = { onFilterSelected("COMPLETED") },
                label = { Text("Concluído") }
            )

            FilterChip(
                selected = selectedFilter == "REJECTED",
                onClick = { onFilterSelected("REJECTED") },
                label = { Text("Rejeitado") }
            )

            FilterChip(
                selected = selectedFilter == "CANCELLED",
                onClick = { onFilterSelected("CANCELLED") },
                label = { Text("Cancelado") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminRequestCard(
    request: ServiceRequest,
    categoryName: String,
    onUpdateStatus: (String) -> Unit,
    onDeleteRequest: () -> Unit
) {
    val createdDate = formatDateOnly(request.createdAt)

    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(request.status) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = RequestStatus.getLabelFromDbValue(selectedStatus),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Alterar estado") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    RequestStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                selectedStatus = status.dbValue
                                expanded = false
                                onUpdateStatus(status.dbValue)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar pedido")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Eliminar pedido")
            },
            text = {
                Text("Tens a certeza que queres eliminar este pedido? Esta ação não pode ser anulada.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteRequest()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatDateOnly(dateTime: String?): String? {
    return dateTime?.take(10)
}