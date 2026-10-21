package com.example.gestpro.ui.resetpassword

import androidx.lifecycle.SavedStateHandle
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

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val confirmError: String? = null,
    val errorMessage: String? = null,
    val submitAttempted: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false
) {
    val passwordRules: List<PasswordRule> get() = Validators.passwordRules(password)
    val showPasswordRules: Boolean get() = password.isNotEmpty() || submitAttempted
}

class ResetPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: UserRepository
) : ViewModel() {

    private val token: String = savedStateHandle.get<String>("token").orEmpty()

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) = _uiState.update {
        val v = value.take(Limits.PASSWORD_MAX)
        it.copy(
            password = v,
            errorMessage = null,
            confirmError = if (it.confirmPassword.isEmpty()) null
            else Validators.confirmPasswordError(v, it.confirmPassword)
        )
    }

    fun onConfirmPasswordChange(value: String) = _uiState.update {
        val v = value.take(Limits.PASSWORD_MAX)
        it.copy(
            confirmPassword = v,
            errorMessage = null,
            confirmError = if (v.isEmpty()) null else Validators.confirmPasswordError(it.password, v)
        )
    }

    fun submit() {
        val s = _uiState.value
        val confirmError = Validators.confirmPasswordError(s.password, s.confirmPassword)
        _uiState.update { it.copy(submitAttempted = true, confirmError = confirmError) }

        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Link inválido") }
            return
        }
        if (!Validators.isPasswordValid(s.password) || confirmError != null) return   // não salva

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.resetPassword(token, s.password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, success = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun onSuccessHandled() = _uiState.update { it.copy(success = false) }
}