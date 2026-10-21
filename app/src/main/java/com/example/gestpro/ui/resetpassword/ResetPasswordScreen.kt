package com.example.gestpro.ui.resetpassword

import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestpro.ui.AppViewModelProvider
import com.example.gestpro.ui.components.GestProTextField
import com.example.gestpro.ui.components.PasswordRequirements
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestGreen
import com.example.gestpro.ui.theme.GestTextDark
import com.example.gestpro.util.Limits

@Composable
fun ResetPasswordScreen(
    onSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
            onSuccess()
            viewModel.onSuccessHandled()
        }
    }

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
        Text("Nova senha", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = GestGreen)
        Spacer(Modifier.height(16.dp))

        Text("Senha", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GestTextDark)
        GestProTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Crie uma nova senha",
            isPassword = true,
            maxLength = Limits.PASSWORD_MAX
        )
        if (state.showPasswordRules) {
            PasswordRequirements(rules = state.passwordRules)
        }

        Text("Confirmar senha", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GestTextDark)
        GestProTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            placeholder = "Repita a nova senha",
            isPassword = true,
            maxLength = Limits.PASSWORD_MAX,
            errorMessage = state.confirmError
        )

        state.errorMessage?.let {
            Text(it, color = GestError, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = viewModel::submit,
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
                Text("Salvar senha", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}