package com.example.gestpro.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestpro.ui.theme.GestBorder
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen

@Composable
fun GestProTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = Int.MAX_VALUE,
    errorMessage: String? = null,
    onBlur: (() -> Unit)? = null
) {
    var hadFocus by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },   // limite de caracteres (também ao colar)
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focus ->
                if (hadFocus && !focus.isFocused) onBlur?.invoke()   // dispara só ao PERDER o foco
                hadFocus = focus.isFocused
            },
        singleLine = true,
        isError = errorMessage != null,
        shape = RoundedCornerShape(20.dp),
        textStyle = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
        placeholder = {
            Text(
                text = placeholder,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = GestGray
            )
        },
        supportingText = if (errorMessage != null) {
            { Text(text = errorMessage, color = GestError, fontWeight = FontWeight.Medium) }
        } else null,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = GestBorder,
            focusedBorderColor = GestGreen,
            errorBorderColor = GestError,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            errorSupportingTextColor = GestError
        )
    )
}