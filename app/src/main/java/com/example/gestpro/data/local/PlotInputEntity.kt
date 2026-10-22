package com.example.gestpro.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Insumo aplicado em um talhão (adubo, herbicida, fungicida...). */
@Entity(
    tableName = "plot_inputs",
    foreignKeys = [
        ForeignKey(
            entity = PlotEntity::class,
            parentColumns = ["id"],
            childColumns = ["plotId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plotId"])]
)
data class PlotInputEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plotId: Long,
    val name: String,
    val quantity: Double,
    val unit: String,
    val sortOrder: Int
)
