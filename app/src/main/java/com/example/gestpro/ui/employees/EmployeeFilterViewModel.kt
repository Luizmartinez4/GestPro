package com.example.gestpro.ui.employees

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmployeeFilterUiState(
    val filter: EmployeeFilter = EmployeeFilter(),
    val dateError: String? = null
)

class EmployeeFilterViewModel(private val store: EmployeeFilterStore) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeFilterUiState(store.filter.value))
    val uiState: StateFlow<EmployeeFilterUiState> = _uiState.asStateFlow()

    // "Todos" chega como null
    fun onRoleChange(v: String?) = _uiState.update { it.copy(filter = it.filter.copy(role = v)) }
    fun onStatusChange(v: String?) = _uiState.update { it.copy(filter = it.filter.copy(status = v)) }
    fun onContractTypeChange(v: String?) = _uiState.update { it.copy(filter = it.filter.copy(contractType = v)) }
    fun onAdmissionFromChange(v: Long?) = _uiState.update { it.copy(filter = it.filter.copy(admissionFrom = v), dateError = null) }
    fun onAdmissionToChange(v: Long?) = _uiState.update { it.copy(filter = it.filter.copy(admissionTo = v), dateError = null) }

    fun onClearClick() = _uiState.update { EmployeeFilterUiState() }

    /** Devolve true se o filtro foi aplicado (a tela pode fechar). */
    fun onApplyClick(): Boolean {
        val f = _uiState.value.filter
        if (f.admissionFrom != null && f.admissionTo != null && f.admissionFrom > f.admissionTo) {
            _uiState.update { it.copy(dateError = "A data inicial deve ser anterior à data final") }
            return false
        }
        store.filter.value = f
        return true
    }
}
