package com.example.gestpro.ui.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.data.local.EmployeeEntity
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.EmployeeAvatar
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestBorder
import com.example.gestpro.ui.theme.GestDanger
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestSuccess
import com.example.gestpro.util.EmployeeFormatters

@Composable
fun EmployeeListScreen(
    onNewEmployee: () -> Unit,
    onOpenEmployee: (Long) -> Unit,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier.fillMaxSize().background(Color.White)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Funcionários", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GestGreen)
                Text(countLabel(state.totalCount), fontSize = 11.sp, color = GestGray)
            }
            Button(
                onClick = onNewEmployee,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GestBlue)
            ) {
                Text("Novo", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar funcionário", fontSize = 13.sp, color = GestGray) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = GestGray) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GestGreen,
                    unfocusedBorderColor = GestBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            IconButton(onClick = onOpenFilter) {
                BadgedBox(badge = { if (state.activeFilters > 0) Badge { Text("${state.activeFilters}") } }) {
                    Icon(Icons.Filled.FilterList, "Filtrar", tint = GestGreen)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.employees.isEmpty() && !state.isLoading) {
                item {
                    Text(
                        text = if (state.totalCount == 0)
                            "Nenhum funcionário cadastrado.\nToque em «Novo» para começar."
                        else "Nenhum funcionário encontrado.",
                        color = GestGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                    )
                }
            }
            items(state.employees, key = { it.id }) { employee ->
                EmployeeRow(employee, onClick = { onOpenEmployee(employee.id) })
            }
        }
    }
}

@Composable
private fun EmployeeRow(employee: EmployeeEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            EmployeeAvatar(employee.photoPath, 52.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(employee.role, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Call, null, tint = GestGray, modifier = Modifier.padding(end = 4.dp).width(12.dp))
                    Text(EmployeeFormatters.phone(employee.phone).ifEmpty { "Sem telefone" }, fontSize = 11.sp, color = GestGray)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        employee.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (employee.status == EmployeeOptions.STATUS_ACTIVE) GestSuccess else GestDanger
                    )
                }
            }
            Text("»", fontSize = 20.sp, color = GestGreen, fontWeight = FontWeight.Bold)
        }
    }
}

private fun countLabel(n: Int) = when (n) {
    0 -> "Nenhum funcionário cadastrado"
    1 -> "1 funcionário cadastrado"
    else -> "$n funcionários cadastrados"
}
