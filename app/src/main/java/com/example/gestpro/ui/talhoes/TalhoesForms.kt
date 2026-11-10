package com.example.gestpro.ui.talhoes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestDanger
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.util.PlotFormat

private val PanelGray = Color(0xFFE0E0E0)
private val FieldGray = Color(0xFFB8B8B8)
private val FieldTextStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)

// ---------------------------------------------------------------------------
// Formulário de talhão — rota talhao_form?plotId={plotId}
// ---------------------------------------------------------------------------

@Composable
fun PlotFormScreen(
    onBack: () -> Unit,
    /** Salvou pelo botão "Novo Lançamento": abrir os lançamentos do talhão com um novo. */
    onSavedOpenNewInput: (plotId: Long) -> Unit,
    /** Talhão excluído: voltar para a Home (a tela de lançamentos dele não existe mais). */
    onDeleted: () -> Unit,
    viewModel: PlotFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.savedPlotId) {
        val id = state.savedPlotId ?: return@LaunchedEffect
        val openNewInput = state.openNewInputAfterSave
        val msg = if (state.isEditing) "Talhão atualizado com sucesso!" else "Talhão cadastrado com sucesso!"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        viewModel.onSavedHandled()
        if (openNewInput) onSavedOpenNewInput(id) else onBack()
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            Toast.makeText(context, "Talhão excluído", Toast.LENGTH_SHORT).show()
            viewModel.onDeletedHandled()
            onDeleted()
        }
    }
    LaunchedEffect(state.notFound) {
        if (state.notFound) {
            Toast.makeText(context, "Talhão não encontrado", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onErrorHandled()
        }
    }

    FormPanelScreen(
        title = if (state.isEditing) "Editar talhão" else "Novo talhão",
        isLoading = state.isLoading,
        onClose = onBack
    ) {
        FormRow("Código Talhão", error = state.numberError) {
            GrayTextField(
                value = state.number,
                onValueChange = viewModel::onNumberChange,
                keyboardType = KeyboardType.Number,
                prefix = "Talhão ",
                isError = state.numberError != null
            )
        }
        FormRow("Quantidade pés", error = state.plantCountError) {
            GrayTextField(
                value = state.plantCount,
                onValueChange = viewModel::onPlantCountChange,
                keyboardType = KeyboardType.Number,
                isError = state.plantCountError != null
            )
        }
        FormRow("Área plantada", error = state.areaError) {
            GrayTextField(
                value = state.area,
                onValueChange = viewModel::onAreaChange,
                keyboardType = KeyboardType.Decimal,
                suffix = " ha",
                isError = state.areaError != null
            )
        }
        FormRow("Data lançamento", labelIcon = Icons.Outlined.CalendarMonth) {
            GrayDateField(value = state.registeredAt, onValueChange = viewModel::onRegisteredAtChange)
        }
        FormRow("Variedade") {
            GrayTextField(value = state.variety, onValueChange = viewModel::onVarietyChange)
        }

        Spacer(Modifier.height(24.dp))
        Text("Observação", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        GrayTextField(
            value = state.notes,
            onValueChange = viewModel::onNotesChange,
            singleLine = false,
            minHeight = 44.dp
        )

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanelButton(
                "Novo Lançamento", GestBlue, { viewModel.onSaveClick(thenNewInput = true) },
                Modifier.weight(1f), enabled = !state.isBusy
            )
            PanelButton(
                "Salvar", GestGreen, { viewModel.onSaveClick() },
                Modifier.weight(1f), enabled = !state.isBusy, isLoading = state.isSaving
            )
        }
        if (state.isEditing) {
            Spacer(Modifier.height(10.dp))
            PanelButton(
                "Excluir talhão", GestDanger, viewModel::onDeleteClick,
                Modifier.fillMaxWidth(), enabled = !state.isBusy, isLoading = state.isDeleting
            )
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            title = { Text("Excluir Talhão ${state.number.padStart(2, '0')}?") },
            text = {
                Text("Todos os lançamentos deste talhão também serão excluídos. Esta ação não pode ser desfeita.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) { Text("Excluir", color = GestDanger) }
            },
            dismissButton = { TextButton(onClick = viewModel::onDeleteDismiss) { Text("Cancelar") } }
        )
    }
}

// ---------------------------------------------------------------------------
// Formulário de lançamento — rota talhao/{plotId}/lancamento/{inputId}
// ---------------------------------------------------------------------------

@Composable
fun InputFormScreen(
    onBack: () -> Unit,
    viewModel: InputFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) {
            val msg = if (state.isEditing) "Lançamento atualizado!" else "Lançamento cadastrado!"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onSavedHandled()
            onBack()
        }
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            Toast.makeText(context, "Lançamento excluído", Toast.LENGTH_SHORT).show()
            viewModel.onDeletedHandled()
            onBack()
        }
    }
    LaunchedEffect(state.notFound) {
        if (state.notFound) {
            Toast.makeText(context, "Lançamento não encontrado", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onErrorHandled()
        }
    }

    FormPanelScreen(
        title = if (state.isEditing) "Editar lançamento" else "Novo lançamento",
        isLoading = state.isLoading,
        onClose = onBack
    ) {
        FormRow("Insumo", error = state.nameError) {
            GrayTextField(value = state.name, onValueChange = viewModel::onNameChange, isError = state.nameError != null)
        }
        FormRow("Quantidade", error = state.quantityError) {
            GrayTextField(
                value = state.quantity,
                onValueChange = viewModel::onQuantityChange,
                keyboardType = KeyboardType.Decimal,
                isError = state.quantityError != null
            )
        }
        FormRow("Unidade") {
            GrayDropdown(value = state.unit, options = PlotOptions.UNITS, onSelect = viewModel::onUnitChange)
        }
        FormRow("Data lançamento", labelIcon = Icons.Outlined.CalendarMonth) {
            GrayDateField(value = state.appliedAt, onValueChange = viewModel::onAppliedAtChange)
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (state.isEditing) {
                PanelButton(
                    "Excluir", GestDanger, viewModel::onDeleteClick,
                    Modifier.weight(1f), enabled = !state.isBusy, isLoading = state.isDeleting
                )
            } else {
                PanelButton("Cancelar", GestGray, onBack, Modifier.weight(1f))
            }
            PanelButton(
                "Salvar", GestGreen, viewModel::onSaveClick,
                Modifier.weight(1f), enabled = !state.isBusy, isLoading = state.isSaving
            )
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            title = { Text("Excluir lançamento?") },
            text = { Text("Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) { Text("Excluir", color = GestDanger) }
            },
            dismissButton = { TextButton(onClick = viewModel::onDeleteDismiss) { Text("Cancelar") } }
        )
    }
}

// ---------------------------------------------------------------------------
// Peças do painel cinza
// ---------------------------------------------------------------------------

/** Tela inteira com o fundo da Home escurecido e o painel cinza no centro. */
@Composable
private fun FormPanelScreen(
    title: String,
    isLoading: Boolean,
    onClose: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    TalhoesBackground(dim = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PanelGray)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, "Fechar", tint = Color.Black)
                    }
                }
                if (isLoading) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GestGreen)
                    }
                } else {
                    Column(Modifier.padding(end = 8.dp), content = content)
                }
            }
        }
    }
}

@Composable
private fun FormRow(
    label: String,
    error: String? = null,
    labelIcon: ImageVector? = null,
    field: @Composable () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (labelIcon != null) {
                    Spacer(Modifier.width(4.dp))
                    Icon(labelIcon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
            Box(Modifier.weight(1f)) { field() }
        }
        if (error != null) {
            Text(
                error,
                color = GestError,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun GrayTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    suffix: String? = null,
    singleLine: Boolean = true,
    minHeight: Dp = 0.dp,
    isError: Boolean = false
) {
    val shape = RoundedCornerShape(3.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = FieldTextStyle,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(shape)
            .background(FieldGray)
            .then(if (isError) Modifier.border(1.5.dp, GestError, shape) else Modifier)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (prefix != null) Text(prefix, style = FieldTextStyle)
                Box(Modifier.weight(1f)) { inner() }
                if (suffix != null && value.isNotEmpty()) Text(suffix, style = FieldTextStyle)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrayDateField(value: Long?, onValueChange: (Long?) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    Text(
        text = if (value != null) PlotFormat.date(value) else "00/00/0000",
        style = FieldTextStyle,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(FieldGray)
            .clickable { showPicker = true }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    )
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = value)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = { onValueChange(state.selectedDateMillis); showPicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }
}

@Composable
private fun GrayDropdown(value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(FieldGray)
                .clickable { expanded = true }
                .padding(start = 8.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, style = FieldTextStyle, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Black)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun PanelButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
        }
    }
}
