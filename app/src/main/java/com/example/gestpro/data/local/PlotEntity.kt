package com.example.gestpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Talhão da propriedade. */
@Entity(tableName = "plots")
data class PlotEntity(
    @PrimaryKey val id: Long,
    val number: Int,
    val areaHa: Double
)
