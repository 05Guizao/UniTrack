package com.example.unitrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitrack.data.model.UserProfile
import com.example.unitrack.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val user: UserProfile? = null,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Preenche o email e a password."
            )
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)

                val user = repository.login(
                    email = email.trim(),
                    password = password
                )

                _authState.value = AuthState(user = user)
            } catch (e: Exception) {
                _authState.value = AuthState(
                    errorMessage = getFriendlyErrorMessage(e)
                )
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        profileType: String
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Preenche todos os campos."
            )
            return
        }

        if (password.length < 6) {
            _authState.value = _authState.value.copy(
                errorMessage = "A password deve ter pelo menos 6 caracteres."
            )
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)

                val user = repository.register(
                    name = name.trim(),
                    email = email.trim(),
                    password = password,
                    profileType = profileType
                )

                _authState.value = AuthState(user = user)
            } catch (e: Exception) {
                _authState.value = AuthState(
                    errorMessage = getFriendlyErrorMessage(e)
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState()
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }

    private fun getFriendlyErrorMessage(e: Exception): String {
        val message = e.message ?: return "Ocorreu um erro inesperado."

        return when {
            message.contains("email_address_invalid", ignoreCase = true) ->
                "O email introduzido não é válido."

            message.contains("Invalid login credentials", ignoreCase = true) ->
                "Email ou password incorretos."

            message.contains("User already registered", ignoreCase = true) ->
                "Este email já está registado."

            message.contains("Password", ignoreCase = true) ->
                "A password não cumpre os requisitos mínimos."

            else ->
                "Não foi possível concluir a operação. Verifica os dados e tenta novamente."
        }
    }
}