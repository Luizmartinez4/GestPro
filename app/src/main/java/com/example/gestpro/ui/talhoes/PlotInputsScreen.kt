package com.example.gestpro.ui.talhoes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestMuted

private val TitleCardGreen = Color(0xFF063A0C)
private val RowGreen = Color(0xFF0A4512)

/** Tela "Lançamentos por lote/talhão" (rota talhao/{plotId}). */
@Composable
fun PlotInputsScreen(
    onBack: () -> Unit,
    onEditPlot: (Long) -> Unit,
    onNewInput: (Long) -> Unit,
    onEditInput: (plotId: Long, inputId: Long) -> Unit,
    viewModel: PlotInputsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val plotId = viewModel.plotId

    TalhoesBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GestGreen)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Lançamentos por lote/talhão",
                    color = GestMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.Close, "Fechar", tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }

            val plot = state.plot
            when {
                state.isLoading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                }

                plot == null -> EmptyText("Talhão não encontrado.")

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TitleCardGreen)
                            .clickable { onEditPlot(plotId) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            plot.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(Icons.Outlined.Edit, "Editar talhão", tint = Color.White)
                    }

                    Spacer(Modifier.height(14.dp))
                    BlueButton(
                        text = "Novo Lançamento",
                        onClick = { onNewInput(plotId) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.padding(horizontal = 10.dp)) {
                        HeaderCell("Insumo", 1.5f)
                        HeaderCell("Qtd", 0.9f)
                        HeaderCell("UN", 0.6f)
                        HeaderCell("Data lançamento", 1.4f)
                        Spacer(Modifier.width(30.dp))
                    }
                    Spacer(Modifier.height(6.dp))

                    if (state.inputs.isEmpty()) {
                        EmptyText("Nenhum lançamento neste talhão.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            items(state.inputs, key = { it.id }) { input ->
                                InputRowItem(input = input, onEdit = { onEditInput(plotId, input.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text,
        color = GestMuted,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        modifier = Modifier.weight(weight)
    )
}

@Composable
private fun InputRowItem(input: InputRow, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(RowGreen)
            .clickable(onClick = onEdit)
            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(input.name, 1.5f, maxLines = 2)
        Cell(input.quantity, 0.9f)
        Cell(input.unit, 0.6f)
        Cell(input.date, 1.4f)
        Box(Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Edit, "Editar lançamento", tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, maxLines: Int = 1) {
    Text(
        text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(weight).padding(end = 4.dp)
    )
}
