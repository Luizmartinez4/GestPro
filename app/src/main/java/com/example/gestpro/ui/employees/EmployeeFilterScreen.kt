package com.example.gestpro.ui.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.GestActionButton
import com.example.gestpro.ui.components.GestDateField
import com.example.gestpro.ui.components.GestDropdown
import com.example.gestpro.ui.components.GestTopBar
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestTextDark

@Composable
fun EmployeeFilterScreen(
    onBack: () -> Unit,
    viewModel: EmployeeFilterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val f = state.filter
    val all = EmployeeOptions.ALL

    Column(Modifier.fillMaxSize().background(Color.White)) {
        GestTopBar("Filtro Funcionários", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Close, "Fechar", tint = GestTextDark, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(12.dp))
                Text("Filtrar funcionários", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(
                    "Limpar", color = GestGreen, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = viewModel::onClearClick)
                )
            }
            Spacer(Modifier.height(20.dp))
            GestDropdown(
                "Cargo / Função", f.role ?: all, listOf(all) + EmployeeOptions.roles,
                { viewModel.onRoleChange(it.takeUnless { v -> v == all }) }
            )
            Spacer(Modifier.height(14.dp))
            GestDropdown(
                "Status", f.status ?: all, listOf(all) + EmployeeOptions.statuses,
                { viewModel.onStatusChange(it.takeUnless { v -> v == all }) }
            )
            Spacer(Modifier.height(14.dp))
            GestDropdown(
                "Tipo de contrato", f.contractType ?: all, listOf(all) + EmployeeOptions.contractTypes,
                { viewModel.onContractTypeChange(it.takeUnless { v -> v == all }) }
            )
            Spacer(Modifier.height(14.dp))
            Text("Período de admissão", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = GestTextDark)
            Spacer(Modifier.height(6.dp))
            Row {
                GestDateField("De", f.admissionFrom, viewModel::onAdmissionFromChange, Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                GestDateField("Até", f.admissionTo, viewModel::onAdmissionToChange, Modifier.weight(1f))
            }
            state.dateError?.let {
                Text(it, color = GestError, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(28.dp))
            GestActionButton("Aplicar Filtro", onClick = { if (viewModel.onApplyClick()) onBack() }, color = GestBlue)
        }
    }
}
