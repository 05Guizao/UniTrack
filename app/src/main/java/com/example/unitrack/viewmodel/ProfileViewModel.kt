package com.example.unitrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitrack.data.model.UserProfile
import com.example.unitrack.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val profile = repository.getProfile(userId)

                _profileState.value = ProfileState(
                    isLoading = false,
                    profile = profile
                )
            } catch (e: Exception) {
                _profileState.value = ProfileState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar perfil."
                )
            }
        }
    }

    fun updateName(
        userId: String,
        name: String
    ) {
        if (name.isBlank()) {
            _profileState.value = _profileState.value.copy(
                errorMessage = "O nome é obrigatório."
            )
            return
        }

        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.updateName(
                    userId = userId,
                    name = name
                )

                val updatedProfile = repository.getProfile(userId)

                _profileState.value = ProfileState(
                    isSaving = false,
                    profile = updatedProfile,
                    successMessage = "Perfil atualizado com sucesso."
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao atualizar perfil."
                )
            }
        }
    }

    fun updatePassword(
        newPassword: String,
        confirmPassword: String
    ) {
        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            _profileState.value = _profileState.value.copy(
                errorMessage = "Preenche a nova password e a confirmação."
            )
            return
        }

        if (newPassword.length < 6) {
            _profileState.value = _profileState.value.copy(
                errorMessage = "A password deve ter pelo menos 6 caracteres."
            )
            return
        }

        if (newPassword != confirmPassword) {
            _profileState.value = _profileState.value.copy(
                errorMessage = "As passwords não coincidem."
            )
            return
        }

        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.updatePassword(newPassword)

                _profileState.value = _profileState.value.copy(
                    isSaving = false,
                    successMessage = "Password alterada com sucesso."
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao alterar password."
                )
            }
        }
    }

    fun clearMessages() {
        _profileState.value = _profileState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}