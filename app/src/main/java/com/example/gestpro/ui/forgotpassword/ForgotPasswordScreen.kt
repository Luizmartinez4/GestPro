package com.example.gestpro.ui.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.GestProTextField
import com.example.gestpro.ui.theme.GestGray
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestSuccess
import com.example.gestpro.ui.theme.GestTextDark
import com.example.gestpro.util.Limits

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Recuperar senha", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = GestGreen)
        Text(
            "Informe o email cadastrado. Enviaremos um link para você criar uma nova senha.",
            color = GestGray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Text("Email", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GestTextDark)
        GestProTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Digite seu email",
            keyboardType = KeyboardType.Email,
            maxLength = Limits.EMAIL_MAX,
            errorMessage = state.emailError,
            onBlur = viewModel::onEmailBlur
        )

        if (state.emailSent) {
            Text(
                "Enviamos o link para ${state.email}. Verifique também a caixa de spam.",
                color = GestSuccess,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = viewModel::sendLink,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(0.6f).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GestGreen)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Enviar link", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }

        TextButton(onClick = onBack) {
            Text("Voltar para o login", color = GestGray, fontWeight = FontWeight.Bold)
        }
    }
}