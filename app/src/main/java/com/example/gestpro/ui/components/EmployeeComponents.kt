package com.example.gestpro.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestBorder
import com.example.gestpro.ui.theme.GestChipActive
import com.example.gestpro.ui.theme.GestChipInactive
import com.example.gestpro.ui.theme.GestDanger
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestSuccess
import com.example.gestpro.ui.theme.GestTextDark
import com.example.gestpro.util.EmployeeFormatters
import com.example.gestpro.util.PhotoUtils

@Composable
fun GestTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GestGreen)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GestGreen,
    unfocusedBorderColor = GestBorder,
    errorBorderColor = GestError,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

@Composable
private fun FieldLabel(label: String) {
    Text(label, fontSize = 12.sp, color = GestTextDark, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun FieldError(message: String?) {
    if (message != null) {
        Text(message, color = GestError, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun GestFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int? = null,
    errorMessage: String? = null
) {
    Column(modifier) {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(if (maxLength != null) it.take(maxLength) else it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = GestGray, fontSize = 14.sp) },
            singleLine = singleLine,
            minLines = minLines,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(fontSize = 14.sp),
            colors = fieldColors()
        )
        FieldError(errorMessage)
    }
}

@Composable
fun GestDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Selecione",
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        FieldLabel(label)
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = GestGray, fontSize = 14.sp) },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                isError = errorMessage != null,
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                colors = fieldColors()
            )
            Box(Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onSelect(option); expanded = false }
                    )
                }
            }
        }
        FieldError(errorMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestDateField(
    label: String,
    value: Long?,
    onValueChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    var showPicker by remember { mutableStateOf(false) }
    Column(modifier) {
        FieldLabel(label)
        Box {
            OutlinedTextField(
                value = EmployeeFormatters.date(value),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("dd/mm/aaaa", color = GestGray, fontSize = 14.sp) },
                trailingIcon = { Icon(Icons.Filled.DateRange, null, tint = GestGray) },
                isError = errorMessage != null,
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                colors = fieldColors()
            )
            Box(Modifier.matchParentSize().clickable { showPicker = true })
        }
        FieldError(errorMessage)
    }
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = value)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = { onValueChange(state.selectedDateMillis); showPicker = false }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = state) }
    }
}

@Composable
fun EmployeeAvatar(photoPath: String?, size: Dp, modifier: Modifier = Modifier) {
    val bitmap = remember(photoPath) { PhotoUtils.load(photoPath) }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, GestBorder, CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(bitmap, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Filled.Person, null, tint = GestGray, modifier = Modifier.size(size * 0.7f))
        }
    }
}

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val active = status == "Ativo"
    Text(
        text = status,
        color = if (active) GestSuccess else GestDanger,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) GestChipActive else GestChipInactive)
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}

@Composable
fun GestActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GestBlue,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = color)
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
        } else {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
