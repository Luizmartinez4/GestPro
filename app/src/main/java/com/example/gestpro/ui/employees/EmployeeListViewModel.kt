package com.example.gestpro.ui.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.EmployeeEntity
import com.example.gestpro.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EmployeeListUiState(
    val query: String = "",
    val employees: List<EmployeeEntity> = emptyList(),
    val totalCount: Int = 0,
    val activeFilters: Int = 0,
    val isLoading: Boolean = true
)

class EmployeeListViewModel(
    repository: EmployeeRepository,
    filterStore: EmployeeFilterStore
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<EmployeeListUiState> = combine(
        repository.observeEmployees(), query, filterStore.filter
    ) { all, q, filter ->
        val term = q.trim()
        val shown = all.filter { e ->
            filter.matches(e) &&
                (term.isEmpty() || e.name.contains(term, ignoreCase = true) || e.role.contains(term, ignoreCase = true))
        }
        EmployeeListUiState(q, shown, all.size, filter.activeCount, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmployeeListUiState())

    fun onQueryChange(value: String) {
        query.value = value.take(80)
    }
}
