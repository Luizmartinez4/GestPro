package com.example.gestpro.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestpro.R
import com.example.gestpro.ui.employees.EmployeeListScreen
import com.example.gestpro.ui.theme.GestBlue

private const val MENU = "menu"
private const val EMPLOYEES = "employees"

/**
 * Aba «Cadastros» da Home. Por enquanto só tem «Funcionários»
 * (Insumos, Serviços e Diversos entram nas próximas etapas).
 */
@Composable
fun CadastrosTab(
    onNewEmployee: () -> Unit,
    onOpenEmployee: (Long) -> Unit,
    onOpenEmployeeFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var screen by rememberSaveable { mutableStateOf(MENU) }

    if (screen == EMPLOYEES) {
        BackHandler { screen = MENU }
        EmployeeListScreen(
            onNewEmployee = onNewEmployee,
            onOpenEmployee = onOpenEmployee,
            onOpenFilter = onOpenEmployeeFilter,
            modifier = modifier
        )
    } else {
        Column(modifier.fillMaxSize().padding(16.dp)) {
            CadastroCard("Funcionários", R.drawable.cadastro_funcionarios) { screen = EMPLOYEES }
        }
    }
}

@Composable
private fun CadastroCard(label: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(4.dp)) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = label,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxWidth().padding(top = 4.dp).background(GestBlue, RoundedCornerShape(6.dp)).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
