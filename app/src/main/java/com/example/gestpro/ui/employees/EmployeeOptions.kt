package com.example.gestpro.ui.employees

object EmployeeOptions {
    const val ALL = "Todos"
    const val STATUS_ACTIVE = "Ativo"
    const val STATUS_INACTIVE = "Inativo"

    val roles = listOf("Caseiro", "Turmeiro", "Cooperativa", "Motorista", "Trabalhador Rural", "Tratorista", "Gerente")
    val contractTypes = listOf("CLT", "Diarista", "Temporário", "Autônomo")
    val statuses = listOf(STATUS_ACTIVE, STATUS_INACTIVE)
}
