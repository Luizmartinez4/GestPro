package com.example.gestpro.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.repository.UserRepository
import com.example.gestpro.util.Limits
import com.example.gestpro.util.PasswordRule
import com.example.gestpro.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val confirmError: String? = null,
    val submitAttempted: Boolean = false,
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false
) {
    // Recalculado a cada dígito digitado
    val passwordRules: List<PasswordRule> get() = Validators.passwordRules(password)
    val showPasswordRules: Boolean get() = password.isNotEmpty() || submitAttempted
}

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update {
        val v = value.take(Limits.EMAIL_MAX)
        // Se já havia erro e o email passou a ser válido, limpa na hora
        val keepError = it.emailError != null && Validators.emailError(v) != null
        it.copy(email = v, emailError = if (keepError) it.emailError else null)
    }

    /** Validação ao sair do campo. */
    fun onEmailBlur() = _uiState.update {
        it.copy(emailError = Validators.emailError(it.email))
    }

    fun onPasswordChange(value: String) = _uiState.update {
        val v = value.take(Limits.PASSWORD_MAX)
        it.copy(
            password = v,
            confirmError = if (it.confirmPassword.isEmpty()) null
            else Validators.confirmPasswordError(v, it.confirmPassword)
        )
    }

    fun onConfirmPasswordChange(value: String) = _uiState.update {
        val v = value.take(Limits.PASSWORD_MAX)
        it.copy(
            confirmPassword = v,
            confirmError = if (v.isEmpty()) null else Validators.confirmPasswordError(it.password, v)
        )
    }

    fun register() {
        val s = _uiState.value
        val emailError = Validators.emailError(s.email)
        val confirmError = Validators.confirmPasswordError(s.password, s.confirmPassword)
        val passwordOk = Validators.isPasswordValid(s.password)

        _uiState.update {
            it.copy(submitAttempted = true, emailError = emailError, confirmError = confirmError)
        }
        if (emailError != null || confirmError != null || !passwordOk) return   // não salva

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.register(s.email, s.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure { e ->
                    // Ex.: "Este email já está cadastrado", exibido abaixo do input de email
                    _uiState.update { it.copy(isLoading = false, emailError = e.message) }
                }
        }
    }

    fun onRegisterHandled() = _uiState.update { it.copy(registerSuccess = false) }
}