package com.example.unitrack.ui.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.unitrack.data.model.Category
import com.example.unitrack.viewmodel.CreateRequestState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    state: CreateRequestState,
    onCreateRequest: (Long?, String, String, ByteArray?) -> Unit,
    onClearMessages: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPhotoUri = uri
        photoError = null
        onClearMessages()
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage == "Pedido criado com sucesso.") {
            selectedCategory = null
            location = ""
            description = ""
            selectedPhotoUri = null
            photoError = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Criar Pedido")

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoadingCategories) {
            CircularProgressIndicator()
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    state.categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(category.name)
                            },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                                onClearMessages()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                onClearMessages()
            },
            label = {
                Text("Localização")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                onClearMessages()
            },
            label = {
                Text("Descrição")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                photoPickerLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (selectedPhotoUri == null) {
                    "Selecionar foto opcional"
                } else {
                    "Alterar foto selecionada"
                }
            )
        }

        selectedPhotoUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = uri,
                contentDescription = "Foto selecionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        photoError?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        state.errorMessage?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        state.successMessage?.let {
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isSubmitting) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val photoBytes = try {
                        selectedPhotoUri?.let { uri ->
                            context.contentResolver
                                .openInputStream(uri)
                                ?.use { inputStream ->
                                    inputStream.readBytes()
                                }
                        }
                    } catch (e: Exception) {
                        photoError = "Erro ao carregar a imagem selecionada."
                        return@Button
                    }

                    onCreateRequest(
                        selectedCategory?.id,
                        location,
                        description,
                        photoBytes
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submeter pedido")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar")
            }
        }
    }
}