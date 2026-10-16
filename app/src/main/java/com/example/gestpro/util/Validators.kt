package com.example.gestpro.util

import android.util.Patterns

object Limits {
    const val EMAIL_MAX = 254
    const val PASSWORD_MAX = 60
}

data class PasswordRule(val label: String, val isMet: Boolean)

object Validators {

    /** Retorna a mensagem de erro, ou null se o email for válido. */
    fun emailError(email: String): String? = when {
        email.isBlank() -> "Informe o email"
        email.length > Limits.EMAIL_MAX -> "O email deve ter no máximo ${Limits.EMAIL_MAX} caracteres"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Email inválido"
        else -> null
    }

    fun passwordRules(password: String): List<PasswordRule> = listOf(
        PasswordRule("Mínimo de 8 caracteres", password.length >= 8),
        PasswordRule("Pelo menos uma letra maiúscula", password.any { it.isUpperCase() }),
        PasswordRule("Pelo menos uma letra minúscula", password.any { it.isLowerCase() }),
        PasswordRule("Pelo menos um número", password.any { it.isDigit() }),
        PasswordRule(
            "Pelo menos um caractere especial",
            password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        )
    )

    fun isPasswordValid(password: String): Boolean =
        password.length <= Limits.PASSWORD_MAX && passwordRules(password).all { it.isMet }

    fun confirmPasswordError(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Confirme a senha"
        password != confirm -> "As senhas não coincidem"
        else -> null
    }
}