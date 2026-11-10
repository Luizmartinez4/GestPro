package com.example.gestpro.ui.talhoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.repository.FarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PlotInputsUiState(
    /** null depois de carregar = talhão não existe (ex.: foi excluído). */
    val plot: PlotRow? = null,
    val inputs: List<InputRow> = emptyList(),
    val isLoading: Boolean = true
)

/** Tela "Lançamentos por lote/talhão" (rota talhao/{plotId}). */
class PlotInputsViewModel(
    savedStateHandle: SavedStateHandle,
    repository: FarmRepository
) : ViewModel() {

    val plotId: Long = savedStateHandle.get<Long>("plotId") ?: 0L

    val uiState: StateFlow<PlotInputsUiState> = combine(
        repository.observePlot(plotId),
        repository.observeInputs(plotId)
    ) { plot, inputs ->
        PlotInputsUiState(
            plot = plot?.toRow(),
            inputs = inputs.map { it.toRow() },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlotInputsUiState())
}
