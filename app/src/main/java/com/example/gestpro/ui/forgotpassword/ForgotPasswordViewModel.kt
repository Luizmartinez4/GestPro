package com.example.gestpro.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestpro.data.repository.UserRepository
import com.example.gestpro.util.Limits
import com.example.gestpro.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val emailSent: Boolean = false
)

class ForgotPasswordViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update {
        it.copy(email = value.take(Limits.EMAIL_MAX), emailError = null, emailSent = false)
    }

    fun onEmailBlur() = _uiState.update { it.copy(emailError = Validators.emailError(it.email)) }

    fun sendLink() {
        val email = _uiState.value.email
        val error = Validators.emailError(email)
        if (error != null) {
            _uiState.update { it.copy(emailError = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailError = null, emailSent = false) }
            repository.requestPasswordReset(email)
                .onSuccess { _uiState.update { it.copy(isLoading = false, emailSent = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, emailError = e.message) } }
        }
    }
}