package com.example.gestpro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.repository.FarmRepository
import com.example.gestpro.util.Formatters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class PlotItem(val id: Long, val number: Int, val areaHa: Double)

data class InputItem(val name: String, val amount: String)

data class HomeUiState(
    val farmName: String = "",
    val safra: String = "",
    val totalPlots: Int = 0,
    val cultivatedAreaHa: Double = 0.0,
    val plots: List<PlotItem> = emptyList(),
    val selectedPlotId: Long = HomeViewModel.DEFAULT_PLOT_ID,
    val selectedInputs: List<InputItem> = emptyList(),
    val isLoading: Boolean = true
) {
    val selectedPlot: PlotItem? get() = plots.firstOrNull { it.id == selectedPlotId }
}

class HomeViewModel(repository: FarmRepository) : ViewModel() {

    private val selectedPlotId = MutableStateFlow(DEFAULT_PLOT_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeFarm(),
        repository.observePlots(),
        selectedPlotId,
        selectedPlotId.flatMapLatest { repository.observeInputs(it) }
    ) { farm, plots, selectedId, inputs ->
        HomeUiState(
            farmName = farm?.name.orEmpty(),
            safra = farm?.safra.orEmpty(),
            totalPlots = plots.size,
            cultivatedAreaHa = plots.sumOf { it.areaHa },
            plots = plots.map { PlotItem(it.id, it.number, it.areaHa) },
            selectedPlotId = selectedId,
            selectedInputs = inputs.map {
                InputItem(it.name, "${Formatters.number(it.quantity)} ${it.unit}")
            },
            isLoading = farm == null && plots.isEmpty()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onPlotSelected(plotId: Long) {
        selectedPlotId.value = plotId
    }

    companion object {
        const val DEFAULT_PLOT_ID = 1L
    }
}
