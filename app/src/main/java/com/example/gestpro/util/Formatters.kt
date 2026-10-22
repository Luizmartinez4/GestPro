package com.example.gestpro.util

import java.util.Locale

object Formatters {
    /** 18.0 -> "18" | 3.5 -> "3.5" */
    fun number(value: Double): String {
        val rounded = Math.round(value * 10) / 10.0
        return if (rounded % 1.0 == 0.0) rounded.toLong().toString()
        else String.format(Locale.US, "%.1f", rounded)
    }
}