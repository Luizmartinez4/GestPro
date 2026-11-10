package com.example.gestpro.ui.talhoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.PlotInputEntity
import com.example.gestpro.data.repository.FarmRepository
import com.example.gestpro.util.PlotFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InputFormUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val quantity: String = "",
    val unit: String = PlotOptions.UNITS.first(),
    val appliedAt: Long? = null,
    val nameError: String? = null,
    val quantityError: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val notFound: Boolean = false
) {
    val isBusy: Boolean get() = isSaving || isDeleting
}

/** Formulário de lançamento (rota talhao/{plotId}/lancamento/{inputId}; 0 = novo). */
class InputFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FarmRepository
) : ViewModel() {

    private val plotId: Long = savedStateHandle.get<Long>("plotId") ?: 0L
    private val inputId: Long = savedStateHandle.get<Long>("inputId") ?: 0L
    private var sortOrder: Int = 0

    private val _uiState = MutableStateFlow(InputFormUiState(isEditing = inputId != 0L))
    val uiState: StateFlow<InputFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (inputId == 0L) {
                _uiState.update { it.copy(isLoading = false, appliedAt = PlotFormat.todayUtc()) }
            } else {
                val i = repository.getInput(inputId)
                if (i == null) {
                    _uiState.update { it.copy(isLoading = false, notFound = true) }
                } else {
                    sortOrder = i.sortOrder
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = i.name,
                            quantity = PlotFormat.decimalInput(i.quantity),
                            unit = i.unit.uppercase(),
                            appliedAt = i.appliedAt
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v.take(60), nameError = null) }
    fun onQuantityChange(v: String) = _uiState.update { it.copy(quantity = v.take(12), quantityError = null) }
    fun onUnitChange(v: String) = _uiState.update { it.copy(unit = v) }
    fun onAppliedAtChange(v: Long?) = _uiState.update { it.copy(appliedAt = v) }

    fun onSaveClick() {
        val form = _uiState.value
        if (form.isBusy || form.isLoading) return

        val quantity = PlotFormat.parseDecimal(form.quantity)
        val checked = form.copy(
            nameError = if (form.name.isBlank()) "Informe o insumo" else null,
            quantityError = if (quantity == null || quantity <= 0.0) "Informe a quantidade" else null
        )
        if (checked.nameError != null || checked.quantityError != null || quantity == null) {
            _uiState.value = checked
            return
        }

        _uiState.value = checked.copy(isSaving = true)
        viewModelScope.launch {
            runCatching {
                repository.saveInput(
                    PlotInputEntity(
                        id = inputId,
                        plotId = plotId,
                        name = form.name.trim(),
                        quantity = quantity,
                        unit = form.unit,
                        sortOrder = sortOrder,
                        appliedAt = form.appliedAt
                    )
                )
            }.onSuccess { _uiState.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { err ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = err.message ?: "Erro ao salvar") }
                }
        }
    }

    fun onSavedHandled() = _uiState.update { it.copy(saved = false) }

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }
    fun onDeleteDismiss() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun onDeleteConfirm() {
        if (!_uiState.value.isEditing) return
        _uiState.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            runCatching { repository.deleteInput(inputId) }
                .onSuccess { _uiState.update { it.copy(isDeleting = false, deleted = true) } }
                .onFailure { err ->
                    _uiState.update { it.copy(isDeleting = false, errorMessage = err.message ?: "Erro ao excluir") }
                }
        }
    }

    fun onDeletedHandled() = _uiState.update { it.copy(deleted = false) }
    fun onErrorHandled() = _uiState.update { it.copy(errorMessage = null) }
}
