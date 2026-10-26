package com.example.gestpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Datas guardadas como epoch ms (UTC, meia-noite). Telefone guardado só com dígitos. */
@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,
    val phone: String,
    val birthDate: Long?,
    val contractType: String,
    val admissionDate: Long?,
    val salary: Double,
    val status: String,
    val notes: String,
    val photoPath: String?
)
