package com.example.unitrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitrack.data.model.Category
import com.example.unitrack.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryManagementState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categoryState = MutableStateFlow(CategoryManagementState())
    val categoryState: StateFlow<CategoryManagementState> = _categoryState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoryState.value = _categoryState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val categories = repository.getActiveCategories()

                _categoryState.value = CategoryManagementState(
                    isLoading = false,
                    categories = categories
                )
            } catch (e: Exception) {
                _categoryState.value = CategoryManagementState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar categorias."
                )
            }
        }
    }

    fun createCategory(
        name: String,
        description: String
    ) {
        if (name.isBlank()) {
            _categoryState.value = _categoryState.value.copy(
                errorMessage = "O nome da categoria é obrigatório."
            )
            return
        }

        viewModelScope.launch {
            try {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.createCategory(
                    name = name,
                    description = description
                )

                val categories = repository.getActiveCategories()

                _categoryState.value = CategoryManagementState(
                    categories = categories,
                    successMessage = "Categoria criada com sucesso."
                )
            } catch (e: Exception) {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao criar categoria."
                )
            }
        }
    }

    fun updateCategory(
        categoryId: Long,
        name: String,
        description: String
    ) {
        if (name.isBlank()) {
            _categoryState.value = _categoryState.value.copy(
                errorMessage = "O nome da categoria é obrigatório."
            )
            return
        }

        viewModelScope.launch {
            try {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.updateCategory(
                    categoryId = categoryId,
                    name = name,
                    description = description
                )

                val categories = repository.getActiveCategories()

                _categoryState.value = CategoryManagementState(
                    categories = categories,
                    successMessage = "Categoria atualizada com sucesso."
                )
            } catch (e: Exception) {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao atualizar categoria."
                )
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.removeCategory(categoryId)

                val categories = repository.getActiveCategories()

                _categoryState.value = CategoryManagementState(
                    categories = categories,
                    successMessage = "Categoria removida com sucesso."
                )
            } catch (e: Exception) {
                _categoryState.value = _categoryState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao remover categoria."
                )
            }
        }
    }

    fun clearMessages() {
        _categoryState.value = _categoryState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}