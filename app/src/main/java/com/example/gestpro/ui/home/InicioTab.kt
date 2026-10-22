package com.example.gestpro.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestpro.ui.theme.GestAction
import com.example.gestpro.ui.theme.GestCardGreen
import com.example.gestpro.ui.theme.GestCardSelected
import com.example.gestpro.ui.theme.GestMuted
import com.example.gestpro.ui.theme.GestPanel
import com.example.gestpro.util.Formatters

private const val GRID_COLUMNS = 4

@Composable
fun InicioTab(
    state: HomeUiState,
    onPlotClick: (Long) -> Unit,
    onHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SectionLabel("Totalizadores")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TotalCard("Talhões", state.totalPlots.toString(), null, Modifier.weight(1f))
                TotalCard(
                    "Área cultivada",
                    Formatters.number(state.cultivatedAreaHa),
                    "ha.",
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(4.dp))
            SectionLabel("Talhões")

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                PlotGrid(state.plots, state.selectedPlotId, onPlotClick)
            }
        }

        SelectedPanel(state, onHistoryClick)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun TotalCard(label: String, value: String, unit: String?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .shadow(8.dp, shape)
            .clip(shape)
            .background(GestCardGreen)
            .padding(12.dp)
    ) {
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(value, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            if (unit != null) {
                Text(
                    unit,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PlotGrid(plots: List<PlotItem>, selectedId: Long, onClick: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        plots.chunked(GRID_COLUMNS).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { plot ->
                    PlotCard(
                        plot = plot,
                        selected = plot.id == selectedId,
                        onClick = { onClick(plot.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Completa a última linha para manter o tamanho dos cards
                repeat(GRID_COLUMNS - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun PlotCard(
    plot: PlotItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .aspectRatio(1.05f)
            .clip(shape)
            .background(if (selected) GestCardSelected else GestCardGreen)
            .then(if (selected) Modifier.border(BorderStroke(1.5.dp, Color.White), shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text("Talhão", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                plot.number.toString(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${Formatters.number(plot.areaHa)}\nha.",
                color = Color.White,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SelectedPanel(state: HomeUiState, onHistoryClick: () -> Unit) {
    val plot = state.selectedPlot

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GestPanel)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text("Selecionado", color = GestMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Text(
            text = plot?.let { "Talhão %02d".format(it.number) } ?: "Nenhum talhão",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))

        if (state.selectedInputs.isEmpty()) {
            Text(
                "Nenhum insumo registrado para este talhão.",
                color = GestMuted,
                fontWeight = FontWeight.Medium
            )
        } else {
            state.selectedInputs.forEach { input ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(input.name, color = GestMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(input.amount, color = GestMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        DottedDivider()
        Spacer(Modifier.height(20.dp))

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = onHistoryClick,
                enabled = plot != null,
                modifier = Modifier.fillMaxWidth(0.8f).height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GestAction)
            ) {
                Text(
                    "Ver histórico do talhão",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

/** Linha fina com um ponto em cada ponta, como no design. */
@Composable
private fun DottedDivider() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(5.dp).background(Color.White, CircleShape))
        Box(Modifier.weight(1f).height(1.dp).background(Color.White))
        Box(Modifier.size(5.dp).background(Color.White, CircleShape))
    }
}
