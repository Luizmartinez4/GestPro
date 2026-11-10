package com.example.gestpro.ui.talhoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.PlotEntity
import com.example.gestpro.data.local.PlotInputEntity
import com.example.gestpro.data.repository.FarmRepository
import com.example.gestpro.util.PlotFormat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Linha da lista de talhões, já formatada para a tela. */
data class PlotRow(
    val id: Long,
    val number: Int,
    val title: String,   // "Talhão 01 · Café Bourbon"
    val plants: String,  // "3.550"
    val area: String     // "3,5 ha"
)

/** Linha da tabela de lançamentos, já formatada para a tela. */
data class InputRow(
    val id: Long,
    val name: String,
    val quantity: String,
    val unit: String,
    val date: String
)

data class TalhoesUiState(
    val plots: List<PlotRow> = emptyList(),
    val totalArea: String = "0",
    val isLoading: Boolean = true
)

/** Lista de talhões da aba Talhões. */
class TalhoesViewModel(repository: FarmRepository) : ViewModel() {

    val uiState: StateFlow<TalhoesUiState> = repository.observePlots()
        .map { plots ->
            TalhoesUiState(
                plots = plots.map { it.toRow() },
                totalArea = PlotFormat.decimal(plots.sumOf { it.areaHa }),
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TalhoesUiState())
}

/** Opções fixas usadas pelos formulários de talhão e de lançamento. */
object PlotOptions {
    const val DEFAULT_VARIETY = "Bourbon"
    val UNITS = listOf("KG", "TON", "L", "ML", "SC", "UN", "H", "S")
}

internal fun PlotEntity.toRow(): PlotRow {
    val varietyLabel = variety?.trim()?.takeIf { it.isNotEmpty() }?.let {
        if (it.startsWith("Café", ignoreCase = true)) it else "Café $it"
    }
    return PlotRow(
        id = id,
        number = number,
        title = "Talhão %02d".format(number) + (varietyLabel?.let { " · $it" } ?: ""),
        plants = plantCount?.let { PlotFormat.integer(it) } ?: "—",
        area = "${PlotFormat.decimal(areaHa)} ha"
    )
}

internal fun PlotInputEntity.toRow() = InputRow(
    id = id,
    name = name,
    quantity = PlotFormat.decimal(quantity),
    unit = unit.uppercase(),
    date = appliedAt?.let { PlotFormat.date(it) } ?: "—"
)
