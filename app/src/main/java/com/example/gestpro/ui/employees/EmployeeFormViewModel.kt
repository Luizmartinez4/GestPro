package com.example.gestpro.ui.employees

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.EmployeeEntity
import com.example.gestpro.data.repository.EmployeeRepository
import com.example.gestpro.util.EmployeeLimits
import com.example.gestpro.util.EmployeeValidators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployeeFormUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val role: String = "",
    val phone: String = "",              // só dígitos
    val birthDate: Long? = null,
    val contractType: String = "CLT",
    val admissionDate: Long? = null,
    val salary: String = "",
    val status: String = EmployeeOptions.STATUS_ACTIVE,
    val notes: String = "",
    val photoPath: String? = null,
    val nameError: String? = null,
    val roleError: String? = null,
    val phoneError: String? = null,
    val salaryError: String? = null,
    val isLoading: Boolean = false,      // carregando dados para edição
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false
)

class EmployeeFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EmployeeRepository
) : ViewModel() {

    private val employeeId: Long = savedStateHandle.get<Long>("employeeId") ?: 0L

    private val _uiState = MutableStateFlow(
        EmployeeFormUiState(isEditing = employeeId != 0L, isLoading = employeeId != 0L)
    )
    val uiState: StateFlow<EmployeeFormUiState> = _uiState.asStateFlow()

    init {
        if (employeeId != 0L) {
            viewModelScope.launch {
                repository.getEmployee(employeeId)
                    .onSuccess { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false, name = e.name, role = e.role, phone = e.phone,
                                birthDate = e.birthDate, contractType = e.contractType,
                                admissionDate = e.admissionDate,
                                salary = com.example.gestpro.util.EmployeeFormatters.salaryInput(e.salary),
                                status = e.status, notes = e.notes, photoPath = e.photoPath
                            )
                        }
                    }
                    .onFailure { err -> _uiState.update { it.copy(isLoading = false, errorMessage = err.message) } }
            }
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v.take(EmployeeLimits.NAME_MAX), nameError = null) }
    fun onRoleChange(v: String) = _uiState.update { it.copy(role = v, roleError = null) }
    fun onPhoneChange(v: String) =
        _uiState.update { it.copy(phone = v.filter(Char::isDigit).take(EmployeeLimits.PHONE_MAX), phoneError = null) }
    fun onBirthDateChange(v: Long?) = _uiState.update { it.copy(birthDate = v) }
    fun onContractTypeChange(v: String) = _uiState.update { it.copy(contractType = v) }
    fun onAdmissionDateChange(v: Long?) = _uiState.update { it.copy(admissionDate = v) }
    fun onSalaryChange(v: String) =
        _uiState.update { it.copy(salary = v.filter { c -> c.isDigit() || c == ',' || c == '.' }.take(12), salaryError = null) }
    fun onStatusChange(v: String) = _uiState.update { it.copy(status = v) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v.take(EmployeeLimits.NOTES_MAX)) }
    fun onPhotoChange(path: String?) = _uiState.update { it.copy(photoPath = path) }

    fun onSaveClick() {
        val s = _uiState.value
        if (s.isSaving) return
        val nameErr = EmployeeValidators.nameError(s.name)
        val roleErr = EmployeeValidators.roleError(s.role)
        val phoneErr = EmployeeValidators.phoneError(s.phone)
        val salaryErr = EmployeeValidators.salaryError(s.salary)
        if (listOf(nameErr, roleErr, phoneErr, salaryErr).any { it != null }) {
            _uiState.update {
                it.copy(nameError = nameErr, roleError = roleErr, phoneError = phoneErr, salaryError = salaryErr)
            }
            return
        }
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            val entity = EmployeeEntity(
                id = employeeId,
                name = s.name.trim(),
                role = s.role,
                phone = s.phone,
                birthDate = s.birthDate,
                contractType = s.contractType,
                admissionDate = s.admissionDate,
                salary = EmployeeValidators.parseSalary(s.salary) ?: 0.0,
                status = s.status,
                notes = s.notes.trim(),
                photoPath = s.photoPath
            )
            repository.save(entity)
                .onSuccess { _uiState.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { err -> _uiState.update { it.copy(isSaving = false, errorMessage = err.message) } }
        }
    }

    fun onSavedHandled() = _uiState.update { it.copy(saved = false) }
    fun onErrorHandled() = _uiState.update { it.copy(errorMessage = null) }
}
