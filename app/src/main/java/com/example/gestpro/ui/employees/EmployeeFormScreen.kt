package com.example.gestpro.ui.employees

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.EmployeeAvatar
import com.example.gestpro.ui.components.GestActionButton
import com.example.gestpro.ui.components.GestDateField
import com.example.gestpro.ui.components.GestDropdown
import com.example.gestpro.ui.components.GestFormField
import com.example.gestpro.ui.components.GestTopBar
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestBorder
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestTextDark
import com.example.gestpro.util.EmployeeFormatters
import com.example.gestpro.util.EmployeeLimits
import com.example.gestpro.util.PhotoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EmployeeFormScreen(
    onBack: () -> Unit,
    viewModel: EmployeeFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch {
            val path = withContext(Dispatchers.IO) { PhotoUtils.copyToInternal(context, uri) }
            if (path != null) viewModel.onPhotoChange(path)
        }
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            val msg = if (state.isEditing) "Funcionário atualizado com sucesso!" else "Funcionário cadastrado com sucesso!"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onSavedHandled()
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
        GestTopBar(if (state.isEditing) "Detalhes Funcionários" else "Novo Funcionário", onBack)

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = GestGreen) }
            return@Column
        }

        Column(
            Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("Dados pessoais", color = GestGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(84.dp).clickable {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    EmployeeAvatar(state.photoPath, 80.dp)
                    Box(
                        Modifier.align(Alignment.BottomEnd).size(28.dp).clip(CircleShape)
                            .background(Color.White).border(1.dp, GestBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Filled.CameraAlt, "Alterar foto", tint = GestTextDark, modifier = Modifier.size(16.dp)) }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    GestFormField(
                        label = "Nome completo *", value = state.name, onValueChange = viewModel::onNameChange,
                        maxLength = EmployeeLimits.NAME_MAX, errorMessage = state.nameError
                    )
                    Spacer(Modifier.height(8.dp))
                    GestDropdown(
                        label = "Cargo / Função *", value = state.role, options = EmployeeOptions.roles,
                        onSelect = viewModel::onRoleChange, errorMessage = state.roleError
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            GestFormField(
                label = "Telefone", value = EmployeeFormatters.phone(state.phone), onValueChange = viewModel::onPhoneChange,
                placeholder = "(00) 00000-0000", keyboardType = KeyboardType.Phone, errorMessage = state.phoneError
            )
            Spacer(Modifier.height(8.dp))
            GestDateField("Data de nascimento", state.birthDate, viewModel::onBirthDateChange)

            Spacer(Modifier.height(18.dp))
            Text("Informações adicionais", color = GestGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            Row {
                GestDropdown(
                    "Tipo de contrato", state.contractType, EmployeeOptions.contractTypes,
                    viewModel::onContractTypeChange, Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                GestDateField("Data de admissão", state.admissionDate, viewModel::onAdmissionDateChange, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row {
                GestFormField(
                    label = "Salário / Diária (R$)", value = state.salary, onValueChange = viewModel::onSalaryChange,
                    modifier = Modifier.weight(1f), placeholder = "0,00",
                    keyboardType = KeyboardType.Decimal, errorMessage = state.salaryError
                )
                Spacer(Modifier.width(10.dp))
                GestDropdown(
                    "Status", state.status, EmployeeOptions.statuses,
                    viewModel::onStatusChange, Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            GestFormField(
                label = "Observações", value = state.notes, onValueChange = viewModel::onNotesChange,
                singleLine = false, minLines = 3, maxLength = EmployeeLimits.NOTES_MAX
            )

            Spacer(Modifier.height(20.dp))
            GestActionButton("Salvar Funcionário", onClick = viewModel::onSaveClick, color = GestBlue, isLoading = state.isSaving)
        }
    }
}
