package com.example.gestpro.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.R
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.theme.GestBarGreen
import com.example.gestpro.ui.theme.GestMuted
import com.example.gestpro.ui.theme.GestOverlay
import java.util.Calendar

enum class HomeTab(val label: String) {
    INICIO("Início"),
    TALHOES("Talhões"),
    CADASTROS("Cadastros")
}

@Composable
fun HomeScreen(
    onNewEmployee: () -> Unit,
    onOpenEmployee: (Long) -> Unit,
    onOpenEmployeeFilter: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = HomeTab.entries

    Box(Modifier.fillMaxSize()) {
        // Fundo compartilhado por todas as abas
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(GestOverlay))

        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            HomeHeader(farmName = state.farmName, safra = state.safra)
            HomeTabBar(
                tabs = tabs,
                selectedIndex = tabIndex,
                onSelect = { tabIndex = it }
            )

            when (tabs[tabIndex]) {
                HomeTab.INICIO -> InicioTab(
                    state = state,
                    onPlotClick = viewModel::onPlotSelected,
                    onHistoryClick = {
                        Toast.makeText(
                            context,
                            "O histórico do talhão ainda será implementado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HomeTab.CADASTROS -> (
                    CadastrosTab(
                        onNewEmployee = onNewEmployee,
                        onOpenEmployee = onOpenEmployee,
                        onOpenEmployeeFilter = onOpenEmployeeFilter
                    )
                )

                else -> PlaceholderTab(title = tabs[tabIndex].label)
            }
        }
    }
}

@Composable
private fun HomeHeader(farmName: String, safra: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(Modifier.weight(1f)) {
            Text(greeting(), color = GestMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(farmName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column {
            Text("Safra", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(safra, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun HomeTabBar(
    tabs: List<HomeTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp).background(GestBarGreen)
    ) {
        tabs.forEachIndexed { index, tab ->
            if (index > 0) {
                Box(
                    Modifier
                        .align(Alignment.CenterVertically)
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (index == selectedIndex) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(0.5f)
                            .height(3.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

private fun greeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Bom dia, produtor!"
        hour < 18 -> "Boa tarde, produtor!"
        else -> "Boa noite, produtor!"
    }
}
