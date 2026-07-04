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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unitrack.data.model.Category
import com.example.unitrack.viewmodel.CategoryManagementState

@Composable
fun ManageCategoriesScreen(
    state: CategoryManagementState,
    onRefresh: () -> Unit,
    onCreateCategory: (String, String) -> Unit,
    onUpdateCategory: (Long, String, String) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onClearMessages: () -> Unit,
    onBack: () -> Unit
) {
    var editingCategoryId by remember { mutableStateOf<Long?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            editingCategoryId = null
            name = ""
            description = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Gerir Categorias")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onClearMessages()
            },
            label = { Text("Nome da categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                onClearMessages()
            },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.errorMessage != null) {
            Text(state.errorMessage)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.successMessage != null) {
            Text(state.successMessage)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isSaving) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val categoryId = editingCategoryId

                    if (categoryId == null) {
                        onCreateCategory(name, description)
                    } else {
                        onUpdateCategory(categoryId, name, description)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (editingCategoryId == null) {
                        "Criar categoria"
                    } else {
                        "Guardar alterações"
                    }
                )
            }

            if (editingCategoryId != null) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        editingCategoryId = null
                        name = ""
                        description = ""
                        onClearMessages()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar edição")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            state.categories.isEmpty() -> {
                Text("Não existem categorias ativas.")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.categories) { category ->
                        CategoryCard(
                            category = category,
                            onEdit = {
                                editingCategoryId = category.id
                                name = category.name
                                description = category.description ?: ""
                                onClearMessages()
                            },
                            onRemove = {
                                onRemoveCategory(category.id)
                            }
                        )
                    }
                }
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

@Composable
private fun CategoryCard(
    category: Category,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(category.name)

            if (!category.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(category.description)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    onClick = {
                        showRemoveDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Remover")
                }
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = {
                showRemoveDialog = false
            },
            title = {
                Text("Remover categoria")
            },
            text = {
                Text("Tens a certeza que queres remover esta categoria? Ela deixará de aparecer nos novos pedidos.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                        onRemove()
                    }
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}