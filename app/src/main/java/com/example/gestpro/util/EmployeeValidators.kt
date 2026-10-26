package com.example.gestpro.util

object EmployeeLimits {
    const val NAME_MAX = 80
    const val NOTES_MAX = 300
    const val PHONE_MAX = 11
}

object EmployeeValidators {
    fun nameError(name: String): String? = when {
        name.isBlank() -> "Informe o nome completo"
        name.trim().length < 3 -> "Nome muito curto"
        else -> null
    }

    fun roleError(role: String): String? =
        if (role.isBlank()) "Selecione o cargo ou função" else null

    /** Telefone é opcional, mas se preenchido precisa ter 10 ou 11 dígitos. */
    fun phoneError(digits: String): String? =
        if (digits.isNotEmpty() && digits.length !in 10..11) "Telefone inválido" else null

    fun salaryError(text: String): String? =
        if (parseSalary(text) == null) "Valor inválido" else null

    /** Aceita "120", "120,50" e "1.500,50". Vazio vira 0.0. */
    fun parseSalary(text: String): Double? {
        val s = text.trim()
        if (s.isEmpty()) return 0.0
        val normalized = if (s.contains(',')) s.replace(".", "").replace(',', '.') else s
        return normalized.toDoubleOrNull()?.takeIf { it >= 0 }
    }
}
