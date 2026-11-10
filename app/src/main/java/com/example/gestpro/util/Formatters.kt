package com.example.gestpro.util

import java.text.NumberFormat
import java.util.Locale

/** Formatação de números do app, no padrão brasileiro. */
object Formatters {
    private val ptBr = Locale("pt", "BR")

    /**
     * 18.0 -> "18" | 3.5 -> "3,5" | 1200.0 -> "1.200"
     * [maxDecimals] limita as casas decimais (padrão: 1).
     */
    fun number(value: Double, maxDecimals: Int = 1): String =
        NumberFormat.getNumberInstance(ptBr).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = maxDecimals
        }.format(value)
}
