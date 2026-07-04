package com.example.unitrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitrack.data.model.Category
import com.example.unitrack.data.model.ServiceRequest
import com.example.unitrack.data.repository.CategoryRepository
import com.example.unitrack.data.repository.RequestRepository
import com.example.unitrack.data.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateRequestState(
    val isLoadingCategories: Boolean = false,
    val isSubmitting: Boolean = false,
    val categories: List<Category> = emptyList(),
    val successMessage: String? = null,
    val errorMessage: String? = null
)

data class UserRequestsState(
    val isLoading: Boolean = false,
    val requests: List<ServiceRequest> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
    val errorMessage: String? = null
)

data class HistoryRequestsState(
    val isLoading: Boolean = false,
    val requests: List<ServiceRequest> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
    val errorMessage: String? = null
)

data class AdminRequestsState(
    val isLoading: Boolean = false,
    val requests: List<ServiceRequest> = emptyList(),
    val categoryNames: Map<Long, String> = emptyMap(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class AdminStatsState(
    val isLoading: Boolean = false,
    val totalRequests: Int = 0,
    val submittedRequests: Int = 0,
    val inAnalysisRequests: Int = 0,
    val completedRequests: Int = 0,
    val rejectedRequests: Int = 0,
    val cancelledRequests: Int = 0,
    val errorMessage: String? = null
)

data class UserStatsState(
    val isLoading: Boolean = false,
    val totalRequests: Int = 0,
    val submittedRequests: Int = 0,
    val inAnalysisRequests: Int = 0,
    val completedRequests: Int = 0,
    val rejectedRequests: Int = 0,
    val cancelledRequests: Int = 0,
    val errorMessage: String? = null
)

class RequestViewModel : ViewModel() {

    private val categoryRepository = CategoryRepository()
    private val requestRepository = RequestRepository()
    private val storageRepository = StorageRepository()

    private val _createRequestState = MutableStateFlow(CreateRequestState())
    val createRequestState: StateFlow<CreateRequestState> = _createRequestState.asStateFlow()

    private val _userRequestsState = MutableStateFlow(UserRequestsState())
    val userRequestsState: StateFlow<UserRequestsState> = _userRequestsState.asStateFlow()

    private val _historyRequestsState = MutableStateFlow(HistoryRequestsState())
    val historyRequestsState: StateFlow<HistoryRequestsState> = _historyRequestsState.asStateFlow()

    private val _adminRequestsState = MutableStateFlow(AdminRequestsState())
    val adminRequestsState: StateFlow<AdminRequestsState> = _adminRequestsState.asStateFlow()
    private val _adminStatsState = MutableStateFlow(AdminStatsState())
    val adminStatsState: StateFlow<AdminStatsState> = _adminStatsState.asStateFlow()
    private val _userStatsState = MutableStateFlow(UserStatsState())
    val userStatsState: StateFlow<UserStatsState> = _userStatsState.asStateFlow()


    fun loadCategories() {
        if (_createRequestState.value.categories.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _createRequestState.value = _createRequestState.value.copy(
                    isLoadingCategories = true,
                    errorMessage = null
                )

                val categories = categoryRepository.getActiveCategories()

                _createRequestState.value = _createRequestState.value.copy(
                    isLoadingCategories = false,
                    categories = categories
                )
            } catch (e: Exception) {
                _createRequestState.value = _createRequestState.value.copy(
                    isLoadingCategories = false,
                    errorMessage = "Erro ao carregar categorias."
                )
            }
        }
    }

    fun createRequest(
        userId: String,
        categoryId: Long?,
        location: String,
        description: String,
        photoBytes: ByteArray? = null
    ) {
        if (categoryId == null) {
            _createRequestState.value = _createRequestState.value.copy(
                errorMessage = "Seleciona uma categoria."
            )
            return
        }

        if (location.isBlank() || description.isBlank()) {
            _createRequestState.value = _createRequestState.value.copy(
                errorMessage = "Preenche a localização e a descrição."
            )
            return
        }

        viewModelScope.launch {
            try {
                _createRequestState.value = _createRequestState.value.copy(
                    isSubmitting = true,
                    successMessage = null,
                    errorMessage = null
                )

                val photoUrl = if (photoBytes != null) {
                    storageRepository.uploadRequestPhoto(
                        userId = userId,
                        photoBytes = photoBytes
                    )
                } else {
                    null
                }

                requestRepository.createRequest(
                    userId = userId,
                    categoryId = categoryId,
                    location = location.trim(),
                    description = description.trim(),
                    photoUrl = photoUrl
                )

                _createRequestState.value = _createRequestState.value.copy(
                    isSubmitting = false,
                    successMessage = "Pedido criado com sucesso."
                )
            } catch (e: Exception) {
                _createRequestState.value = _createRequestState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Erro ao criar pedido."
                )
            }
        }
    }

    fun loadUserRequests(userId: String) {
        viewModelScope.launch {
            try {
                _userRequestsState.value = UserRequestsState(isLoading = true)

                val allRequests = requestRepository.getRequestsByUser(userId)
                val categories = categoryRepository.getAllCategories()

                val categoryNames = categories.associate { category ->
                    category.id to category.name
                }

                val activeRequests = allRequests.filter { request ->
                    request.status == "SUBMITTED" ||
                            request.status == "IN_ANALYSIS"
                }

                _userRequestsState.value = UserRequestsState(
                    isLoading = false,
                    requests = activeRequests,
                    categoryNames = categoryNames
                )
            } catch (e: Exception) {
                _userRequestsState.value = UserRequestsState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar pedidos."
                )
            }
        }
    }

    fun clearMessages() {
        _createRequestState.value = _createRequestState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    fun cancelRequest(requestId: Long, userId: String) {
        viewModelScope.launch {
            try {
                requestRepository.cancelRequest(requestId)
                loadUserRequests(userId)
            } catch (e: Exception) {
                _userRequestsState.value = _userRequestsState.value.copy(
                    errorMessage = "Erro ao cancelar pedido."
                )
            }
        }
    }

    fun loadHistoryRequests(userId: String) {
        viewModelScope.launch {
            try {
                _historyRequestsState.value = HistoryRequestsState(isLoading = true)

                val allRequests = requestRepository.getRequestsByUser(userId)
                val categories = categoryRepository.getAllCategories()

                val categoryNames = categories.associate { category ->
                    category.id to category.name
                }

                val historyRequests = allRequests.filter { request ->
                    request.status == "COMPLETED" ||
                            request.status == "REJECTED" ||
                            request.status == "CANCELLED"
                }

                _historyRequestsState.value = HistoryRequestsState(
                    isLoading = false,
                    requests = historyRequests,
                    categoryNames = categoryNames
                )
            } catch (e: Exception) {
                _historyRequestsState.value = HistoryRequestsState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar histórico."
                )
            }
        }
    }

    fun loadAllRequestsForAdmin() {
        viewModelScope.launch {
            try {
                _adminRequestsState.value = AdminRequestsState(isLoading = true)

                val requests = requestRepository.getAllRequests()
                val categories = categoryRepository.getAllCategories()

                val categoryNames = categories.associate { category ->
                    category.id to category.name
                }

                _adminRequestsState.value = AdminRequestsState(
                    isLoading = false,
                    requests = requests,
                    categoryNames = categoryNames
                )
            } catch (e: Exception) {
                _adminRequestsState.value = AdminRequestsState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar pedidos."
                )
            }
        }
    }

    fun updateRequestStatusAsAdmin(
        requestId: Long,
        newStatus: String
    ) {
        viewModelScope.launch {
            try {
                requestRepository.updateRequestStatus(
                    requestId = requestId,
                    newStatus = newStatus
                )

                _adminRequestsState.value = _adminRequestsState.value.copy(
                    successMessage = "Estado atualizado com sucesso.",
                    errorMessage = null
                )

                loadAllRequestsForAdmin()
            } catch (e: Exception) {
                _adminRequestsState.value = _adminRequestsState.value.copy(
                    errorMessage = "Erro ao atualizar estado."
                )
            }
        }
    }

    fun deleteRequestAsAdmin(requestId: Long) {
        viewModelScope.launch {
            try {
                requestRepository.deleteRequest(requestId)

                _adminRequestsState.value = _adminRequestsState.value.copy(
                    successMessage = "Pedido eliminado com sucesso.",
                    errorMessage = null
                )

                loadAllRequestsForAdmin()
            } catch (e: Exception) {
                _adminRequestsState.value = _adminRequestsState.value.copy(
                    errorMessage = "Erro ao eliminar pedido."
                )
            }
        }
    }

    fun clearAdminMessages() {
        _adminRequestsState.value = _adminRequestsState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun loadAdminStats() {
        viewModelScope.launch {
            try {
                _adminStatsState.value = AdminStatsState(isLoading = true)

                val requests = requestRepository.getAllRequests()

                _adminStatsState.value = AdminStatsState(
                    isLoading = false,
                    totalRequests = requests.size,
                    submittedRequests = requests.count { it.status == "SUBMITTED" },
                    inAnalysisRequests = requests.count { it.status == "IN_ANALYSIS" },
                    completedRequests = requests.count { it.status == "COMPLETED" },
                    rejectedRequests = requests.count { it.status == "REJECTED" },
                    cancelledRequests = requests.count { it.status == "CANCELLED" }
                )
            } catch (e: Exception) {
                _adminStatsState.value = AdminStatsState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar estatísticas."
                )
            }
        }
    }

    fun loadUserStats(userId: String) {
        viewModelScope.launch {
            try {
                _userStatsState.value = UserStatsState(isLoading = true)

                val requests = requestRepository.getRequestsByUser(userId)

                _userStatsState.value = UserStatsState(
                    isLoading = false,
                    totalRequests = requests.size,
                    submittedRequests = requests.count { it.status == "SUBMITTED" },
                    inAnalysisRequests = requests.count { it.status == "IN_ANALYSIS" },
                    completedRequests = requests.count { it.status == "COMPLETED" },
                    rejectedRequests = requests.count { it.status == "REJECTED" },
                    cancelledRequests = requests.count { it.status == "CANCELLED" }
                )
            } catch (e: Exception) {
                _userStatsState.value = UserStatsState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar estatísticas."
                )
            }
        }
    }
}