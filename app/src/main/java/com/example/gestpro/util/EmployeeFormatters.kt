package com.example.gestpro.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object EmployeeFormatters {
    private val ptBr = Locale("pt", "BR")
    private val utc = TimeZone.getTimeZone("UTC")

    fun date(ms: Long?): String =
        ms?.let { SimpleDateFormat("dd/MM/yyyy", ptBr).apply { timeZone = utc }.format(Date(it)) } ?: ""

    fun age(birth: Long?): Int? {
        if (birth == null) return null
        val now = Calendar.getInstance(utc)
        val b = Calendar.getInstance(utc).apply { timeInMillis = birth }
        var years = now.get(Calendar.YEAR) - b.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < b.get(Calendar.DAY_OF_YEAR)) years--
        return years.coerceAtLeast(0)
    }

    fun currency(value: Double): String = NumberFormat.getCurrencyInstance(ptBr).format(value)

    /** Valor para o campo de texto: 120.0 -> "120,00". Zero vira vazio. */
    fun salaryInput(value: Double): String =
        if (value == 0.0) "" else String.format(ptBr, "%.2f", value)

    /** Recebe dígitos (ou texto com máscara) e devolve (35) 99912-3456. */
    fun phone(raw: String): String {
        val s = raw.filter { it.isDigit() }.take(11)
        return when {
            s.isEmpty() -> ""
            s.length <= 2 -> "($s"
            s.length <= 6 -> "(${s.take(2)}) ${s.drop(2)}"
            s.length <= 10 -> "(${s.take(2)}) ${s.substring(2, 6)}-${s.drop(6)}"
            else -> "(${s.take(2)}) ${s.substring(2, 7)}-${s.drop(7)}"
        }
    }
}
