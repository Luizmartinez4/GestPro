package com.example.gestpro.ui.employees

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.EmployeeAvatar
import com.example.gestpro.ui.components.GestActionButton
import com.example.gestpro.ui.components.GestTopBar
import com.example.gestpro.ui.components.StatusChip
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestBorder
import com.example.gestpro.ui.theme.GestDanger
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestTextDark
import com.example.gestpro.util.EmployeeFormatters

@Composable
fun EmployeeDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: EmployeeDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            Toast.makeText(context, "Funcionário excluído", Toast.LENGTH_SHORT).show()
            viewModel.onDeletedHandled()
            onBack()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onErrorHandled()
        }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        GestTopBar("Consulta Funcionários", onBack)
        val e = state.employee
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = GestGreen) }
            e == null -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Funcionário não encontrado", color = GestGray) }
            else -> Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EmployeeAvatar(e.photoPath, 80.dp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(e.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(e.role, color = GestGray, fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))
                        StatusChip(e.status)
                    }
                }
                Spacer(Modifier.height(20.dp))
                InfoRow(Icons.Filled.Call, EmployeeFormatters.phone(e.phone).ifEmpty { "Não informado" })
                val age = EmployeeFormatters.age(e.birthDate)
                InfoRow(
                    Icons.Filled.DateRange,
                    if (e.birthDate == null) "Não informado"
                    else "${EmployeeFormatters.date(e.birthDate)}" + (age?.let { " ($it anos)" } ?: "")
                )
                InfoRow(Icons.Filled.Work, e.role)
                InfoRow(Icons.Filled.Description, "Contrato", e.contractType)
                InfoRow(Icons.Filled.DateRange, "Data de admissão", EmployeeFormatters.date(e.admissionDate).ifEmpty { "—" })
                InfoRow(Icons.Filled.AttachMoney, "Salário / Diária", EmployeeFormatters.currency(e.salary))
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = GestBorder)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Description, null, tint = GestTextDark, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Observações", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(
                    e.notes.ifBlank { "Sem observações." },
                    fontSize = 13.sp,
                    color = GestTextDark,
                    modifier = Modifier.padding(start = 30.dp, top = 6.dp)
                )
                Spacer(Modifier.height(24.dp))
                GestActionButton("Editar Funcionário", onClick = { onEdit(e.id) }, color = GestBlue)
                Spacer(Modifier.height(12.dp))
                GestActionButton("Excluir Funcionário", onClick = viewModel::onDeleteClick, color = GestDanger, isLoading = state.isDeleting)
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            title = { Text("Excluir funcionário?") },
            text = { Text("Esta ação não pode ser desfeita.") },
            confirmButton = { TextButton(onClick = viewModel::onDeleteConfirm) { Text("Excluir", color = GestDanger) } },
            dismissButton = { TextButton(onClick = viewModel::onDeleteDismiss) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String, trailing: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = GestTextDark, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, fontSize = 14.sp)
        }
        if (trailing != null) Text(trailing, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
