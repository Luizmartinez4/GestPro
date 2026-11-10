package com.example.gestpro.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Formatação no padrão brasileiro para talhões e lançamentos. */
object PlotFormat {
    private val ptBr = Locale("pt", "BR")

    /** 3.5 -> "3,5" | 15000.0 -> "15.000" (até 2 casas, para quantidades como 3,75 L) */
    fun decimal(value: Double): String = Formatters.number(value, maxDecimals = 2)

    /** 3550 -> "3.550" */
    fun integer(value: Int): String = NumberFormat.getIntegerInstance(ptBr).format(value)

    /** Valor para campo de texto, sem separador de milhar: 3.5 -> "3,5" */
    fun decimalInput(value: Double): String =
        NumberFormat.getNumberInstance(ptBr).apply {
            maximumFractionDigits = 2
            isGroupingUsed = false
        }.format(value)

    /**
     * Lê o que o usuário digitou. Aceita "3,5", "3.5" e "15.000,5".
     * Devolve null se não for um número.
     */
    fun parseDecimal(raw: String): Double? {
        val text = raw.trim().replace(" ", "")
        if (text.isEmpty()) return null
        val normalized = if (text.contains(',')) text.replace(".", "").replace(',', '.') else text
        return normalized.toDoubleOrNull()
    }

    fun date(ms: Long?): String = EmployeeFormatters.date(ms)

    /** Hoje à meia-noite UTC, no mesmo formato que o DatePicker devolve. */
    fun todayUtc(): Long {
        val now = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
}
