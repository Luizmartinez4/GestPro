package com.example.gestpro.ui.talhoes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.theme.GestCardGreen
import com.example.gestpro.ui.theme.GestMuted

private val SummaryDark = Color(0xE61E2A21)

/** Aba "Talhões" da Home: resumo, botão "Novo talhão" e lista. */
@Composable
fun TalhoesTab(
    onOpenPlot: (Long) -> Unit,
    onNewPlot: () -> Unit,
    viewModel: TalhoesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SummaryPill(count = state.plots.size, area = state.totalArea, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            BlueButton(text = "Novo talhão", onClick = onNewPlot)
        }
        Spacer(Modifier.height(12.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
            }

            state.plots.isEmpty() -> Text(
                "Nenhum talhão cadastrado ainda.",
                color = GestMuted,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                textAlign = TextAlign.Center
            )

            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.plots, key = { it.id }) { plot ->
                    PlotCard(plot = plot, onClick = { onOpenPlot(plot.id) })
                }
            }
        }
    }
}

@Composable
private fun SummaryPill(count: Int, area: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(SummaryDark)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            if (count == 1) "1 Talhão" else "$count Talhões",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text("$area ha", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun PlotCard(plot: PlotRow, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(GestCardGreen)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                plot.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("Plantas: ${plot.plants}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(plot.area, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(">>", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
