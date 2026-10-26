package com.example.gestpro.ui.employees

import com.example.gestpro.data.local.EmployeeEntity
import kotlinx.coroutines.flow.MutableStateFlow

data class EmployeeFilter(
    val role: String? = null,
    val status: String? = null,
    val contractType: String? = null,
    val admissionFrom: Long? = null,
    val admissionTo: Long? = null
) {
    val activeCount: Int
        get() = listOf(role, status, contractType).count { it != null } +
            (if (admissionFrom != null || admissionTo != null) 1 else 0)

    fun matches(e: EmployeeEntity): Boolean =
        (role == null || e.role == role) &&
            (status == null || e.status == status) &&
            (contractType == null || e.contractType == contractType) &&
            (admissionFrom == null || (e.admissionDate != null && e.admissionDate >= admissionFrom)) &&
            (admissionTo == null || (e.admissionDate != null && e.admissionDate <= admissionTo))
}

/** Filtro ativo na sessão; compartilhado entre a lista e a tela de filtro (singleton no Application). */
class EmployeeFilterStore {
    val filter = MutableStateFlow(EmployeeFilter())
}
