package com.example.gestpro.ui.talhoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.local.PlotEntity
import com.example.gestpro.data.repository.FarmRepository
import com.example.gestpro.util.PlotFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlotFormUiState(
    val isEditing: Boolean = false,
    val number: String = "",
    val plantCount: String = "",
    val area: String = "",
    val registeredAt: Long? = null,
    val variety: String = "",
    val notes: String = "",
    val numberError: String? = null,
    val plantCountError: String? = null,
    val areaError: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    /** Não nulo = salvou; a tela navega e chama onSavedHandled(). */
    val savedPlotId: Long? = null,
    /** Salvou pelo botão "Novo Lançamento": abrir o formulário de lançamento. */
    val openNewInputAfterSave: Boolean = false,
    val deleted: Boolean = false,
    val notFound: Boolean = false
) {
    val isBusy: Boolean get() = isSaving || isDeleting
}

/** Formulário de talhão (rota talhao_form?plotId={plotId}; 0 = novo). */
class PlotFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FarmRepository
) : ViewModel() {

    private val plotId: Long = savedStateHandle.get<Long>("plotId") ?: 0L

    private val _uiState = MutableStateFlow(PlotFormUiState(isEditing = plotId != 0L))
    val uiState: StateFlow<PlotFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (plotId == 0L) {
                val next = repository.nextPlotNumber()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        number = next.toString(),
                        registeredAt = PlotFormat.todayUtc(),
                        variety = PlotOptions.DEFAULT_VARIETY
                    )
                }
            } else {
                val p = repository.getPlot(plotId)
                if (p == null) {
                    _uiState.update { it.copy(isLoading = false, notFound = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            number = p.number.toString(),
                            plantCount = p.plantCount?.toString().orEmpty(),
                            area = PlotFormat.decimalInput(p.areaHa),
                            registeredAt = p.registeredAt,
                            variety = p.variety.orEmpty(),
                            notes = p.notes.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun onNumberChange(v: String) =
        _uiState.update { it.copy(number = v.filter(Char::isDigit).take(3), numberError = null) }
    fun onPlantCountChange(v: String) =
        _uiState.update { it.copy(plantCount = v.filter(Char::isDigit).take(9), plantCountError = null) }
    fun onAreaChange(v: String) = _uiState.update { it.copy(area = v.take(12), areaError = null) }
    fun onRegisteredAtChange(v: Long?) = _uiState.update { it.copy(registeredAt = v) }
    fun onVarietyChange(v: String) = _uiState.update { it.copy(variety = v.take(40)) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v.take(300)) }

    /**
     * Salva o talhão. Com [thenNewInput] = true (botão "Novo Lançamento"),
     * a tela abre os lançamentos dele já com o formulário de novo lançamento.
     */
    fun onSaveClick(thenNewInput: Boolean = false) {
        val form = _uiState.value
        if (form.isBusy || form.isLoading) return

        val number = form.number.toIntOrNull()
        val plants = form.plantCount.toIntOrNull()
        val area = PlotFormat.parseDecimal(form.area)

        val checked = form.copy(
            numberError = if (number == null || number <= 0) "Informe o número" else null,
            plantCountError = if (form.plantCount.isNotEmpty() && plants == null) "Valor inválido" else null,
            areaError = if (area == null || area <= 0.0) "Informe a área" else null
        )
        if (checked.numberError != null || checked.plantCountError != null || checked.areaError != null ||
            number == null || area == null
        ) {
            _uiState.value = checked
            return
        }

        _uiState.value = checked.copy(isSaving = true)
        viewModelScope.launch {
            runCatching {
                if (repository.isPlotNumberTaken(number, plotId)) {
                    null
                } else {
                    repository.savePlot(
                        PlotEntity(
                            id = plotId,
                            number = number,
                            areaHa = area,
                            variety = form.variety.trim().ifBlank { null },
                            plantCount = plants,
                            registeredAt = form.registeredAt,
                            notes = form.notes.trim().ifBlank { null }
                        )
                    )
                }
            }.onSuccess { savedId ->
                _uiState.update {
                    if (savedId == null) it.copy(isSaving = false, numberError = "Já existe esse talhão")
                    else it.copy(isSaving = false, savedPlotId = savedId, openNewInputAfterSave = thenNewInput)
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isSaving = false, errorMessage = err.message ?: "Erro ao salvar") }
            }
        }
    }

    fun onSavedHandled() = _uiState.update { it.copy(savedPlotId = null, openNewInputAfterSave = false) }

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }
    fun onDeleteDismiss() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun onDeleteConfirm() {
        if (!_uiState.value.isEditing) return
        _uiState.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            runCatching { repository.deletePlot(plotId) }
                .onSuccess { _uiState.update { it.copy(isDeleting = false, deleted = true) } }
                .onFailure { err ->
                    _uiState.update { it.copy(isDeleting = false, errorMessage = err.message ?: "Erro ao excluir") }
                }
        }
    }

    fun onDeletedHandled() = _uiState.update { it.copy(deleted = false) }
    fun onErrorHandled() = _uiState.update { it.copy(errorMessage = null) }
}
