package com.example.gestpro.ui.employees

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.EmployeeEntity
import com.example.gestpro.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployeeDetailUiState(
    val employee: EmployeeEntity? = null,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val deleted: Boolean = false
)

class EmployeeDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EmployeeRepository
) : ViewModel() {

    private val employeeId: Long = savedStateHandle.get<Long>("employeeId") ?: 0L

    private val _uiState = MutableStateFlow(EmployeeDetailUiState())
    val uiState: StateFlow<EmployeeDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeEmployee(employeeId).collect { e ->
                _uiState.update { it.copy(employee = e, isLoading = false) }
            }
        }
    }

    val id: Long get() = employeeId

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }
    fun onDeleteDismiss() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun onDeleteConfirm() {
        _uiState.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            repository.delete(employeeId)
                .onSuccess { _uiState.update { it.copy(isDeleting = false, deleted = true) } }
                .onFailure { err -> _uiState.update { it.copy(isDeleting = false, errorMessage = err.message) } }
        }
    }

    fun onDeletedHandled() = _uiState.update { it.copy(deleted = false) }
    fun onErrorHandled() = _uiState.update { it.copy(errorMessage = null) }
}
