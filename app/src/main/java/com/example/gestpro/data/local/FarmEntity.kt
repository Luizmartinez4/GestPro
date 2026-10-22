package com.example.gestpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Propriedade rural. Por enquanto existe apenas um registro (id = 1). */
@Entity(tableName = "farm")
data class FarmEntity(
    @PrimaryKey val id: Long = 1,
    val name: String,
    val safra: String,
    val cultivatedAreaHa: Double
)
